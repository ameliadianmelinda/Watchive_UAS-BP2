package com.example.watchive.ui.detail

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.watchive.R
import com.example.watchive.databinding.ItemActorBinding
import com.example.watchive.data.remote.model.Actor

class ActorAdapter(private var actors: List<Actor>) : RecyclerView.Adapter<ActorAdapter.ActorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val binding = ItemActorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        holder.bind(actors[position])
    }

    override fun getItemCount(): Int = actors.size

    fun updateData(newActors: List<Actor>) {
        actors = newActors
        notifyDataSetChanged()
    }

    class ActorViewHolder(private val binding: ItemActorBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(actor: Actor) {
            val context = itemView.context
            binding.tvActorName.text = actor.name
            binding.tvActorRole.text = ""
            val imageUrl = actor.profilePath?.let { "https://image.tmdb.org/t/p/w200$it" }
            binding.imgActor.load(imageUrl) {
                crossfade(true)
                placeholder(R.drawable.login_bg_gradient)
                error(R.drawable.login_bg_gradient)
                transformations(RoundedCornersTransformation(36f))
            }

            // LOGIKA TEMA GLOBAL
            val sharedPref = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
            val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
            
            if (!isDarkMode) {
                // LIGHT MODE (Matahari Aktif)
                val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)
                binding.tvActorName.setTextColor(darkPurple)
            } else {
                // DARK MODE
                binding.tvActorName.setTextColor(Color.WHITE)
            }
        }
    }
}
