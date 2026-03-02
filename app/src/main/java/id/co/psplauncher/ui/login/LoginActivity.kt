package id.co.psplauncher.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.Utils.snackbar
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
        setupInputListeners()
    }

    // Hapus error saat user mulai mengetik
    private fun setupInputListeners() {
        binding.textInputUsername.editText?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textInputUsername.error = null
                binding.textInputUsername.isErrorEnabled = false
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })

        binding.textInputPassword.editText?.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.textInputPassword.error = null
                binding.textInputPassword.isErrorEnabled = false
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
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
            binding.textInputUsername.isErrorEnabled = true
            binding.textInputUsername.error = "Username tidak boleh kosong"
            binding.textInputUsername.requestFocus()
            isValid = false
        } else {
            binding.textInputUsername.error = null
            binding.textInputUsername.isErrorEnabled = false
        }

        if (password.isNullOrEmpty()) {
            binding.textInputPassword.isErrorEnabled = true
            binding.textInputPassword.error = "Password tidak boleh kosong"
            if (isValid) binding.textInputPassword.requestFocus()
            isValid = false
        } else if (password.length < 6) {
            binding.textInputPassword.isErrorEnabled = true
            binding.textInputPassword.error = "Password minimal 6 karakter"
            if (isValid) binding.textInputPassword.requestFocus()
            isValid = false
        } else {
            binding.textInputPassword.error = null
            binding.textInputPassword.isErrorEnabled = false
        }

        return isValid
    }

    private fun observeLoginState() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Idle -> resetButton()
                is LoginState.Loading -> {
                    binding.btnLogin.text = ""
                    binding.btnLogin.isClickable = false
                    binding.progressLogin.visibility = View.VISIBLE
                }
                is LoginState.Success -> {
                    resetButton()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is LoginState.NotCashier -> {
                    resetButton()
                    binding.textInputUsername.isErrorEnabled = true
                    binding.textInputUsername.error = "Akun ini bukan kasir"
                }
                is LoginState.Error -> {
                    resetButton()
                    binding.textInputPassword.error = state.message
                }
            }
        }
    }

    private fun checkNotCashierExtra() {
        if (intent.getBooleanExtra("NOT_CASHIER", false)) {
            binding.textInputUsername.isErrorEnabled = true
            binding.textInputUsername.error = "Akun ini bukan kasir"
        }
    }

    private fun resetButton() {
        binding.btnLogin.text = "Login"
        binding.btnLogin.isEnabled = true
        binding.btnLogin.isClickable = true
        binding.progressLogin.visibility = View.GONE
    }
}