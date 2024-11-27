package com.example.keysecrets

data class Post(
    val post_id: Long? = null,
    val user_id: Long? = null,
    val content: String = "",
    val image_url: String = "",
    val created_at: Long = 0L
)
