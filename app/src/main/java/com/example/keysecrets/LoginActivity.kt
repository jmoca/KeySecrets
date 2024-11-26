package com.example.keysecrets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.emailOrPhoneEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isEmailValid(email)) {
                Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Iniciar sesión con Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Login exitoso
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthInvalidUserException -> "Usuario no encontrado"
                            is FirebaseAuthInvalidCredentialsException -> "Credenciales incorrectas"
                            else -> "Error: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

        }

        // Desde LoginActivity hacia SignUpActivity
        val signUpButton: Button = findViewById(R.id.signUpButton) // Botón que dirige a SignUp
        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuario autenticado, redirigir al dashboard
            val intent = Intent(this, MainActivity::class.java) // Dashboard o actividad principal
            startActivity(intent)
            finish() // Evitar que el usuario regrese al login/registro
        }
    }

}
