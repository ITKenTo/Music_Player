package com.example.music_app.model

data class MusicFileModel(
    val path:String,
    val title:String,
    val artist:String,
    val duration:String?
) {
}