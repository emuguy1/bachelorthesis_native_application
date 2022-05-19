package de.num42.sharing.ui.main

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import de.num42.sharing.databinding.ActivityMainBinding
import de.num42.sharing.ui.login.LoginActivity
import de.num42.sharing.ui.register.RegisterActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       setSupportActionBar(binding.toolbarMain.toolbar)

        val textView = binding.mainTextHappy
        val paint = textView.paint
        val width = paint.measureText(textView.text.toString())
        val textShader: Shader = LinearGradient(0f, 0f, width, textView.textSize, intArrayOf(
            Color.parseColor("#0873C8"),
            Color.parseColor("#EE9911EE"),
            Color.parseColor("#E91E63")
        ), null, Shader.TileMode.REPEAT)

        textView.paint.shader = textShader
        
        binding.exampleItemList.setHasFixedSize(true)

            val layout = FlexboxLayoutManager(
            this
        )
        layout.flexDirection=FlexDirection.ROW
        layout.justifyContent= JustifyContent.CENTER
        binding.exampleItemList.layoutManager = layout
        val adapter = ExampleListAdapter(
            this
        )
        binding.exampleItemList.adapter = adapter
        adapter.setList(mutableListOf("Spiel",
            "Bücher",
            "Autoanhänger",
            "Hochdruckreiniger",
            "Beamer",
            "Drohne",
            "Teppichreiniger",
            "Bohrmaschiene",
            "Festbank",
            "Vertikutierer",
            "Fotokamera",
            "Dampfreiniger",
            "Schleifmaschiene",
            "Bohrhammer",
            "Kammeraobjektiv",
            "Stichsäge",
            "Gepäckbox",
            "Umgrabmaschiene"))

        binding.toolbarMain.toolbarLoginButton.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarMain.toolbarRegisterButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



    }

}