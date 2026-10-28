package com.example.viewmodel

import com.example.model.QuizQuestion

/** Store the count on the queued appearance, leaving the question being answered unchanged. */
internal fun QuizState.enqueueRecycledQuestion(question: QuizQuestion): QuizState {
    val phraseId = question.phrase.id
    val count = maxOf(recycleCounts[phraseId] ?: 0, question.recycleCount) + 1
    val updatedQuestions = questions.toMutableList()
    val insertIndex = (currentIndex + 4).coerceAtMost(updatedQuestions.size)
    updatedQuestions.add(insertIndex, question.copy(recycleCount = count))
    return copy(
        questions = updatedQuestions,
        recycleCounts = recycleCounts + (phraseId to count),
        reshuffledCount = reshuffledCount + 1
    )
}

/** An overridden answer removes only its newly queued retry, preserving earlier mistakes. */
internal fun QuizState.undoCurrentRecycle(): QuizState {
    val current = questions.getOrNull(currentIndex) ?: return this
    val count = recycleCounts[current.phrase.id] ?: return this
    val retryIndex = questions.indices.firstOrNull {
        it > currentIndex && questions[it].phrase.id == current.phrase.id &&
            questions[it].recycleCount == count && count > current.recycleCount
    } ?: return this
    val updatedQuestions = questions.toMutableList().apply { removeAt(retryIndex) }
    val updatedCounts = recycleCounts.toMutableMap().apply {
        if (count > 1) put(current.phrase.id, count - 1) else remove(current.phrase.id)
    }
    return copy(
        questions = updatedQuestions,
        recycleCounts = updatedCounts,
        reshuffledCount = (reshuffledCount - 1).coerceAtLeast(0)
    )
}
