package com.example.watchive.data.remote.model

import com.google.gson.annotations.SerializedName

data class Actor(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    // The path to the actor's profile picture
    @SerializedName("profile_path")
    val profilePath: String?
)
