package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.LongitudinalFollowup
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowupScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.followupState.collectAsStateWithLifecycle()
    val searchedChild by viewModel.searchedChild.collectAsStateWithLifecycle()
    val searchError by viewModel.searchError.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var searchQuery by remember { mutableStateOf("") }
    
    // Form state
    var round by remember { mutableStateOf("M9") }
    var currentStatus by remember { mutableStateOf("Alive") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var whz by remember { mutableStateOf("") }
    var thrReceived by remember { mutableStateOf("Regular") }
    var thrConsumedDays by remember { mutableStateOf("") }
    var dietaryScore by remember { mutableStateOf("") }

    var dynamicAnswers by remember { mutableStateOf(mapOf<String, String>()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Log Follow-Up (Tool 2)") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetFollowupState()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by Study ID, CSAM ID or Name") },
                trailingIcon = {
                    IconButton(onClick = { viewModel.searchChildForFollowup(searchQuery) }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("search_bar")
            )
            
            searchError?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            if (searchedChild != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Child Found: ${searchedChild!!.childName}", fontWeight = FontWeight.Bold)
                            Text("Study ID: ${searchedChild!!.studyId} | CSAM ID: ${searchedChild!!.csamId}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                if (formState is FormState.Success) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Success! Follow-up logged securely.", style = MaterialTheme.typography.titleMedium)
                            Button(
                                onClick = {
                                    viewModel.resetFollowupState()
                                    onNavigateBack()
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Return to Dashboard")
                            }
                        }
                    }
                } else {
                     if (formState is FormState.Error) {
                        Text(
                            text = "Error: ${(formState as FormState.Error).message}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Text("Follow-Up Data", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = round, onValueChange = { round = it }, label = { Text("Round (M9, M12, M15)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = currentStatus, onValueChange = { currentStatus = it }, label = { Text("Current Status (Alive/Dead)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Weight (kg)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Height (cm)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = whz, onValueChange = { whz = it }, label = { Text("WHZ Score") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = thrConsumedDays, onValueChange = { thrConsumedDays = it }, label = { Text("THR Consumed Days (last 7d)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dietaryScore, onValueChange = { dietaryScore = it }, label = { Text("Dietary Score (24h)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Extended Questionnaire", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    DynamicFormFields(
                        fields = QuestionBank.allFollowupFields,
                        answers = dynamicAnswers,
                        onAnswerChange = { id, value ->
                            dynamicAnswers = dynamicAnswers.toMutableMap().apply { put(id, value) }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val extraJson = JSONObject(dynamicAnswers).toString()
                            val followup = LongitudinalFollowup(
                                studyId = searchedChild!!.studyId,
                                followupRound = round,
                                currentStatus = currentStatus,
                                weight = weight.toDoubleOrNull() ?: 0.0,
                                height = height.toDoubleOrNull() ?: 0.0,
                                whz = whz.toDoubleOrNull() ?: 0.0,
                                thrReceived = thrReceived,
                                thrConsumedDays7d = thrConsumedDays.toIntOrNull() ?: 0,
                                dietaryScore24h = dietaryScore.toIntOrNull() ?: 0,
                                extraDataJson = extraJson
                            )
                            viewModel.submitFollowup(followup)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_save_followup"),
                        enabled = formState != FormState.Loading && round.isNotBlank()
                    ) {
                        if (formState is FormState.Loading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Save Follow-up Data")
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("Search for a child to unlock the form.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
