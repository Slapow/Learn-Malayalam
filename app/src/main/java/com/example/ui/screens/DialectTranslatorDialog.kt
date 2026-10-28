package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TranslationResult
import com.example.model.PhraseItem
import com.example.ui.theme.TextPrimaryDark
import com.example.viewmodel.TranslatorState

@Composable
fun DialectTranslatorDialog(
    state: TranslatorState,
    onInputChange: (String) -> Unit,
    onTranslate: () -> Unit,
    onSpeakPhrase: (PhraseItem) -> Unit,
    onDismiss: () -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    // Resume an unfinished query when the translator is reopened.
    LaunchedEffect(Unit) {
        if (state.inputText.isNotBlank()) onTranslate()
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize().testTag("translator_screen")) {
            Column(
                modifier = Modifier.fillMaxSize().safeDrawingPadding()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                        .background(Color(0xFF19271F)).padding(horizontal = 4.dp)
                        .testTag("translator_header"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Translator",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryDark,
                        modifier = Modifier.testTag("translator_title")
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterStart).testTag("translator_back_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryDark
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().imePadding()
                        .padding(start = 20.dp, end = 20.dp, top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Type English, Manglish, or Malayalam. Translations update automatically in Central Travancore Malayalam.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = state.inputText,
                        onValueChange = onInputChange,
                        label = { Text("Word or phrase") },
                        placeholder = { Text("e.g. hello, I want tea, or chaaya") },
                        modifier = Modifier.fillMaxWidth().testTag("translator_input"),
                        minLines = 1,
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            keyboard?.hide()
                            onTranslate()
                        })
                    )
                    if (!state.isAiConfigured) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().testTag("translator_configuration_notice"),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Text(
                                "Vocabulary translations are available. To enable AI for other phrases, add GEMINI_API_KEY to the project's local .env file and rebuild the app.",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                    if (state.isLoading) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().testTag("translator_loading"))
                    }
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth().testTag("translator_results"),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        state.error?.let { error ->
                            item {
                                Column {
                                    Text(
                                        text = error,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.testTag("translator_error")
                                    )
                                    TextButton(onClick = onTranslate, modifier = Modifier.testTag("translator_retry")) {
                                        Text("Retry")
                                    }
                                }
                            }
                        }
                        itemsIndexed(state.results) { index, result ->
                            TranslationMeaningCard(result, index, onSpeakPhrase)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TranslationMeaningCard(
    result: TranslationResult,
    index: Int,
    onSpeakPhrase: (PhraseItem) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().testTag("translator_result_$index")) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (result.usageLabel.isNotBlank()) {
                        Text(result.usageLabel, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = if (result.isDatabaseMatch) "From your vocabulary" else "AI alternative",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = {
                    onSpeakPhrase(PhraseItem(
                        id = "translator_result_$index",
                        english = result.english,
                        manglish = result.manglish,
                        malayalam = result.malayalam
                    ))
                }) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Hear ${result.english}")
                }
            }
            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(result.english, style = MaterialTheme.typography.titleMedium)
                    Text(
                        result.manglish,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(result.malayalam, style = MaterialTheme.typography.bodyLarge)
                    if (result.note.isNotBlank()) {
                        Text(result.note, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
