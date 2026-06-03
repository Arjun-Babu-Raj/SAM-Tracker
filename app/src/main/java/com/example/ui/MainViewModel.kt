package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BaselineAssessment
import com.example.data.Child
import com.example.data.Household
import com.example.data.LongitudinalFollowup
import com.example.data.SamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    object Success : FormState()
    data class Error(val message: String) : FormState()
}

data class AnalyticsStats(
    val totalEnrolled: Int = 0,
    val male: Int = 0,
    val female: Int = 0,
    val sam: Int = 0,
    val mam: Int = 0,
    val normal: Int = 0
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SamRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SamRepository(database.samDao())
    }

    private val _baselineState = MutableStateFlow<FormState>(FormState.Idle)
    val baselineState: StateFlow<FormState> = _baselineState.asStateFlow()

    private val _followupState = MutableStateFlow<FormState>(FormState.Idle)
    val followupState: StateFlow<FormState> = _followupState.asStateFlow()

    private val _searchedChild = MutableStateFlow<Child?>(null)
    val searchedChild: StateFlow<Child?> = _searchedChild.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    fun resetBaselineState() {
        _baselineState.value = FormState.Idle
    }

    fun submitBaseline(
        household: Household,
        child: Child,
        baseline: BaselineAssessment // It has extraDataJson built in
    ) {
        viewModelScope.launch {
            _baselineState.value = FormState.Loading
            try {
                repository.logBaseline(household, child, baseline)
                _baselineState.value = FormState.Success
            } catch (e: Exception) {
                _baselineState.value = FormState.Error(e.message ?: "Failed to log baseline")
            }
        }
    }

    fun searchChildForFollowup(query: String) {
        viewModelScope.launch {
            _searchError.value = null
            _searchedChild.value = null
            try {
                val child = repository.searchChild(query)
                if (child != null) {
                    _searchedChild.value = child
                } else {
                    _searchError.value = "Child not found. Verify Study ID or CSAM ID."
                }
            } catch (e: Exception) {
                _searchError.value = "Error searching database."
            }
        }
    }

    fun resetFollowupState() {
        _followupState.value = FormState.Idle
        _searchedChild.value = null
        _searchError.value = null
    }

    val allChildren: StateFlow<List<Child>> = repository.getAllChildren()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val analyticsStats: StateFlow<AnalyticsStats> = kotlinx.coroutines.flow.flow {
        repository.getAllChildren().collect { children ->
            val baselines = repository.getAllBaselines().associateBy { it.studyId }
            val followups = repository.getAllFollowups().groupBy { it.studyId }
            
            var samCount = 0
            var mamCount = 0
            var normalCount = 0
            
            for (child in children) {
                // Get most recent WHZ
                val fList = followups[child.studyId]
                if (!fList.isNullOrEmpty()) {
                    val latestWHZ = fList.maxByOrNull { it.followupRound }?.whz ?: 0.0 // M15 > M12 > M9 approx by string sort
                    if (latestWHZ < -3) samCount++
                    else if (latestWHZ < -2) mamCount++
                    else normalCount++
                } else {
                    val b = baselines[child.studyId]
                    if (b != null) {
                        // Estimate baseline from outcome if no WHZ is provided
                        when (b.programmeOutcome) {
                            "Recovered" -> normalCount++
                            // If they are in treatment we assume still SAM or MAM based on generic logic.
                            else -> samCount++
                        }
                    }
                }
            }
            
            emit(AnalyticsStats(
                totalEnrolled = children.size,
                male = children.count { it.sex.equals("Male", ignoreCase = true) },
                female = children.count { it.sex.equals("Female", ignoreCase = true) },
                sam = samCount,
                mam = mamCount,
                normal = normalCount
            ))
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AnalyticsStats())

    fun submitFollowup(followup: LongitudinalFollowup) {
        viewModelScope.launch {
            _followupState.value = FormState.Loading
            try {
                repository.logFollowup(followup)
                _followupState.value = FormState.Success
            } catch (e: Exception) {
                _followupState.value = FormState.Error(
                    if (e.message?.contains("UNIQUE constraint") == true) {
                        "This round is already logged for this child."
                    } else {
                        e.message ?: "Failed to log followup"
                    }
                )
            }
        }
    }

    fun getChildProfileData(studyId: String): kotlinx.coroutines.flow.Flow<Triple<Child, BaselineAssessment?, List<LongitudinalFollowup>>?> = kotlinx.coroutines.flow.flow {
        val child = repository.searchChild(studyId)
        if (child != null) {
            val baselines = repository.getAllBaselines().filter { it.studyId == studyId }
            val followups = repository.getAllFollowups().filter { it.studyId == studyId }
            emit(Triple(child, baselines.firstOrNull(), followups))
        } else {
            emit(null)
        }
    }

    fun exportDataToCsv(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val households = repository.getAllHouseholds().associateBy { it.householdId }
                val children = repository.getAllChildrenList()
                val baselines = repository.getAllBaselines().associateBy { it.studyId }
                val followups = repository.getAllFollowups().groupBy { it.studyId }

                val csvBuilder = StringBuilder()
                csvBuilder.append("StudyID,CSAM_ID,ChildName,Sex,AWC,Sector,")
                csvBuilder.append("HouseholdID,FamilyType,FamilyMembers,MonthlyIncome,")
                csvBuilder.append("Baseline_WeightEnrol,Baseline_Weight12,Baseline_HeightEnrol,Baseline_Height12,Baseline_Outcome,")
                csvBuilder.append("Followups\n")
                
                for (child in children) {
                    val hh = households[child.householdId]
                    val b = baselines[child.studyId]
                    val fs = followups[child.studyId] ?: emptyList()
                    
                    csvBuilder.append("${child.studyId},${child.csamId},${child.childName},${child.sex},${child.awcName},${child.sectorBlockDistrict},")
                    csvBuilder.append("${hh?.householdId},${hh?.familyType},${hh?.totalFamilyMembers},${hh?.monthlyIncome},")
                    csvBuilder.append("${b?.weightEnrolment},${b?.weight12Weeks},${b?.heightEnrolment},${b?.height12Weeks},${b?.programmeOutcome},")
                    csvBuilder.append("${fs.joinToString("|") { it.followupRound + ":" + it.currentStatus + ":" + it.weight }}\n")
                }
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvBuilder.toString().toByteArray())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
