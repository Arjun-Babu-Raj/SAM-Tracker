package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildProfileScreen(
    studyId: String,
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val childData by viewModel.getChildProfileData(studyId).collectAsState(initial = null)
    
    val bgColor = Color(0xFFF8F9FF)
    val titleColor = Color(0xFF001D35)
    val subtitleColor = Color(0xFF64748B)
    
    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Child Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (childData == null) {
            Box(modifier = Modifier.fillMaxSize() .padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val (child, baseline, followups) = childData!!
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(child.childName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = titleColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sex: ${child.sex} | Study ID: ${child.studyId}", color = subtitleColor)
                        Text("AWC: ${child.awcName}", color = subtitleColor)
                    }
                }
                
                Text(
                    "Weight Trajectory (kg)", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = titleColor
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val weights = mutableListOf<Pair<String, Double>>()
                        if (baseline != null) {
                            val baseAge = try {
                                val json = org.json.JSONObject(baseline.extraDataJson)
                                val age = json.optString("child_age_months", "")
                                if (age.isNotEmpty()) "${age}m" else "Base"
                            } catch (e: Exception) { "Base" }
                            weights.add("$baseAge" to baseline.weightEnrolment)
                            weights.add("$baseAge(12w)" to baseline.weight12Weeks)
                        }
                        
                        followups.sortedBy { it.followupRound }.forEach { f -> 
                            val label = try {
                                val json = org.json.JSONObject(f.extraDataJson)
                                val age = json.optString("child_age_months", "")
                                if (age.isNotEmpty()) "${age}m" else f.followupRound
                            } catch (e: Exception) { f.followupRound }
                            weights.add(label to f.weight)
                        }

                        if (weights.isNotEmpty()) {
                            GrowthChartCanvas(weights)
                        } else {
                            Text("No measurements available", modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }

                Text(
                    "WHO Z-Score Tracking (Wasting / WHZ)", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = titleColor
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(220.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val zScores = mutableListOf<Pair<String, Double>>()
                        // Baseline doesn't store WHZ currently. Only Follow ups do.
                        followups.sortedBy { it.followupRound }.forEach { f -> 
                            val label = try {
                                val json = org.json.JSONObject(f.extraDataJson)
                                val age = json.optString("child_age_months", "")
                                if (age.isNotEmpty()) "${age}m" else f.followupRound
                            } catch (e: Exception) { f.followupRound }
                            zScores.add(label to f.whz)
                        }

                        if (zScores.isNotEmpty()) {
                            ZScoreChartCanvas(zScores)
                        } else {
                            Text("No follow-up Z-scores available", color = subtitleColor, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
                
                // Historical data
                Text("Measurement History", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
                if (baseline != null) {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Baseline Enrolment: ${baseline.weightEnrolment}kg", fontWeight = FontWeight.Medium)
                            Text("Baseline 12-wk: ${baseline.weight12Weeks}kg -> ${baseline.programmeOutcome}", color = subtitleColor)
                        }
                    }
                }
                followups.sortedBy { it.followupRound }.forEach { f ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("${f.followupRound}: ${f.weight}kg (WHZ: ${f.whz})", fontWeight = FontWeight.Medium)
                            Text("Status: ${f.currentStatus}", color = subtitleColor)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GrowthChartCanvas(dataPoints: List<Pair<String, Double>>) {
    val lineColor = Color(0xFF0061A4)
    val pointColor = Color(0xFFBA1A1A)
    val textMeasurer = rememberTextMeasurer()
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        val maxWeight = (dataPoints.maxOfOrNull { it.second } ?: 10.0) + 2.0
        val minWeight = ((dataPoints.minOfOrNull { it.second } ?: 2.0) - 2.0).coerceAtLeast(0.0)
        val weightRange = maxWeight - minWeight
        
        val gap = width / (dataPoints.size.coerceAtLeast(2) - 1)
        
        val path = Path()
        
        dataPoints.forEachIndexed { index, pair ->
            val x = index * gap
            val normalizedY = 1f - ((pair.second - minWeight) / weightRange).toFloat()
            val y = normalizedY * height
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            drawCircle(
                color = pointColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
            
            drawText(
                textMeasurer = textMeasurer,
                text = pair.first,
                topLeft = Offset(x - 20.dp.toPx(), y + 10.dp.toPx()),
                style = TextStyle(color = Color.DarkGray, fontSize = 10.sp)
            )
        }
        
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
fun ZScoreChartCanvas(dataPoints: List<Pair<String, Double>>) {
    val lineColor = Color(0xFF0061A4)
    val pointColor = Color(0xFF001D35)
    val textMeasurer = rememberTextMeasurer()
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Z-score bounds
        val maxZ = 2f
        val minZ = -4f
        val zRange = maxZ - minZ
        
        // Draw background bands
        // Normal (+2 to -2)
        val normalTop = 0f
        val normalBottom = ((maxZ - (-2f)) / zRange) * height
        drawRect(color = Color(0xFFDCFCE7), topLeft = Offset(0f, normalTop), size = androidx.compose.ui.geometry.Size(width, normalBottom - normalTop))
        
        // MAM (-2 to -3)
        val mamTop = normalBottom
        val mamBottom = ((maxZ - (-3f)) / zRange) * height
        drawRect(color = Color(0xFFFEF08A), topLeft = Offset(0f, mamTop), size = androidx.compose.ui.geometry.Size(width, mamBottom - mamTop))
        
        // SAM (-3 to -4)
        val samTop = mamBottom
        val samBottom = height
        drawRect(color = Color(0xFFFECACA), topLeft = Offset(0f, samTop), size = androidx.compose.ui.geometry.Size(width, samBottom - samTop))
        
        // Draw Z-score lines
        val strokeWidth = 1.dp.toPx()
        drawLine(Color.Gray, Offset(0f, normalBottom), Offset(width, normalBottom), strokeWidth = strokeWidth)
        drawLine(Color.Gray, Offset(0f, mamBottom), Offset(width, mamBottom), strokeWidth = strokeWidth)

        if (dataPoints.isEmpty()) return@Canvas
        
        val gap = if (dataPoints.size > 1) width / (dataPoints.size - 1) else width / 2
        val path = Path()
        
        dataPoints.forEachIndexed { index, pair ->
            val x = if (dataPoints.size > 1) index * gap else width / 2
            val z = pair.second.toFloat().coerceIn(minZ, maxZ)
            val y = ((maxZ - z) / zRange) * height
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            
            drawCircle(
                color = pointColor,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )

            drawText(
                textMeasurer = textMeasurer,
                text = pair.first,
                topLeft = Offset(x - 15.dp.toPx(), y + 10.dp.toPx()),
                style = TextStyle(color = Color.DarkGray, fontSize = 10.sp)
            )
        }
        
        if (dataPoints.size > 1) {
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
