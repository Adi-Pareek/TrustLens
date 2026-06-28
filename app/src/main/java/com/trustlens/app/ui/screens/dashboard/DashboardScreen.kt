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
    val targetScore = result?.trustScore?.toFloat() ?: 0f

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

    val riskLabel = result?.risk ?: "LOW"

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

            // Trust Score Card
            SectionCard(title = "Verification Report") {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(160.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 16.dp.toPx()
                                val sweepAngle = (trustScoreAnim.value / 100f) * 300f

                                drawArc(
                                    color = SurfaceElevated,
                                    startAngle = 120f,
                                    sweepAngle = 300f,
                                    useCenter = false,
                                    style = Stroke(
                                        width = strokeWidth,
                                        cap = StrokeCap.Round
                                    )
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
                                    style = Stroke(
                                        width = strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${trustScoreAnim.value.toInt()}",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )

                                Text(
                                    text = "/100",
                                    fontSize = 14.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "$riskLabel TRUST",
                            color = scoreColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result?.summary ?: "Analysis complete",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RiskBadge(
                    "🛡️",
                    "Risk Level",
                    riskLabel,
                    scoreColor,
                    Modifier.weight(1f)
                )

                RiskBadge(
                    "✅",
                    "Verdict",
                    result?.verdict ?: "Pending",
                    TrustCyan,
                    Modifier.weight(1f)
                )

                RiskBadge(
                    "🤖",
                    "Powered By",
                    "AI + OCR",
                    TextPrimary,
                    Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionCard(title = "🤖 AI Analysis Summary") {
                Text(
                    text = result?.summary ?: "No summary available",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "⚠️ Detected Issues") {
                if (result?.differences.isNullOrEmpty()) {
                    Text(
                        text = "No issues detected.",
                        color = ScoreHigh
                    )
                } else {
                    result?.differences?.forEach {
                        Text(
                            text = "• $it",
                            color = ScoreLow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionCard(title = "🌐 Source Verification") {
                SourceItem(
                    source = "Issuer",
                    status = result?.issuer ?: "Not Available",
                    verified = result?.issuer != null
                )

                SourceItem(
                    source = "Official Source",
                    status = result?.officialSource ?: "Not Available",
                    verified = result?.officialSource != null
                )

                SourceItem(
                    source = "Confidence",
                    status = "${result?.confidence ?: 0.0}",
                    verified = (result?.confidence ?: 0.0) > 0
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.reset()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verify Another Document")
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
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

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