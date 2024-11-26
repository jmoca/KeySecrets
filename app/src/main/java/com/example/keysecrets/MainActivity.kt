package com.example.keysecrets

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio)

        // Referencia al botón que va a abrir SignUpActivity
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        signUpButton.setOnClickListener {
            try {
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}
