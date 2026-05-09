package com.example.maywave.chat.viewmodel

data class ChatGameUiState(
    private val responseMessages: Map<String, String> = emptyMap(),
    private val loadingRequestKeys: Set<String> = emptySet(),
    private val errorMessages: Map<String, String> = emptyMap()
) {

    fun resultTextFor(request: ChatGameRequest): String {
        val requestKey = request.key

        return when {
            requestKey in loadingRequestKeys -> LoadingMessage
            errorMessages[requestKey] != null -> errorMessages.getValue(requestKey)
            responseMessages[requestKey] != null -> responseMessages.getValue(requestKey)
            else -> LoadingMessage
        }
    }

    fun startLoading(request: ChatGameRequest): ChatGameUiState {
        return copy(
            loadingRequestKeys = loadingRequestKeys + request.key,
            errorMessages = errorMessages - request.key
        )
    }

    fun saveResponse(
        request: ChatGameRequest,
        message: String
    ): ChatGameUiState {
        return copy(
            responseMessages = responseMessages + (request.key to message),
            loadingRequestKeys = loadingRequestKeys - request.key,
            errorMessages = errorMessages - request.key
        )
    }

    fun saveError(request: ChatGameRequest): ChatGameUiState {
        return copy(
            loadingRequestKeys = loadingRequestKeys - request.key,
            errorMessages = errorMessages + (request.key to ErrorMessage)
        )
    }

    companion object {
        private const val LoadingMessage = "서버 응답을 기다리는 중입니다."
        private const val ErrorMessage = "서버 응답을 가져오지 못했습니다."
    }
}

data class ChatGameRequest(
    val roleId: Int,
    val scenarioId: Int,
    val choice: Int
) {
    val key: String = "$roleId:$scenarioId:$choice"
}
