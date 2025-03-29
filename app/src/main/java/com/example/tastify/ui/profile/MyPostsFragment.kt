package com.example.tastify.ui.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.data.dao.repository.ReviewRepository
import com.example.tastify.databinding.FragmentMyPostsBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private val reviewViewModel: ReviewViewModel by viewModels {
        ReviewViewModelFactory(ReviewRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ReviewsAdapter(
            reviews = mutableListOf(),
            isEditable = true,
            onEditClicked = { review ->
                val action = MyPostsFragmentDirections
                    .actionMyPostsFragmentToEditReviewFragment(review.id)
                findNavController().navigate(action)
            },
            onDeleteClicked = { review ->
                showDeleteConfirmation(review)
            }
        )

        binding.recyclerReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerReviews.adapter = adapter

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid

            reviewViewModel.getReviewsByUser(userId)

            viewLifecycleOwner.lifecycleScope.launch {
                reviewViewModel.userReviews.collect { reviews ->
                    adapter.updateData(reviews.toMutableList())
                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }

            binding.swipeRefreshLayout.setOnRefreshListener {
                binding.swipeRefreshLayout.isRefreshing = true
                reviewViewModel.getReviewsByUser(userId)
            }
        } else {
            binding.tvMyPostsTitle.text = "אין משתמש מחובר"
        }
    }

    private fun showDeleteConfirmation(review: com.example.tastify.data.model.Review) {
        AlertDialog.Builder(requireContext())
            .setTitle("מחיקת פוסט")
            .setMessage("האם אתה בטוח שברצונך למחוק את הפוסט?")
            .setPositiveButton("כן") { _, _ ->
                reviewViewModel.deleteReview(review)
                Toast.makeText(requireContext(), "הביקורת נמחקה", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("ביטול", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
