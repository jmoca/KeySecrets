package com.example.keysecrets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainpageActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        // Configurar RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rv_posts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val posts = mutableListOf<Post>()
        adapter = PostAdapter(posts)
        recyclerView.adapter = adapter

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()

        // Escuchar datos de Firestore
        db.collection("posts")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    println("Error al leer datos: $e")
                    return@addSnapshotListener
                }
                posts.clear()
                for (doc in snapshot!!) {
                    val post = doc.toObject(Post::class.java)
                    posts.add(post)
                }
                adapter.notifyDataSetChanged()
            }

        FooterNavigation.setupNavigation(this)
    }
}


