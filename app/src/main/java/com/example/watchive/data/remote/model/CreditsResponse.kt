package com.example.watchive.data.remote.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreditsResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("cast")
    val cast: List<Actor>,

    @SerializedName("crew")
    val crew: List<Crew>?
) : Parcelable

@Parcelize
data class Crew(
    @SerializedName("id") val id: Int,
    @SerializedName("credit_id") val creditId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("job") val job: String?
) : Parcelable
