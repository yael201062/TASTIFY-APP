package com.example.tastify.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tastify.data.model.Post

class PostViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> get() = _posts

    init {
        loadDummyPosts()
    }

    private fun loadDummyPosts() {
        val dummyPosts = listOf(
            Post(
                id = "1",
                userId = "u1",
                userName = "Yoram Cohen",
                content = "המסעדה הייתה מעולה! ממליץ בחום.",
                imageUrl = "https://picsum.photos/200/300"
            ),
            Post(
                id = "2",
                userId = "u2",
                userName = "Yonatan Edri",
                content = "שרות מעולה, אוכל טעים מאוד. אחזור שוב!",
                imageUrl = "https://picsum.photos/200/301"
            ),
            Post(
                id = "3",
                userId = "u3",
                userName = "Dudi levi",
                content = "האוכל סבבה, אבל לקח הרבה זמן.",
                imageUrl = "https://picsum.photos/200/302"
            )
        )
        _posts.value = dummyPosts
    }
}
