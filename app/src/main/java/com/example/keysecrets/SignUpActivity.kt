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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.editText2)
        val passwordEditText: EditText = findViewById(R.id.editText)
        val registerButton: Button = findViewById(R.id.button)
        val loginButton: Button = findViewById(R.id.loginButton)

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

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val user = hashMapOf(
                            "user_id" to System.currentTimeMillis(),  // Generar ID numérico único
                            "username" to email.substringBefore("@"), // Nombre de usuario basado en el correo
                            "password" to password,                   // Nota: Evitar en producción
                            "created_at" to Date()                    // Fecha de creación
                        )

                        userId?.let {
                            db.collection("users").document(it)
                                .set(user)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuario registrado y guardado con éxito", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainpageActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar el usuario en Firestore: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        }
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

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainpageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
