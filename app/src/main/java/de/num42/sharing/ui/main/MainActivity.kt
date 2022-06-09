package de.num42.sharing.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.okHttpClient
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import de.num42.sharing.SharingApplication
import de.num42.sharing.apollo.ApolloInstance
import de.num42.sharing.apolloInstance
import de.num42.sharing.databinding.ActivityMainBinding
import de.num42.sharing.graphql.MeQuery
import de.num42.sharing.ui.login.LoginActivity
import de.num42.sharing.ui.profile.ProfileActivity
import de.num42.sharing.ui.register.RegisterActivity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var client : ApolloClient
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("SharingSharedPref", MODE_PRIVATE)

        if(sharedPreferences.contains("authentication")){
            val authenticationString = sharedPreferences.getString("authentication","")?:""
            client=apolloInstance.setAuthorization(authenticationString)
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarMain.toolbar)

        client = apolloInstance.get()

        println("Test--------"+apolloInstance.BASE_URL)

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

        getPersonData()

    }

    private fun getPersonData(){
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.query(MeQuery()).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val me = response.data?.me
            if(me == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                return@launchWhenResumed
            } else {
                println("Test--------"+me)
            }
        }
    }

}