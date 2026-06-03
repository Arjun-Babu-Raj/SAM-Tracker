package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                    "WHO Growth Chart (Weight trajectory)", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = titleColor
                )
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(250.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        val weights = mutableListOf<Pair<String, Double>>()
                        if (baseline != null) {
                            weights.add("B-Enrol" to baseline.weightEnrolment)
                            weights.add("B-12wk" to baseline.weight12Weeks)
                        }
                        
                        followups.sortedBy { it.followupRound }.forEach { f -> 
                            weights.add(f.followupRound to f.weight)
                        }

                        if (weights.isNotEmpty()) {
                            GrowthChartCanvas(weights)
                        } else {
                            Text("No measurements available", modifier = Modifier.align(Alignment.Center))
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
            }
        }
    }
}

@Composable
fun GrowthChartCanvas(dataPoints: List<Pair<String, Double>>) {
    val lineColor = Color(0xFF0061A4)
    val pointColor = Color(0xFFBA1A1A)
    
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
