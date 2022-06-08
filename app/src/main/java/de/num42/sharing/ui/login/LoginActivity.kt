package de.num42.sharing.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import de.num42.sharing.apolloInstance
import de.num42.sharing.databinding.ActivityLoginBinding
import de.num42.sharing.graphql.LoginMutation
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.profile.ProfileActivity
import de.num42.sharing.ui.register.RegisterActivity


class LoginActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var client : ApolloClient
    private lateinit var loginIntent :Intent
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("SharingSharedPref", MODE_PRIVATE)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        client = apolloInstance.get()

        loginIntent= Intent(this, ProfileActivity::class.java)

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
                val email = binding.EmailInput.text.toString()
                val password = binding.inputPassword.text.toString()
                client.mutation(LoginMutation(email,password)).execute()
            }catch (e : ApolloException){
                binding.loadingSpinner.visibility= View.INVISIBLE
                binding.loginErrorText.visibility= View.VISIBLE
                binding.loginErrorText.text = "Es konnte keine Verbindung hergestellt werden. Versuchen sie es später noch einmal."
                return@launchWhenResumed
            }
            val authentication = response.data?.login
            if(authentication == null || response.hasErrors()){
                println(response.errors?.get(0)?.message)
                binding.loginErrorText.visibility = View.VISIBLE
                binding.loginErrorText.text = response.errors?.get(0)?.message
                binding.loadingSpinner.visibility= View.GONE
                return@launchWhenResumed
            } else {
                sharedPreferences.edit()
                    .putString("authentication",authentication)
                    .apply()
                client=apolloInstance.setAuthorization(authentication)
                startActivity(loginIntent)
                println(authentication)
            }
        }
    }

}