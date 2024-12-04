package com.example.keysecrets

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class MainpageActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: PostAdapter
    private lateinit var drawerLayout: DrawerLayout
    private var sortByLikes: Boolean = false // Bandera para alternar el orden

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainpage)

        // Configurar Toolbar y DrawerLayout
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(android.R.drawable.ic_menu_revert) // O cualquier otro ícono de menú

        supportActionBar?.setDisplayShowTitleEnabled(false)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Mientras se desliza el menú
            }

            override fun onDrawerOpened(drawerView: View) {
                findViewById<RecyclerView>(R.id.rv_posts).isEnabled = false
            }

            override fun onDrawerClosed(drawerView: View) {
                // Menú cerrado, habilitar interacción con el RecyclerView
                findViewById<RecyclerView>(R.id.rv_posts).isEnabled = true
                drawerLayout.requestFocus()
            }

            override fun onDrawerStateChanged(newState: Int) {
                // Cambios de estado en el DrawerLayout
            }
        })
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainpageActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START) // Abrir el menú
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Cerrar el menú si está abierto
        } else {
            super.onBackPressed()
        }
    }
}
