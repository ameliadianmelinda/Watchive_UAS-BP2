package com.example.watchive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
=======
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
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
<<<<<<< HEAD

        // Klik Edit Profil
        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Klik Logout
        binding.btnLogout.setOnClickListener {
            // Hapus session
            val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().remove("user_email").apply()

            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()

            // Kembali ke Login dan hapus semua history activity sebelumnya
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun loadUserData() {
        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val email = sharedPref.getString("user_email", null)

        if (email != null) {
            val db = AppDatabase.getDatabase(requireContext())
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(email)
                if (user != null) {
                    binding.tvProfileName.text = user.name
                    binding.tvProfileEmail.text = user.email
=======
        setupLogout()
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
>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
                }
            }
        }
    }

<<<<<<< HEAD
=======
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

>>>>>>> f64f4956950dbb7c1aa94ea6d268a10e174579de
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
