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
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.Review
import com.example.tastify.data.repository.ReviewRepository
import com.example.tastify.ui.adapters.ReviewsAdapter
import com.example.tastify.viewmodel.ReviewViewModel
import com.google.android.ads.mediationtestsuite.viewmodels.ViewModelFactory

class HomeFragment : Fragment() {

  //  private val reviewViewModel: ReviewViewModel by viewModels()
    private lateinit var adapter: ReviewsAdapter
    private lateinit var btnAddReview: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    private lateinit var reviewViewModel: ReviewViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerReviews)
        val btnAddReview = view.findViewById<Button>(R.id.btnAddReview)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ReviewsAdapter(emptyList())
        recyclerView.adapter = adapter

        val repository = ReviewRepository(AppDatabase.getDatabase(requireContext()).reviewDao())
        reviewViewModel = ViewModelProvider(this, ViewModelFactory(repository))[ReviewViewModel::class.java]

        reviewViewModel.getAllReviews.observe(viewLifecycleOwner) { reviews ->
            adapter.updateData(reviews)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        postAdapter = PostAdapter()
//        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
//        binding.rvPosts.adapter = postAdapter
//
//        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
//            postAdapter.setPosts(posts)
//        }


    btnAddReview.setOnClickListener {
        val newReview = Review(userId = "1", restaurantId = "1", rating = 4.5f, comment = "אחלה מסעדה!")
        reviewViewModel.insertReview(newReview)
            // ניווט למסך הוספת ביקורת (תיצור מסך נפרד לכך)
        }
}
