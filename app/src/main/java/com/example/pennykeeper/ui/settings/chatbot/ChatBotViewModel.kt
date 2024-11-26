package com.example.pennykeeper.ui.settings.chatbot

import androidx.lifecycle.ViewModel
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.*

class ChatBotViewModel(
    private val aiService: AIService,
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    /*
        Manages chat messages between users and AI
     */
    data class ChatMessage(
        val content: String,
        val isUser: Boolean
    )

    /*
        Manages reactive updates using StateFlow
     */
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun sendMessage(message: String) {
        /*
        TODO: You will implement this for the tutorial
         */
    }
}