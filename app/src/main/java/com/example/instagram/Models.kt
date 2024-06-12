package com.example.instagram

data class Post(
    private val title: String = "",
    private val content:String="",
    val contentUrl: String = "",
    val likesCount: String = "0",
    val sharesCount: String = "0"
) {
    fun getTitle(): String {
        return title
    }
    fun getcontent(): String {
        return content
    }
}
