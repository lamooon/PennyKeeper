package com.example.pennykeeper.ui.settings.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChatBotViewModel(
    private val aiService: AIService
) : ViewModel() {

    data class ChatMessage(
        val content: String,
        val isUser: Boolean
    )

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun analyzeAllData() {

    }

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            _chatHistory.value += ChatMessage(userMessage, isUser = true)
            _isAnalyzing.value = true

            try {
                val response = aiService.makeRequest(userMessage)
                _chatHistory.value += ChatMessage(response, isUser = false)
            } catch (e: Exception) {
                val errorMessage = e.message ?: "An unknown error occurred"
                _chatHistory.value += ChatMessage(
                    "Sorry, I encountered an error: $errorMessage",
                    isUser = false
                )
            } finally {
                _isAnalyzing.value = false
            }
        }
    }
}