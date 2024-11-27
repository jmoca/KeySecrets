package com.example.keysecrets

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.identity.util.UUID
import com.google.firebase.firestore.FirebaseFirestore

class NewsecretActivity : AppCompatActivity() {
    private lateinit var messageText: EditText
    private lateinit var sendButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newsecret)

        // Referencias a los elementos del layout
        messageText = findViewById(R.id.messageText) // EditText para escribir el mensaje
        sendButton = findViewById(R.id.sendButton)   // Botón para enviar el mensaje

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
        val post = hashMapOf(
            "post_id" to UUID.randomUUID().toString(),
            "user_id" to 1, // Cambia según el usuario actual
            "content" to content,
            "image_url" to "foto1", // Modificar si necesitas subir imágenes
            "created_at" to System.currentTimeMillis()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Secreto subido con éxito", Toast.LENGTH_SHORT).show()
                messageText.text.clear() // Limpia el campo después de subir
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el secreto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}