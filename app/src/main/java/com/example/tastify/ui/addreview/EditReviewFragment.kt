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
import androidx.navigation.fragment.navArgs
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.data.model.Review
import com.example.tastify.databinding.FragmentEditReviewBinding
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditReviewFragment : Fragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!

    private val args: EditReviewFragmentArgs by navArgs()
    private var currentReview: Review? = null
    private var selectedImageUri: Uri? = null
    private var photoUri: Uri? = null

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository())
    }

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

                if (!review.imagePath.isNullOrEmpty()) {
                    Picasso.get().load(review.imagePath).into(binding.ivSelectedImage)
                }
            } else {
                Toast.makeText(requireContext(), "לא נמצאה ביקורת", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }

        binding.btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSaveReview.setOnClickListener {
            saveReview()
        }

        binding.btnDeleteReview.setOnClickListener {
            deleteReview()
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 3001)
    }

    private fun saveReview() {
        val restaurantName = binding.etRestaurantName.text.toString().trim()
        val comment = binding.etReviewContent.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (restaurantName.isBlank() || comment.isBlank()) {
            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val review = currentReview ?: return

        // 🚀 העלאת תמונה חדשה אם נבחרה
        CoroutineScope(Dispatchers.Main).launch {
            val newImageUrl = selectedImageUri?.let { uploadImageToFirebase(it, review.id) }
            val updatedReview = review.copy(
                restaurantId = restaurantName,
                comment = comment,
                rating = rating,
                imagePath = newImageUrl ?: review.imagePath
            )

            reviewViewModel.updateReview(updatedReview)
            Toast.makeText(requireContext(), "הביקורת עודכנה בהצלחה", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private suspend fun uploadImageToFirebase(uri: Uri, reviewId: String): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance()
                .reference.child("review_images/${reviewId}_${System.currentTimeMillis()}.jpg")
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
        if (requestCode == 3001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivSelectedImage.setImageURI(selectedImageUri)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
