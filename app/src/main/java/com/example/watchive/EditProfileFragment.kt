package com.example.watchive

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.watchive.data.local.AppDatabase
import com.example.watchive.databinding.FragmentEditProfileBinding
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentEmail = sharedPref.getString("user_email", null)

        val db = AppDatabase.getDatabase(requireContext())

        if (currentEmail != null) {
            lifecycleScope.launch {
                val user = db.userDao().getUserByEmail(currentEmail)
                user?.let {
                    binding.etEditName.setText(it.name)
                    binding.etEditEmail.setText(it.email)
                    // Kita tidak menampilkan password lama demi keamanan, 
                    // namun kolom password baru sudah disiapkan di XML.
                }
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString()
            val newEmail = binding.etEditEmail.text.toString()
            val newPassword = binding.etEditPassword.text.toString()

            if (newName.isNotEmpty() && newEmail.isNotEmpty() && currentEmail != null) {
                lifecycleScope.launch {
                    val user = db.userDao().getUserByEmail(currentEmail)
                    if (user != null) {
                        // Jika password baru diisi, gunakan yang baru. Jika kosong, tetap gunakan yang lama.
                        val finalPassword = if (newPassword.isNotEmpty()) newPassword else user.password
                        
                        val updatedUser = user.copy(
                            name = newName, 
                            email = newEmail,
                            password = finalPassword
                        )
                        
                        db.userDao().updateUser(updatedUser)
                        
                        sharedPref.edit().putString("user_email", newEmail).apply()

                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                            findNavController().navigateUp()
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Harap isi Nama dan Email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
