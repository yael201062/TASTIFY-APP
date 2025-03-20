package com.example.tastify.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tastify.data.model.User
import com.example.tastify.databinding.FragmentEditProfileBinding
import com.example.tastify.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.etUserName.setText(user?.name)
            if (!user?.profileImageUrl.isNullOrEmpty()) {
                Picasso.get().load(user?.profileImageUrl).into(binding.ivProfileImage)
            }
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.etUserName.text.toString().trim()
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Toast.makeText(requireContext(), "שגיאה בזיהוי המשתמש", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (name.isBlank()) {
                Toast.makeText(requireContext(), "נא להזין שם", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(id = userId, name = name, profileImageUrl = selectedImageUri?.toString() ?: "")

            lifecycleScope.launch {
                userViewModel.updateUser(user)
                Toast.makeText(requireContext(), "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivProfileImage.setImageURI(selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
