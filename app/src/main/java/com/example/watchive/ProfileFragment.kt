package com.example.watchive

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setupLogout()
        applyTheme()
    }

    private fun applyTheme() {
        val sharedPref = requireActivity().getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("isDarkMode", true)
        
        if (!isDarkMode) {
            val context = requireContext()
            val brandColor = ContextCompat.getColor(context, R.color.brand)
            val darkPurple = ContextCompat.getColor(context, R.color.purple_dark)

            binding.root.setBackgroundColor(brandColor)
            
            // Ubah warna semua teks menjadi Ungu Tua
            updateAllTextViewsColor(binding.root as ViewGroup, darkPurple)
            
            // Sesuaikan Card/Kontainer Profil jika ada
            binding.cardProfileContainer.let { card ->
                card.setCardBackgroundColor(Color.parseColor("#80FFFFFF"))
                card.cardElevation = 0f
            }

            // Sesuaikan Tombol Logout (Tetap Ungu Tua atau sesuaikan)
            binding.btnLogout.backgroundTintList = ColorStateList.valueOf(darkPurple)
            binding.btnLogout.setTextColor(Color.WHITE)
        }
    }

    private fun updateAllTextViewsColor(viewGroup: ViewGroup, color: Int) {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(color)
            } else if (child is ViewGroup) {
                updateAllTextViewsColor(child, color)
            }
        }
    }

    private fun loadUserData() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)

        if (userId != -1) {
            val db = AppDatabase.getDatabase(requireContext())
            lifecycleScope.launch {
                val user = db.userDao().getUserById(userId)
                user?.let {
                    binding.tvProfileName.text = it.name
                    binding.tvProfileEmail.text = it.email
                }
            }
        }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun?")
            .setPositiveButton("Ya, Keluar") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun performLogout() {
        val sharedPref = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
