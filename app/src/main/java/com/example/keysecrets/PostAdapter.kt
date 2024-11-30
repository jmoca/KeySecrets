package com.example.keysecrets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        val formatter = SimpleDateFormat("dd/MM/yyyy ", Locale.getDefault())

        holder.content.text = post.content

        holder.createdAt.text = "Created at: ${formatter.format(post.created_at)}"
        holder.likeCount.text = post.likes.toString()

        holder.likeButton.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val likeRef = db.collection("likes").document("${post.post_id}_$userId")

                likeRef.get().addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Eliminar el like
                        likeRef.delete().addOnSuccessListener {
                            post.likes -= 1
                            db.collection("posts").document(post.post_id.toString())
                                .update("likes", post.likes)
                            holder.likeCount.text = post.likes.toString()
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
                            db.collection("posts").document(post.post_id.toString())
                                .update("likes", post.likes)
                            holder.likeCount.text = post.likes.toString()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = posts.size
}
