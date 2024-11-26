package com.example.keysecrets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.editText2)
        val passwordEditText: EditText = findViewById(R.id.editText)
        val registerButton: Button = findViewById(R.id.button)

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Registrar usuario con Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registro exitoso
                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = when (task.exception) {
                            is FirebaseAuthWeakPasswordException -> "Contraseña demasiado débil"
                            is FirebaseAuthInvalidCredentialsException -> "Correo no válido"
                            is FirebaseAuthUserCollisionException -> "El correo ya está registrado"
                            else -> "Error: ${task.exception?.message}"
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

        }

        // Desde SignUpActivity hacia LoginActivity
        val loginButton: Button = findViewById(R.id.loginButton) // Botón que dirige a Login
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6 // Puedes agregar más reglas
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
