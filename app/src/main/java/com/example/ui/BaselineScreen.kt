package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaselineScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val formState by viewModel.baselineState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Household state
    var householdId by remember { mutableStateOf("") }
    var familyType by remember { mutableStateOf("Nuclear") }
    var totalMembers by remember { mutableStateOf("") }
    var monthlyIncome by remember { mutableStateOf("") }

    // Child state
    var studyId by remember { mutableStateOf("") }
    var csamId by remember { mutableStateOf("") }
    var childName by remember { mutableStateOf("") }
    var sex by remember { mutableStateOf("Male") }
    var awcName by remember { mutableStateOf("") }
    var sectorBlockDistrict by remember { mutableStateOf("") }

    // Baseline state
    var weightEnrolment by remember { mutableStateOf("") }
    var weight12Weeks by remember { mutableStateOf("") }
    var heightEnrolment by remember { mutableStateOf("") }
    var height12Weeks by remember { mutableStateOf("") }
    var programmeOutcome by remember { mutableStateOf("Recovered") }
    var thrReceived by remember { mutableStateOf("Regular") }
    var thrConsumedDays by remember { mutableStateOf("") }
    var dietaryScore by remember { mutableStateOf("") }

    // Extra dynamic answers
    var dynamicAnswers by remember { mutableStateOf(mapOf<String, String>()) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Log Baseline (Tool 1)") },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetBaselineState()
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
            if (formState is FormState.Success) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Success! Baseline logged securely.", style = MaterialTheme.typography.titleMedium)
                        Button(
                            onClick = {
                                viewModel.resetBaselineState()
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

                Text("Household Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = householdId, onValueChange = { householdId = it }, label = { Text("Household ID") }, modifier = Modifier.fillMaxWidth().testTag("hh_id"))
                OutlinedTextField(value = totalMembers, onValueChange = { totalMembers = it }, label = { Text("Total Family Members") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = monthlyIncome, onValueChange = { monthlyIncome = it }, label = { Text("Monthly Income (₹)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(24.dp))
                Text("Child Identification", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = studyId, onValueChange = { studyId = it }, label = { Text("Study ID (Unique for follow-up)") }, modifier = Modifier.fillMaxWidth().testTag("study_id"))
                OutlinedTextField(value = csamId, onValueChange = { csamId = it }, label = { Text("CSAM ID") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = childName, onValueChange = { childName = it }, label = { Text("Child Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = awcName, onValueChange = { awcName = it }, label = { Text("AWC Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = sectorBlockDistrict, onValueChange = { sectorBlockDistrict = it }, label = { Text("Sector / Block / District") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(24.dp))
                Text("Anthropometry & Clinical Status", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = weightEnrolment, onValueChange = { weightEnrolment = it }, label = { Text("Weight (Enrol) kg") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = weight12Weeks, onValueChange = { weight12Weeks = it }, label = { Text("Weight (12wk) kg") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = heightEnrolment, onValueChange = { heightEnrolment = it }, label = { Text("Height (Enrol) cm") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                    OutlinedTextField(value = height12Weeks, onValueChange = { height12Weeks = it }, label = { Text("Height (12wk) cm") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.weight(1f))
                }
                OutlinedTextField(value = thrConsumedDays, onValueChange = { thrConsumedDays = it }, label = { Text("THR Consumed Days (last 7d)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = dietaryScore, onValueChange = { dietaryScore = it }, label = { Text("Dietary Score (24h)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(24.dp))
                Text("Extended Questionnaire", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DynamicFormFields(
                    fields = QuestionBank.allBaselineFields,
                    answers = dynamicAnswers,
                    onAnswerChange = { id, value ->
                        dynamicAnswers = dynamicAnswers.toMutableMap().apply { put(id, value) }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        val hl = Household(householdId, familyType, totalMembers.toIntOrNull() ?: 0, monthlyIncome.toDoubleOrNull() ?: 0.0)
                        val ch = Child(studyId, csamId, householdId, childName, sex, awcName, sectorBlockDistrict)
                        val extraJson = JSONObject(dynamicAnswers).toString()
                        val bs = BaselineAssessment(
                            studyId = studyId,
                            weightEnrolment = weightEnrolment.toDoubleOrNull() ?: 0.0,
                            weight12Weeks = weight12Weeks.toDoubleOrNull() ?: 0.0,
                            heightEnrolment = heightEnrolment.toDoubleOrNull() ?: 0.0,
                            height12Weeks = height12Weeks.toDoubleOrNull() ?: 0.0,
                            programmeOutcome = programmeOutcome,
                            thrReceivedTreatment = thrReceived,
                            thrConsumedDays7d = thrConsumedDays.toIntOrNull() ?: 0,
                            dietaryScore24h = dietaryScore.toIntOrNull() ?: 0,
                            extraDataJson = extraJson
                        )
                        viewModel.submitBaseline(hl, ch, bs)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("btn_save_baseline"),
                    enabled = formState != FormState.Loading && studyId.isNotBlank() && householdId.isNotBlank()
                ) {
                    if (formState is FormState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Baseline Assessment")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
