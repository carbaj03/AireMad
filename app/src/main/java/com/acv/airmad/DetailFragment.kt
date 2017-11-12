package com.acv.airmad

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.View.GONE
import android.widget.Toast
import kotlinx.android.synthetic.main.collapsing_toolbar.*
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : BaseFragment() {
    override fun getLayout() =
            R.layout.fragment_detail

    override fun onCreate() {
        configToolbar("Android Developer")
appbar.visibility = GONE
        btnEmail click { sendEmail() }
    }

    private fun sendEmail() =
            with(Intent(Intent.ACTION_SEND)) {
                data = Uri.parse("mailto:")
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, "asdfsdf@sdfs.com")
                putExtra(Intent.EXTRA_CC, "CC")
                putExtra(Intent.EXTRA_SUBJECT, "Your subject")
                putExtra(Intent.EXTRA_TEXT, "Email message goes here")
                try {
                    startActivity(Intent.createChooser(this, "Send mail..."))
                } catch (ex: ActivityNotFoundException) {
                    Toast.makeText(activity, "There is no email client installed.", Toast.LENGTH_SHORT).show()
                }
            }
}
