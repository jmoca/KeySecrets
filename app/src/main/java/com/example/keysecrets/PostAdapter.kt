package com.example.keysecrets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.tv_content)
        val userId: TextView = view.findViewById(R.id.tv_user_id)
        val createdAt: TextView = view.findViewById(R.id.tv_created_at)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.content.text = post.content
        holder.userId.text = "User ID: ${post.user_id}"
        holder.createdAt.text = "Created at: ${formatter.format(post.created_at)}"
    }

    override fun getItemCount(): Int = posts.size
}


