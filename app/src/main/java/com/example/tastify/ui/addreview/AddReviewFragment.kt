package com.example.tastify.ui.addreview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class AddReviewFragment : Fragment() {

    private var _binding: FragmentAddReviewBinding? = null
    private val binding get() = _binding!!

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository(AppDatabase.getDatabase(requireContext()).reviewDao()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddReviewBinding.inflate(inflater, container, false)

        binding.btnSubmitReview.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser == null) {
                Toast.makeText(requireContext(), "לא ניתן להוסיף ביקורת - אין משתמש מחובר", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = currentUser.uid // ✅ ה-UID של המשתמש המחובר

            val review = Review(
                restaurantId = binding.etRestaurantName.text.toString(),
                userId = userId,
                comment = binding.etReviewContent.text.toString(),
                rating = binding.ratingBar.rating
            )

            reviewViewModel.addReview(review)
            Toast.makeText(requireContext(), "הביקורת נוספה", Toast.LENGTH_SHORT).show()

            // חזרה למסך הקודם
            findNavController().navigateUp()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
