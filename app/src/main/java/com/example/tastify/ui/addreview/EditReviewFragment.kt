package com.example.tastify.ui.addreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.data.model.Review
import com.example.tastify.databinding.FragmentEditReviewBinding
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory

class EditReviewFragment : Fragment() {

    private var _binding: FragmentEditReviewBinding? = null
    private val binding get() = _binding!!

    private val args: EditReviewFragmentArgs by navArgs()
    private var currentReview: Review? = null

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

        // ✅ שימוש ב-ViewModel לקריאה אסינכרונית
        reviewViewModel.getReviewById(reviewId) { review ->
            if (review != null) {
                currentReview = review
                binding.etRestaurantName.setText(review.restaurantId)
                binding.etReviewContent.setText(review.comment)
                binding.ratingBar.rating = review.rating
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
    }

    private fun saveReview() {
        val restaurantName = binding.etRestaurantName.text.toString().trim()
        val comment = binding.etReviewContent.text.toString().trim()
        val rating = binding.ratingBar.rating

        if (restaurantName.isBlank() || comment.isBlank()) {
            Toast.makeText(requireContext(), "נא למלא את כל השדות", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedReview = currentReview?.copy(
            restaurantId = restaurantName,
            comment = comment,
            rating = rating
        )

        if (updatedReview != null) {
            reviewViewModel.updateReview(updatedReview)
            Toast.makeText(requireContext(), "הביקורת עודכנה בהצלחה", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    private fun deleteReview() {
        currentReview?.let { review ->
            reviewViewModel.deleteReview(review)
            Toast.makeText(requireContext(), "הביקורת נמחקה", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
