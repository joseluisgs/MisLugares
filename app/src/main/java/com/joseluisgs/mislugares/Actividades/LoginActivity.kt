package com.joseluisgs.mislugares.Actividades

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.joseluisgs.mislugares.Entidades.Usuarios.Usuario
import com.joseluisgs.mislugares.R
import com.joseluisgs.mislugares.Utilidades.Utils
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private lateinit var Auth: FirebaseAuth
    private lateinit var FireStore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    companion object {
        private const val TAG = "Login"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Servicios de Firebase
        Auth = Firebase.auth
        FireStore = FirebaseFirestore.getInstance()
        // Configuramos el SigIn con google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        initUI()
    }


    private fun initUI() {

        // Datos para no meterlos
        loginProgressBar.visibility = View.INVISIBLE
        loginInputLogin.setText("joseluisgs@mislugares.com")
        loginInputPass.setText("joseluis123")
        loginBoton.setOnClickListener { iniciarSesion() }
        loginTexCreateUser.setOnClickListener { crearUsuario() }
        loginGoogle.setOnClickListener { iniciarSesionGoogle() }

        // primero comprobamos que tengamos conexión
        if (Utils.isNetworkAvailable(applicationContext)) {
            procesarSesiones()
        } else {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                "Es necesaria una conexión a internet",
                Snackbar.LENGTH_INDEFINITE
            )
            snackbar.setActionTextColor(getColor(R.color.colorAccent))
            snackbar.setAction("Conectar") {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            }
            snackbar.show()
        }
    }

    /**
     * Inicia una sesión con Google
     */
    private fun iniciarSesionGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * Activity Sresult de Procesar Login Google
     * @param requestCode Int
     * @param resultCode Int
     * @param data Intent?
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in error", e)
                Toast.makeText(baseContext, "Error: " + e.localizedMessage,
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Login With Google
     * @param idToken String
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        loginProgressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        Auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential: Ok")
                    val user = Auth.currentUser
                    Log.i(TAG, user.toString())
                    Toast.makeText(baseContext, "Auth: Usuario autenticado en Google", Toast.LENGTH_SHORT).show()
                    user?.let { insertarUsuario(it) }
                    abrirPrincipal()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential: Error", task.exception)
                    Toast.makeText(baseContext, "Error: " + task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                }
                loginProgressBar.visibility = View.INVISIBLE
            }
    }

    /**
     * Crea un usuario en Firebase con nombre de usuario, email e imagen
     */
    private fun crearUsuario() {
        // Llamamos a la función para crear usuario
        loginProgressBar.visibility = View.VISIBLE
        Auth.createUserWithEmailAndPassword("joseluisgs@mislugares.com", "joseluis123")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.i(TAG, "createUserWithEmail: Ok")
                    val user = Auth.currentUser
                    // Actualizo su información de perfil
                    actualizarPerfilNuevoUsuario(user)
                    Log.i(TAG, user.toString())
                    Toast.makeText(baseContext, "Auth: Usuario creado con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail: Error", task.exception)
                    Toast.makeText(baseContext, "Error: " + task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT).show()
                    // updateUI(null)
                }
            }
        loginProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Actrualiza la información del usuario
     * @param user FirebaseUser?
     */
    private fun actualizarPerfilNuevoUsuario(user: FirebaseUser?) {
        // Actualiza la información de un nuevo usuario
        // https://firebase.google.com/docs/auth/android/manage-users
        val profileUpdates = userProfileChangeRequest {
            displayName = "José Luis González"
            photoUri = Uri.parse("https://pbs.twimg.com/profile_images/1164967571579396096/YXMN71A1_400x400.jpg")
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i(TAG, "Perfil Actualizado.")
                    insertarUsuario(user)
                }
            }
    }

    /**
     * Inserta un usuario, si existe lo sobre-escribe....
     * @param user FirebaseUser
     */
    private fun insertarUsuario(user: FirebaseUser) {
        val usuario = Usuario(
            id = user.uid,
            nombre = user.displayName.toString(),
            login = user.email.toString(),
            github = "https://github.com/joseluisgs",
            twitter = "https://twitter.com/joseluisgonsan",
            avatar = user.photoUrl.toString(),
            correo = user.email.toString()
        )
        FireStore.collection("usuarios")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener { Log.i(TAG, "Usuario insertado!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error insertar usuario", e) }
    }


    /**
     * Abre la sesión principal
     */
    private fun abrirPrincipal() {
        val main = Intent(this, MainActivity::class.java)
        startActivity(main)
        finish()
    }

    /**
     * Comprueba si hay una sesión activa
     */
    private fun procesarSesiones() {
        // Vemos si hay sesión
        val currentUser = Auth.currentUser
        if (currentUser != null) {
            Log.i(TAG, "SÍ hay sesión activa")
            Toast.makeText(baseContext, "Auth: Sesión activa", Toast.LENGTH_SHORT).show()
            abrirPrincipal()
        } else {
            Log.i(TAG, "NO hay sesión activa")
        }
    }

    /**
     * Inicia una sesion
     * @return Boolean
     */
    private fun iniciarSesion() {
        loginProgressBar.visibility = View.VISIBLE
        if (comprobarFormulario()) {
            Auth.signInWithEmailAndPassword(loginInputLogin.text.toString(), loginInputPass.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i(TAG, "signInWithEmail: OK")
                        val user = Auth.currentUser
                        Log.i(TAG, user.toString())
                        Toast.makeText(baseContext, "Auth: Usuario autentificado con éxito", Toast.LENGTH_SHORT).show()
                        abrirPrincipal()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail: Error", task.exception)
                        Toast.makeText(baseContext, "Error: " + task.exception?.localizedMessage,
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
        loginProgressBar.visibility = View.INVISIBLE
    }

    /**
     * Comprueba que no haya campos nulos
     * @return Boolean
     */
    private fun comprobarFormulario(): Boolean {
        var sal = true
        if (loginInputLogin.text!!.isEmpty()) {
            loginInputLogin.error = "El nombre de usuario no puede estar en blanco"
            sal = false
        }

        if (loginInputPass.text!!.isEmpty()) {
            loginInputPass.error = "La contraseña no puede estar en blanco"
            sal = false
        }
        return sal
    }
}