//package com.example.tastify.ui.home
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.tastify.R
//import com.example.tastify.databinding.FragmentHomeBinding
//import com.example.tastify.viewmodel.PostViewModel
//
//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private val postViewModel: PostViewModel by viewModels()
//    private lateinit var postAdapter: PostAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        postAdapter = PostAdapter()
//        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvPosts.adapter = postAdapter
//
//        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
//            postAdapter.setPosts(posts)
//        }
//
//        binding.btnSearch.setOnClickListener {
//            val query = binding.etSearch.text.toString()
//            if (query.isNotEmpty()) {
//                Toast.makeText(requireContext(), "מחפש: $query", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(requireContext(), "אנא הכנס מונח חיפוש", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        binding.btnLogout.setOnClickListener {
//            // TODO: הוסף כאן את הלוגיקה ליציאה
//        }
//
//        // מאזין לחיצה ל-FAB - ניווט ל-AddReviewFragment
//        binding.fabAddReview.setOnClickListener {
//            findNavController().navigate(R.id.addReviewFragment)
//        }
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}



package com.example.tastify.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.repository.ReviewRepository
import com.example.tastify.databinding.FragmentHomeBinding
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.example.tastify.viewmodel.ReviewViewModelFactory


class HomeFragment : Fragment() {

    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var adapter: ReviewsAdapter
    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Correct way to initialize binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = _binding!!.root  // Use binding.root instead of inflating layout again

        val recyclerView = _binding!!.recyclerReviews
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ReviewsAdapter(emptyList()) // אתחל עם רשימה ריקה
        recyclerView.adapter = adapter

        val reviewDao = AppDatabase.getDatabase(requireContext()).reviewDao()
        val repository = ReviewRepository(reviewDao)
        val factory = ReviewViewModelFactory(repository)
        reviewViewModel = ViewModelProvider(this, factory).get(ReviewViewModel::class.java)

        reviewViewModel.loadAllReviews()

        _binding!!.fabAddReview.setOnClickListener {
            findNavController().navigate(R.id.addReviewFragment)
        }

        return view
    }

}
