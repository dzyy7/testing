package id.co.psplauncher.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import id.co.psplauncher.R
import id.co.psplauncher.databinding.ActivityMainBinding
import id.co.psplauncher.ui.fragments.dialog_logout.BottomSheetLogout
import id.co.psplauncher.ui.login.LoginActivity
import id.co.psplauncher.ui.pos.FragmentPos

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigationDrawer()
        setupLogoutBottomSheetListener()
        observeLogoutState()

        if (savedInstanceState == null) {
            loadFragment(FragmentPos())
        }
    }

    private fun setupNavigationDrawer() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_pos -> {
                    loadFragment(FragmentPos())
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }
                R.id.nav_dashboard -> {
                    Toast.makeText(this, "Menu Dashboard Coming Soon!", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_laporan -> {
                    Toast.makeText(this, "Menu Laporan Coming Soon!", Toast.LENGTH_SHORT).show()
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    false
                }
                R.id.nav_logout -> {
                    showLogoutConfirmation()
                    false
                }
                else -> false
            }
        }

        binding.navView.setCheckedItem(R.id.nav_pos)
    }

    private fun observeLogoutState() {
        viewModel.logoutState.observe(this) { isLoggedOut ->
            if (isLoggedOut) {
                navigateToLogin()
            }
        }
    }

    private fun setupLogoutBottomSheetListener() {
        supportFragmentManager.setFragmentResultListener(
            BottomSheetLogout.REQUEST_KEY,
            this
        ) { _, bundle ->
            val shouldLogout = bundle.getBoolean(BottomSheetLogout.RESULT_LOGOUT, false)
            if (shouldLogout) {
                viewModel.logout()
            }
        }
    }

    private fun showLogoutConfirmation() {
        val bottomSheet = BottomSheetLogout.newInstance()
        bottomSheet.show(supportFragmentManager, BottomSheetLogout::class.java.simpleName)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
