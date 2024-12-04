package com.example.keysecrets

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MainpageActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PostAdapter
    private var sortByLikes: Boolean = false // Bandera para alternar el orden

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
                sortPosts(posts) // Ordenar antes de actualizar la vista
                adapter.notifyDataSetChanged()
            }

        // Configurar botón de ordenamiento
        val sortButton = findViewById<Button>(R.id.btn_sort)
        sortButton.setOnClickListener {
            sortByLikes = !sortByLikes // Alternar bandera
            sortPosts(posts) // Reordenar lista según la bandera
            adapter.notifyDataSetChanged() // Notificar cambios
            sortButton.text = if (sortByLikes) "Ordenado por: Likes" else "Ordenado por: Fecha"
        }

        FooterNavigation.setupNavigation(this)
    }

    private fun sortPosts(posts: MutableList<Post>) {
        if (sortByLikes) {
            posts.sortByDescending { it.likes } // Ordenar por likes (descendente)
        } else {
            posts.sortByDescending { it.created_at } // Ordenar por fecha usando el timestamp
        }
    }


}