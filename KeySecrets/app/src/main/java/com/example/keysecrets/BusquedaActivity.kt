package com.example.keysecrets

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.keysecrets.databinding.BusquedaBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BusquedaActivity : AppCompatActivity() {
    private lateinit var binding: BusquedaBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postAdapter: PostAdapterBuscador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configurar View Binding
        binding = BusquedaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firestore y RecyclerView
        firestore = FirebaseFirestore.getInstance()
        postAdapter = PostAdapterBuscador(emptyList())

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPosts.adapter = postAdapter

        // Configurar el buscador usando el EditText dentro de search_bar
        binding.searchBarEdittext.setOnEditorActionListener { v, _, _ ->
            val query = (v as? TextView)?.text.toString().trim()
            Log.d("BusquedaActivity", "Texto ingresado en el buscador: $query")
            if (query.isNotEmpty()) {
                searchPosts(query)
            }
            true
        }

        // Configurar el listener para el ImageView que limpia la búsqueda
        binding.clearSearch.setOnClickListener {
            binding.searchBarEdittext.text.clear()  // Limpiar el texto del EditText
            // Limpiar los resultados de búsqueda
            postAdapter.updatePosts(emptyList())
            // Ocultar el mensaje de "No hay resultados"
            binding.noResultsMessage.visibility = android.view.View.GONE
        }

        // Configurar Footer Navigation
        FooterNavigation.setupNavigation(this)
    }

    private fun searchPosts(query: String) {
        Log.d("BusquedaActivity", "Iniciando búsqueda para query: $query")

        firestore.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                // Filtrar los posts que contienen la consulta
                val posts = result.mapNotNull { it.toObject(Post::class.java) }
                    .filter { it.content.contains(query, ignoreCase = true) }

                // Actualizar la lista de posts
                postAdapter.updatePosts(posts)

                // Verificar si se encontraron resultados
                if (posts.isEmpty()) {
                    // Mostrar el mensaje de "No hay publicaciones"
                    binding.noResultsMessage.visibility = android.view.View.VISIBLE
                } else {
                    // Ocultar el mensaje si hay resultados
                    binding.noResultsMessage.visibility = android.view.View.GONE
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

}
