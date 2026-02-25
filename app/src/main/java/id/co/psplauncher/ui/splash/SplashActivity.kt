package id.co.psplauncher.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.databinding.ActivitySplashBinding
import id.co.psplauncher.ui.login.LoginActivity
import id.co.psplauncher.ui.main.MainActivity

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeAuthState()
        viewModel.checkAuth()
    }

    private fun observeAuthState() {
        viewModel.authState.observe(this) { state ->
            when (state) {
                is AuthState.Authenticated -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                is AuthState.Unauthenticated -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                is AuthState.NotCashier -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("NOT_CASHIER", true)
                    startActivity(intent)
                    finish()
                }
                is AuthState.Loading -> {
                    // Still loading, wait
                }
            }
        }
    }
}
