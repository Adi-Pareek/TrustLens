package com.trustlens.app.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.trustlens.app.data.model.UploadUiState
//import com.trustlens.app.data.model.VerifyApiResponse
import com.trustlens.app.ui.Screen
import com.trustlens.app.ui.theme.*
import com.trustlens.app.viewmodel.VerificationViewModel

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: VerificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = (uiState as? UploadUiState.Success)?.result

    val contentAlpha = remember { Animatable(0f) }
    val trustScoreAnim = remember { Animatable(0f) }
    val targetScore = result?.trustScore?.toFloat() ?: 87f

    LaunchedEffect(result) {
        contentAlpha.animateTo(1f, animationSpec = tween(500))
        trustScoreAnim.animateTo(
            targetValue = targetScore,
            animationSpec = tween(1500, easing = EaseOutCubic)
        )
    }

    val scoreColor = when {
        trustScoreAnim.value >= 75f -> ScoreHigh
        trustScoreAnim.value >= 50f -> ScoreMedium
        else -> ScoreLow
    }

    // Map "Low" / "Medium" / "High" from API to display label
    val riskLabel = when (result?.risk?.uppercase() ?: "LOW") {
        "LOW" -> "LOW"
        "MEDIUM" -> "MEDIUM"
        "HIGH" -> "HIGH"
        else -> "LOW"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .alpha(contentAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCard, CircleShape)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("←", fontSize = 18.sp, color = TextPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Verification Report",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Analysis complete",
                        fontSize = 12.sp,
                        color = ScoreHigh
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Share 📤", fontSize = 12.sp, color = TrustCyan)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Trust Score Ring
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    TrustBlueLight.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(160.dp)
                        ) {
                            androidx.compose.foundation.Canvas(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                val strokeWidth = 16.dp.toPx()
                                val sweepAngle = (trustScoreAnim.value / 100f) * 300f
                                drawArc(
                                    color = SurfaceElevated,
                                    startAngle = 120f,
                                    sweepAngle = 300f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                drawArc(
                                    brush = Brush.sweepGradient(
                                        colors = listOf(
                                            scoreColor.copy(alpha = 0.5f),
                                            scoreColor
                                        )
                                    ),
                                    startAngle = 120f,
                                    sweepAngle = sweepAngle,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${trustScoreAnim.value.toInt()}",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )
                                Text(text = "/ 100", fontSize = 14.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "$riskLabel TRUST",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = result?.summary?.take(60) ?: "This document appears authentic",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Risk Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RiskBadge("🛡️", "Risk Level", riskLabel, scoreColor, Modifier.weight(1f))
                RiskBadge("✅", "Verdict", result?.verdict ?: "AUTHENTIC", TrustCyan, Modifier.weight(1f))
                RiskBadge("🤖", "Powered By", "AI + OCR", TextPrimary, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Summary
            SectionCard(title = "🤖 AI Analysis Summary") {
                Text(
                    text = result?.summary ?: "Analysis complete.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detected Issues / Differences
            SectionCard(title = "⚠️ Detected Issues") {
                val differences = result?.differences
                if (differences.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ScoreHigh.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✅", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "No issues detected. Document passed all checks.",
                                fontSize = 13.sp,
                                color = ScoreHigh,
                                lineHeight = 18.sp
                            )
                        }
                    }
                } else {
                    differences.forEach { issue ->
                        Text("• $issue", fontSize = 13.sp, color = ScoreLow, lineHeight = 20.sp)
                    }
                }
            }

            // source Verification
            // Source Verification
            @Composable
            fun SectionCard(
                title: String,
                content: @Composable ColumnScope.() -> Unit
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        content()
                    }
                }
            }
            @Composable
            fun SourceItem(
                source: String,
                status: String,
                verified: Boolean
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (verified) "✅" else "⚠️",
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = source,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )

                        Text(
                            text = status,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.reset()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrustCyan,
                    contentColor = TrustBlueDark
                )
            ) {
                Text("Verify Another Document", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TextDisabled)
            ) {
                Text("Download Report 📥", fontSize = 15.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RiskBadge(
    emoji: String,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
            Text(label, fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

//sectionCard
@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }

    //changes

    @Composable
    fun SourceItem(
        source: String,
        status: String,
        verified: Boolean
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (verified) "✅" else "⚠️",
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = source,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Text(
                    text = status,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}