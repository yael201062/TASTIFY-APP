package com.example.tastify.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tastify.R
import com.example.tastify.data.database.AppDatabase
import com.example.tastify.data.model.User
import com.example.tastify.data.dao.repository.UserRepository
import com.example.tastify.databinding.FragmentRegisterBinding
import com.example.tastify.viewmodel.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val userViewModel: UserViewModel by viewModels {
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()
        UserViewModel.Factory(UserRepository(userDao))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "אנא מלא את כל השדות", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(requireContext(), "הסיסמאות לא תואמות", Toast.LENGTH_SHORT).show()
            } else {
                // הצגת ProgressBar
                binding.progressBar.visibility = View.VISIBLE
                binding.btnRegister.isEnabled = false
                registerUser(name, email, password)
            }
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                // הסתרת ProgressBar
                binding.progressBar.visibility = View.GONE
                binding.btnRegister.isEnabled = true

                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val user = User(id = userId, name = name, email = email, profileImageUrl = "")

                    lifecycleScope.launch {
                        userViewModel.insertUser(user)
                    }

                    Toast.makeText(requireContext(), "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "שגיאה: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
