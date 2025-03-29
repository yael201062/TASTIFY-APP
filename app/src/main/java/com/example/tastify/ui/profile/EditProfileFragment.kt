package com.example.tastify.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
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
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private var selectedImageUri: Uri? = null
    private var currentUser: User? = null
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            val userId = firebaseUser.uid
            userViewModel.loadUser(userId)

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
            showImagePickerOptions()
        }

        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("בחר מהגלריה", "צלם תמונה")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("בחר תמונת פרופיל")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectImageFromGallery()
                    1 -> takePhotoWithCamera()
                }
            }.show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 1001)
    }

    private fun takePhotoWithCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            photoFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        }
        startActivityForResult(intent, 1002)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp.jpg"
        val storageDir = requireContext().cacheDir
        return File(storageDir, fileName)
    }

    private fun saveUserData() {
        val name = binding.etUserName.text.toString().trim()
        val email = binding.etUserEmail.text.toString().trim()
        val userId = currentUser?.id ?: return

        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val localUri = selectedImageUri ?: photoUri

        lifecycleScope.launch {
            val imageUrl = if (localUri != null) {
                uploadImageToFirebase(localUri, userId)
            } else {
                currentUser?.profileImageUrl
            }

            val updatedUser = User(
                id = userId,
                name = name,
                email = email,
                profileImageUrl = imageUrl
            )

            userViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "הפרופיל עודכן בהצלחה", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri, userId: String): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("profile_images/${userId}_${System.currentTimeMillis()}.jpg")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivProfileImage.setImageURI(selectedImageUri)
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            photoUri?.let {
                binding.ivProfileImage.setImageURI(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
