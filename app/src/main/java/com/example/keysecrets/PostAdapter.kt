package com.example.keysecrets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.tv_content)

        val createdAt: TextView = view.findViewById(R.id.tv_created_at)
        val likeButton: ImageView = view.findViewById(R.id.iv_like)
        val likeCount: TextView = view.findViewById(R.id.tv_like_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }


    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.content.text = post.content
        holder.createdAt.text = "Created at: ${formatter.format(post.created_at)}"

        // Contar los likes de este post en Firestore
        db.collection("likes")
            .whereEqualTo("post_id", post.post_id)
            .get()
            .addOnSuccessListener { result ->
                val likesCount = result.size() // El número de documentos en la consulta es el número de likes
                holder.likeCount.text = likesCount.toString() // Actualiza la UI con la cantidad de likes
                post.likes = likesCount // Actualiza el objeto 'post' con el número de likes
            }
            .addOnFailureListener { e ->
                Toast.makeText(holder.itemView.context, "Error al obtener los likes: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        holder.likeButton.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val likeRef = db.collection("likes").document("${post.post_id}_$userId")

                likeRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Eliminar el like
                        likeRef.delete().addOnSuccessListener {
                            post.likes -= 1
                            updateLikesInFirestore(post, holder) // Actualizar la base de datos
                            holder.likeButton.setImageResource(R.drawable.corazon_vacio)
                        }
                            .addOnFailureListener { e ->
                                Toast.makeText(holder.itemView.context, "Error al quitar el like: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Añadir el like
                        val likeData = hashMapOf(
                            "post_id" to post.post_id,
                            "user_id" to userId,
                            "created_at" to System.currentTimeMillis()
                        )
                        likeRef.set(likeData).addOnSuccessListener {
                            post.likes += 1
                            updateLikesInFirestore(post, holder) // Actualizar la base de datos
                            holder.likeButton.setImageResource(R.drawable.corazon)
                        }
                            .addOnFailureListener { e ->
                                Toast.makeText(holder.itemView.context, "Error al dar like: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Error al verificar el like: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun updateLikesInFirestore(post: Post, holder: PostViewHolder) {
        val postRef = db.collection("posts").document(post.post_id.toString())


        // Intentamos obtener el documento primero
        postRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Si el documento existe, actualizamos los likes
                postRef.update("likes", post.likes)
                    .addOnSuccessListener {
                        holder.likeCount.text = post.likes.toString() // Actualizar el contador de likes en la UI
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Error al actualizar los likes: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Si el documento no existe, lo creamos con el número de likes inicial
                val newPost = hashMapOf(
                    "post_id" to post.post_id,
                    "likes" to post.likes // Iniciar con el número de likes actual
                )
                postRef.set(newPost)
                    .addOnSuccessListener {
                        holder.likeCount.text = post.likes.toString() // Actualizar el contador de likes en la UI
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(holder.itemView.context, "Error al crear el post: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
            .addOnFailureListener { e ->
                Toast.makeText(holder.itemView.context, "Error al obtener el documento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }





    override fun getItemCount(): Int = posts.size
}
