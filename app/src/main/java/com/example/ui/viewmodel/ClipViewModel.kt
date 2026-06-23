package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.AnalyzedVideoEntity
import com.example.data.repository.AnalysisResult
import com.example.data.repository.ClipRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClipViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClipRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ClipRepository(database.clipDao())
    }

    val history: StateFlow<List<AnalyzedVideoEntity>> = repository.history
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _urlInput = MutableStateFlow("")
    val urlInput = _urlInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _loadingStatus = MutableStateFlow("")
    val loadingStatus = _loadingStatus.asStateFlow()

    private val _analysisResult = MutableStateFlow<AnalysisResult.Success?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onUrlInputChanged(newUrl: String) {
        _urlInput.value = newUrl
        _errorMessage.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun analyzeVideoUrl() {
        val url = _urlInput.value.trim()
        if (url.isEmpty()) {
            _errorMessage.value = "Please enter a valid YouTube video URL"
            return
        }

        if (!isValidYoutubeUrl(url)) {
            _errorMessage.value = "Please secure a valid YouTube video URL format"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _analysisResult.value = null
            _loadingStatus.value = "Initializing temporal processors..."

            try {
                val result = repository.analyzeVideo(url) { status ->
                    _loadingStatus.value = status
                }

                when (result) {
                    is AnalysisResult.Success -> {
                        _analysisResult.value = result
                    }
                    is AnalysisResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _errorMessage.value = "Analysis failed: ${e.localizedMessage ?: "Unknown connection error"}"
            } finally {
                _isLoading.value = false
                _loadingStatus.value = ""
            }
        }
    }

    fun selectHistoryItem(video: AnalyzedVideoEntity) {
        try {
            val moshi = com.example.data.api.RetrofitClient.getMoshi()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, com.example.data.api.ClipSuggestion::class.java)
            val adapter = moshi.adapter<List<com.example.data.api.ClipSuggestion>>(listType)
            val clips = adapter.fromJson(video.clipsJson) ?: emptyList()
            
            _analysisResult.value = AnalysisResult.Success(
                title = video.videoTitle,
                clips = clips,
                isSimulated = false
            )
            _urlInput.value = video.youtubeUrl
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load history item: ${e.localizedMessage}"
        }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            repository.deleteVideoById(id)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun isValidYoutubeUrl(url: String): Boolean {
        val clean = url.trim().lowercase()
        return clean.startsWith("http://") || 
               clean.startsWith("https://") || 
               clean.contains("youtube.com") || 
               clean.contains("youtu.be")
    }
}

class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClipViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
