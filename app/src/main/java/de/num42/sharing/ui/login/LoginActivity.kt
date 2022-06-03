package de.num42.sharing.ui.login

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import de.num42.sharing.apollo.ApolloInstance
import de.num42.sharing.apolloInstance
import de.num42.sharing.databinding.ActivityLoginBinding
import de.num42.sharing.databinding.ActivityMainBinding
import de.num42.sharing.graphql.LoginMutation
import de.num42.sharing.graphql.MeQuery
import de.num42.sharing.ui.main.ExampleListAdapter
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.register.RegisterActivity

class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var client : ApolloClient
    private lateinit var loginIntent :Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        client = apolloInstance.get()

        loginIntent= Intent(this, RegisterActivity::class.java)

        setSupportActionBar(binding.toolbarLogin.toolbar)

        binding.toolbarLogin.toolbarTitle.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLogin.toolbarRegisterButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener{
            login()
        }
    }

    private fun login(){
        binding.loadingSpinner.visibility= View.VISIBLE
        lifecycleScope.launchWhenResumed {
            val response = try {
                client.mutation(LoginMutation("asol@num42.de","password")).execute()
            }catch (e : ApolloException){
                //Do something with error
                return@launchWhenResumed
            }
            val authentication = response.data?.login
            if(authentication == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                binding.loginErrorText.visibility = View.VISIBLE
                binding.loadingSpinner.visibility= View.GONE
                return@launchWhenResumed
            } else {
                client=apolloInstance.setAuthorization(authentication)
                startActivity(loginIntent)
                println(authentication)
            }
        }
    }

}