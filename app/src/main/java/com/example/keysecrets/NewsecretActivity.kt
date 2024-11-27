package com.example.keysecrets

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsecretActivity : AppCompatActivity() {
    private lateinit var messageText: EditText
    private lateinit var sendButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newsecret)

        messageText = findViewById(R.id.messageText)
        sendButton = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val content = messageText.text.toString().trim()
            if (content.isNotEmpty()) {
                uploadPostToFirestore(content)
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje antes de enviar", Toast.LENGTH_SHORT).show()
            }
        }

        FooterNavigation.setupNavigation(this)
    }

    private fun uploadPostToFirestore(content: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid.toLongOrNull() ?: 0L // Convertir el UID a Long

            // Generar un post_id de tipo numérico, por ejemplo, usando un contador secuencial o un valor aleatorio.
            val postId = (System.currentTimeMillis() % 1000000).toInt() // Un ejemplo de generación secuencial basado en el tiempo
            val createdAt = System.currentTimeMillis() // Este será un valor numérico (milisegundos)

            val post = hashMapOf(
                "post_id" to postId,
                "user_id" to userId,
                "content" to content,
                "image_url" to "foto1",  // Mantenerlo como una cadena
                "created_at" to createdAt
            )

            db.collection("posts")
                .add(post)
                .addOnSuccessListener {
                    Toast.makeText(this, "Secreto subido con éxito", Toast.LENGTH_SHORT).show()
                    messageText.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al subir el secreto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }
}
