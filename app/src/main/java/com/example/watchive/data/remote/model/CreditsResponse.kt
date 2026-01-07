package com.example.watchive.data.remote.model

import com.google.gson.annotations.SerializedName

data class CreditsResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<Actor>
)
