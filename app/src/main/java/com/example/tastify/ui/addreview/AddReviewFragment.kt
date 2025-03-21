package com.example.tastify.ui.addreview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.Review
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.databinding.FragmentAddReviewBinding
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream

class AddReviewFragment : Fragment() {

    private var _binding: FragmentAddReviewBinding? = null
    private val binding get() = _binding!!

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository(AppDatabase.getDatabase(requireContext()).reviewDao()))
    }

    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)

        binding.btnSelectImage.setOnClickListener {
            selectImageFromGallery()
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

            val review = Review(
                restaurantId = restaurantName,
                userId = currentUser.uid,
                comment = comment,
                rating = rating,
                imagePath = selectedImageUri?.let { copyImageToCache(it) }
            )

            reviewViewModel.addReview(review)
            Toast.makeText(requireContext(), "הביקורת נוספה בהצלחה", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 2001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2001 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.ivSelectedImage.setImageURI(selectedImageUri)
        }
    }

    private fun copyImageToCache(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().cacheDir, "review_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath // ✅ return the saved image path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
