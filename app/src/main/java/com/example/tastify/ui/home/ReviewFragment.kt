//package com.example.tastify.ui.home
//
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
//
//class ReviewFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var reviewViewModel: ReviewViewModel
//    private lateinit var database: AppDatabase
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//
//        // אתחול ה-Database וה-ViewModel
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
//        }
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
