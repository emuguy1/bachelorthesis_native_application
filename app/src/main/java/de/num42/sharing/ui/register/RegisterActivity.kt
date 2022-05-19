package de.num42.sharing.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.num42.sharing.databinding.ActivityLoginBinding
import de.num42.sharing.databinding.ActivityRegisterBinding
import de.num42.sharing.ui.login.LoginActivity
import de.num42.sharing.ui.main.MainActivity

class RegisterActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLogin.toolbar)

        binding.toolbarLogin.toolbarTitle.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLogin.toolbarLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //TODO: Link to html created View
        binding.dataAcceptenceCheckbox.makeLinks(Pair("Datenschutzerklärung", View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }))

        binding.termsOfUsageCheckbox.makeLinks(Pair("Nutzungsbedingungen", View.OnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }))



    }
}


//As suggested here: https://stackoverflow.com/questions/10696986/how-to-set-the-part-of-the-text-view-is-clickable
fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
//      if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}