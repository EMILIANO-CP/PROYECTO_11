package fes.aragon.oauth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import fes.aragon.oauth.databinding.ActivityOpcionesBinding

enum class TipoProvedor {
    CORREO,
    GOOGLE,
    FACEBOOK
}

class OpcionesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcionesBinding
    private lateinit var googleSignInOption: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //datos que manda la actividad
        var bundle: Bundle? = intent.extras
        var email: String? = bundle?.getString("email")
        var provedor: String? = bundle?.getString("provedor")
        inicio(email ?: "", provedor ?: "")

        //GUARDAR DATOS SESION
        val preferencias =
            getSharedPreferences(getString(R.string.file_preferencia), Context.MODE_PRIVATE).edit()
        preferencias.putString("email", email)
        preferencias.putString("provedor", provedor)
        preferencias.apply()
    }

    private fun inicio(email: String, provedor: String) {
        binding.mail.text = email
        binding.provedor.text = provedor

        binding.closeSesion.setOnClickListener {
            val preferencias = getSharedPreferences(
                getString(R.string.file_preferencia),
                Context.MODE_PRIVATE
            ).edit()
            preferencias.clear()
            preferencias.apply()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        //google
        if (provedor == TipoProvedor.GOOGLE.name) {
            googleSignInOption =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
            val data = GoogleSignIn.getLastSignedInAccount(this)
            if (data != null) {
                Picasso.get().load(data.photoUrl).into(binding.img)
            }
        }
    }
}
