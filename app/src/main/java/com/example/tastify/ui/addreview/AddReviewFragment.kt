package com.example.tastify.ui.addreview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tastify.data.model.Review
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.databinding.FragmentAddReviewBinding
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddReviewFragment : Fragment() {

    private var _binding: FragmentAddReviewBinding? = null
    private val binding get() = _binding!!

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository())
    }

    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)

        binding.btnSelectImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnSubmitReview.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Toast.makeText(requireContext(), "לא ניתן להוסיף ביקורת - אין משתמש מחובר", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val restaurantName = binding.etRestaurantName.text.toString().trim()
            val comment = binding.etReviewContent.text.toString().trim()
            val rating = binding.ratingBar.rating

            if (restaurantName.isEmpty() || comment.isEmpty()) {
                Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageAndAddReview(restaurantName, comment, rating, currentUser.uid)
        }

        return binding.root
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("בחר מהגלריה", "צלם תמונה")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("הוספת תמונה")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> selectImageFromGallery()
                    1 -> takePhotoWithCamera()
                }
            }
            .show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2001)
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
        startActivityForResult(intent, 2002)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timestamp.jpg"
        val storageDir = requireContext().cacheDir
        return File(storageDir, fileName)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                2001 -> {
                    selectedImageUri = data?.data
                    binding.ivSelectedImage.setImageURI(selectedImageUri)
                }
                2002 -> {
                    selectedImageUri = photoUri
                    binding.ivSelectedImage.setImageURI(photoUri)
                }
            }
        }
    }

    private fun uploadImageAndAddReview(restaurantName: String, comment: String, rating: Float, userId: String) {
        if (selectedImageUri == null) {
            saveReviewToFirestore(userId, restaurantName, comment, rating, null)
            return
        }

        val fileName = "review_images/${userId}_${System.currentTimeMillis()}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child(fileName)

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    saveReviewToFirestore(userId, restaurantName, comment, rating, downloadUrl)
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "שגיאה בקבלת קישור התמונה", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "שגיאה בהעלאת תמונה", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReviewToFirestore(userId: String, restaurantName: String, comment: String, rating: Float, imageUrl: String?) {
        val review = Review(
            restaurantId = restaurantName,
            userId = userId,
            comment = comment,
            rating = rating,
            imagePath = imageUrl
        )

        reviewViewModel.addReview(review)
        Toast.makeText(requireContext(), "הביקורת נוספה בהצלחה", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
