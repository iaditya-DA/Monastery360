// FILE: com.example.monastery360/viewmodel/FeedViewModelFactory.kt
package com.example.monastery360.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FeedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FeedViewModel() as T
    }
}