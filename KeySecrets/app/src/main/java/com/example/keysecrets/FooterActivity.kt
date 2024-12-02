package com.example.keysecrets

import android.app.Activity
import android.content.Intent
import android.widget.ImageView

object FooterNavigation {

    fun setupNavigation(activity: Activity) {
        // Obtén las ImageView por su ID
        val keyImageView = activity.findViewById<ImageView>(R.id.keyImageView)
        val nuevoImageView = activity.findViewById<ImageView>(R.id.nuevoImageView)
        val busquedaImageView = activity.findViewById<ImageView>(R.id.busquedaImageView)

        // Configura los clics para cada ImageView
        keyImageView?.setOnClickListener {
            val intent = Intent(activity, MainpageActivity::class.java)
            activity.startActivity(intent)
        }

        nuevoImageView?.setOnClickListener {
            val intent = Intent(activity, NewsecretActivity::class.java)
            activity.startActivity(intent)
        }

        busquedaImageView?.setOnClickListener {
            val intent = Intent(activity, BusquedaActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
