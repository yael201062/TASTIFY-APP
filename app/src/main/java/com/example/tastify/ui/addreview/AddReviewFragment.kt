package com.example.tastify.ui.addreview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.Review
import com.example.tastify.data.repository.ReviewRepository
import androidx.navigation.fragment.findNavController
import com.example.tastify.databinding.FragmentAddReviewBinding
import com.example.tastify.databinding.FragmentHomeBinding
import com.example.tastify.viewmodel.ReviewViewModel

//class AddReviewFragment : Fragment() {
//
//    private var _binding: FragmentAddReviewBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var reviewViewModel: ReviewViewModel
//    private lateinit var database: AppDatabase
//    private var selectedImageUri: Uri? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ) {
//        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)
//        database = AppDatabase.getDatabase(requireContext())
//        val repository = ReviewRepository(database.reviewDao())
//        reviewViewModel = ReviewViewModel(repository)
//
//        // כפתור להוספת ביקורת חדשה
//        binding.btnSubmitReview.setOnClickListener {
//            val review = Review(
//                restaurantId = binding.etRestaurantId.text.toString(),
//                userId = "user@example.com",  // יש לשלוף אימייל אמיתי מהמשתמש המחובר
//                comment = binding.etReview.text.toString(),
//                rating = binding.ratingBar.rating
//            )
//
//            reviewViewModel.insertReview(review)
//            Toast.makeText(requireContext(), "הביקורת נוספה", Toast.LENGTH_SHORT).show()
////        }
//        return binding.root
//    }
//
////    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
////        super.onViewCreated(view, savedInstanceState)
////
////        binding.btnSelectImage.setOnClickListener {
////            openGallery()
////        }
////    }
////
////    private fun openGallery() {
////        val intent = Intent(Intent.ACTION_PICK)
////        intent.type = "image/*"
////        startActivityForResult(intent, REQUEST_IMAGE_PICK)
////    }
//
////    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
////        super.onActivityResult(requestCode, resultCode, data)
////        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
////            selectedImageUri = data?.data
////            binding.ivSelectedImage.setImageURI(selectedImageUri)
////        }
////    }
//
////    private fun submitReview() {
////        val restaurantName = binding.etRestaurantName.text.toString()
////        val location = binding.etLocation.text.toString()
////        val reviewContent = binding.etReviewContent.text.toString()
////        val rating = binding.ratingBar.rating
////
////        if (restaurantName.isEmpty() || location.isEmpty() || reviewContent.isEmpty()) {
////            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
////            return
////        }
////
////        // שליחת הנתונים (כאן תוכל לשמור אותם ב-ViewModel או ב-Database)
////        Toast.makeText(requireContext(), "הביקורת נוספה בהצלחה!", Toast.LENGTH_SHORT).show()
////        findNavController().navigateUp()
////    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
////    companion object {
////        private const val REQUEST_IMAGE_PICK = 1001
////    }
//}


//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import com.example.tastify.data.database.AppDatabase
//import com.example.tastify.data.model.Review
//import com.example.tastify.data.repository.ReviewRepository
//import com.example.tastify.databinding.FragmentHomeBinding

class AddReviewFragment : Fragment() {

    private var _binding: FragmentAddReviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)

        // אתחול ה-Database וה-ViewModel
        database = AppDatabase.getDatabase(requireContext())
        val repository = ReviewRepository(database.reviewDao())
        reviewViewModel = ReviewViewModel(repository)

        // כפתור להוספת ביקורת חדשה
        binding.btnSubmitReview.setOnClickListener {
            val review = Review(
                restaurantId = binding.etRestaurantName.text.toString(),
                userId = "user@example.com",  // יש לשלוף אימייל אמיתי מהמשתמש המחובר
                comment = binding.etReviewContent.text.toString(),
                rating = binding.ratingBar.rating
            )

            reviewViewModel.addReview(review)
            Log.d("Review", "Inserted review: ${review.comment} with rating: ${review.rating}")
            Toast.makeText(requireContext(), "הביקורת נוספה", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
