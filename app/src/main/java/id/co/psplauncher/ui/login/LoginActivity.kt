package id.co.psplauncher.ui.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.Utils.handleApiError
import id.co.psplauncher.Utils.snackbar
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.databinding.ActivityLoginPageBinding
import id.co.psplauncher.ui.main.MainActivity

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPageBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupLoginButton()
        observeLoginState()
        checkNotCashierExtra()
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val username = binding.textInputUsername.editText?.text?.toString()?.trim()
            val password = binding.textInputPassword.editText?.text?.toString()?.trim()
            
            if (validateInput(username, password)) {
                viewModel.login(username!!, password!!, "", "Jakarta")
            }
        }
    }

    private fun validateInput(username: String?, password: String?): Boolean {
        var isValid = true
        
        if (username.isNullOrEmpty()) {
            binding.textInputUsername.error = "Username is required"
            isValid = false
        } else {
            binding.textInputUsername.error = null
        }

        if (password.isNullOrEmpty()) {
            binding.textInputPassword.error = "Password is required"
            isValid = false
        } else {
            binding.textInputPassword.error = null
        }

        return isValid
    }

    private fun observeLoginState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> {
                    resetButton()
                }
                is LoginState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.btnLogin.isClickable = false
                }
                is LoginState.Success -> {
                    resetButton()
                    binding.root.snackbar("Login Success")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginState.NotCashier -> {
                    resetButton()
                    binding.root.snackbar("Hanya kasir yang bisa masuk")
                }
                is LoginState.Error -> {
                    resetButton()
                    binding.root.snackbar(state.message)
                }
            }
        }
    }

    private fun checkNotCashierExtra() {
        if (intent.getBooleanExtra("NOT_CASHIER", false)) {
            binding.root.snackbar("Hanya kasir yang bisa masuk")
        }
    }

    private fun resetButton() {
        binding.btnLogin.text = "Login"
        binding.btnLogin.isEnabled = true
        binding.btnLogin.isClickable = true
    }
}
