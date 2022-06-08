package de.num42.sharing.ui.messages
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.num42.sharing.databinding.ActivityProfileBinding
import de.num42.sharing.ui.main.MainActivity
import de.num42.sharing.ui.message.MessageActivity
import de.num42.sharing.ui.profile.ProfileActivity


class MessagesActivity: AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarLoggedIn.toolbar)

        binding.toolbarLoggedIn.toolbarTitle.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLoggedIn.toolbarMessagesButton.setOnClickListener{
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        binding.toolbarLoggedIn.toolbarLogoutButton.setOnClickListener{
            //Logout
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }




    }
}