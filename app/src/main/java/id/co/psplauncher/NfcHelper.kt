package id.co.psplauncher

import android.app.Activity
import android.content.Intent
import android.nfc.NfcAdapter
import id.co.psplauncher.ui.payment.card.CardPayment

/**
 * Call this from MainActivity.onNewIntent() to forward NFC intents
 * to the currently active CardPayment fragment.
 *
 * Usage in MainActivity:
 *
 *   override fun onNewIntent(intent: Intent) {
 *       super.onNewIntent(intent)
 *       NfcHelper.handleNfcIntent(this, intent)
 *   }
 */
object NfcHelper {

    fun handleNfcIntent(activity: Activity, intent: Intent) {
        val action = intent.action ?: return
        if (action != NfcAdapter.ACTION_TAG_DISCOVERED &&
            action != NfcAdapter.ACTION_NDEF_DISCOVERED &&
            action != NfcAdapter.ACTION_TECH_DISCOVERED
        ) return

        // Find the active CardPayment fragment and forward the intent
        val fm = (activity as? androidx.fragment.app.FragmentActivity)
            ?.supportFragmentManager ?: return

        // Check all fragments in the back stack for CardPayment
        for (i in 0 until fm.backStackEntryCount + 1) {
            val fragment = if (i == fm.backStackEntryCount) {
                fm.fragments.lastOrNull()
            } else {
                fm.fragments.getOrNull(i)
            }
            if (fragment is CardPayment) {
                fragment.onNfcIntent(intent)
                return
            }
            fragment?.childFragmentManager?.fragments?.forEach { child ->
                if (child is CardPayment) {
                    child.onNfcIntent(intent)
                    return
                }
            }
        }
    }
}