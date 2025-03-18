package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tastify.data.model.Post
import com.example.tastify.databinding.FragmentMyPostsBinding
import com.example.tastify.ui.home.PostAdapter
import com.google.firebase.auth.FirebaseAuth

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!

    // הוספת ה-ViewModel כאן
    private val viewModel: MyPostsViewModel by viewModels()
    private lateinit var adapter: PostAdapter
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // אתחול של ה-Adapter
        adapter = PostAdapter()
        binding.recyclerViewMyPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMyPosts.adapter = adapter

        // קריאה ל-ViewModel בכדי להחזיר את הפוסטים של המשתמש הנוכחי
        viewModel.getMyPosts(currentUserId.orEmpty())

        // התבוננות בפוסטים
        viewModel.myPosts.observe(viewLifecycleOwner, Observer { posts ->
            adapter.setPosts(posts)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
