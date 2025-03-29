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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.data.model.Review
import com.example.tastify.databinding.FragmentEditReviewBinding
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditReviewFragment : Fragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!

    private val args: EditReviewFragmentArgs by navArgs()
    private var currentReview: Review? = null

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository())
    }

    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reviewId = args.reviewId

        reviewViewModel.getReviewById(reviewId) { review ->
            if (review != null) {
                currentReview = review
                binding.etRestaurantName.setText(review.restaurantId)
                binding.etReviewContent.setText(review.comment)
                binding.ratingBar.rating = review.rating

                // טען תמונה קיימת אם יש
                if (!review.imagePath.isNullOrEmpty()) {
                    Picasso.get().load(review.imagePath).into(binding.ivSelectedImage)
                }
            } else {
                Toast.makeText(requireContext(), "לא נמצאה ביקורת", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.btnSaveReview.setOnClickListener {
            saveReview()
        }

        binding.btnDeleteReview.setOnClickListener {
            deleteReview()
        }

        binding.btnSelectImage.setOnClickListener {
            showImagePickerDialog()
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("בחר מהגלריה", "צלם תמונה")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("בחר תמונה")
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

    private fun saveReview() {
        val restaurantName = binding.etRestaurantName.text.toString().trim()
        val comment = binding.etReviewContent.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (restaurantName.isBlank() || comment.isBlank()) {
            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val localUri = selectedImageUri ?: photoUri

        lifecycleScope.launch {
            val imageUrl = if (localUri != null) {
                uploadImageToFirebase(localUri)
            } else {
                currentReview?.imagePath
            }

            val updatedReview = currentReview?.copy(
                restaurantId = restaurantName,
                comment = comment,
                rating = rating,
                imagePath = imageUrl
            )

            if (updatedReview != null) {
                reviewViewModel.updateReview(updatedReview)
                Toast.makeText(requireContext(), "הביקורת עודכנה בהצלחה", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance().reference
                .child("review_images/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun deleteReview() {
        currentReview?.let { review ->
            reviewViewModel.deleteReview(review)
            Toast.makeText(requireContext(), "הביקורת נמחקה", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivSelectedImage.setImageURI(selectedImageUri)
        } else if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            photoUri?.let {
                binding.ivSelectedImage.setImageURI(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
