package com.example.keysecrets

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.keysecrets.databinding.ItemPostBinding

class PostAdapterBuscador(private var posts: List<Post>) :
    RecyclerView.Adapter<PostAdapterBuscador.PostViewHolder>() {

    inner class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvContent.text = post.content
            binding.tvUserId.text = binding.root.context.getString(R.string.user_id_text, post.user_id)
            binding.tvCreatedAt.text = binding.root.context.getString(R.string.created_at_text, post.created_at)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Log.d("PostAdapterBuscador", "Binding post en posición $position: ${posts[position]}")
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<Post>) {
        Log.d("PostAdapterBuscador", "Actualizando posts con ${newPosts.size} elementos")

        posts = newPosts
        notifyDataSetChanged()
    }
}
