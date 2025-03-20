package com.example.tastify.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.data.model.User
import com.example.tastify.databinding.FragmentProfileBinding
import com.example.tastify.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // יצירת UserViewModel עם Factory
    private val userViewModel: UserViewModel by viewModels {
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        UserViewModel.Factory(UserRepository(userDao))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = "userId_placeholder" // צריך לקבל את ה-UID מהמשתמש המחובר
        userViewModel.loadUser(userId)

        // מאזין לנתוני המשתמש
        userViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.etProfileName.setText(user.name)  // לוודא התאמה לשמות החדשים
                binding.etProfileEmail.setText(user.email)
            }
        }

        binding.btnUpdateProfile.setOnClickListener {
            updateUserProfile(userId)
        }
    }

    private fun updateUserProfile(userId: String) {
        val newName = binding.etProfileName.text.toString().trim()
        val newEmail = binding.etProfileEmail.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedUser = User(id = userId, name = newName, email = newEmail, profileImageUrl = "")

        lifecycleScope.launch {
            userViewModel.updateUser(updatedUser)
            Toast.makeText(requireContext(), "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
