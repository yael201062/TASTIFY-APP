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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tastify.R
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.User
import com.example.tastify.databinding.FragmentEditProfileBinding
import com.example.tastify.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel + Repository
    private lateinit var userViewModel: UserViewModel
    private var selectedImageUri: Uri? = null
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // אתחול ה-ViewModel עם UserRepository
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        val userRepository = UserRepository(userDao)

        userViewModel = ViewModelProvider(this, UserViewModel.Factory(userRepository))
            .get(UserViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val userId = firebaseUser.uid  // 🔹 קבלת ה-UID של המשתמש המחובר
            userViewModel.loadUser(userId) // טוען את הנתונים של המשתמש מהמסד

            userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
                user?.let {
                    currentUser = it
                    binding.etUserName.setText(it.name ?: "")
                    binding.etUserEmail.setText(it.email ?: "")
                    if (!it.profileImageUrl.isNullOrEmpty()) {
                        Picasso.get().load(it.profileImageUrl).into(binding.ivProfileImage)
                    } else {
                        binding.ivProfileImage.setImageResource(R.drawable.default_profile)
                    }
                }
            }
        } else {
            Toast.makeText(requireContext(), "שגיאה: אין משתמש מחובר", Toast.LENGTH_SHORT).show()
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun saveUserData() {
        val name = binding.etUserName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        val userId = currentUser?.id ?: return

        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val profileImageUrl = selectedImageUri?.toString() ?: currentUser?.profileImageUrl ?: ""

        val updatedUser = User(id = userId, name = name, email = email, profileImageUrl = profileImageUrl)

        lifecycleScope.launch {
            userViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack() // חזרה לעמוד הפרופיל
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
