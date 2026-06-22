package com.trustlens.app.ui.screens.home

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.trustlens.app.ui.Screen
import com.trustlens.app.ui.theme.*
import kotlinx.coroutines.delay

data class StatItem(val value: String, val label: String, val emoji: String)

@Composable
fun HomeScreen(navController: NavController) {

    val contentAlpha = remember { Animatable(0f) }
    val cardOffset = remember { Animatable(60f) }

    // Rotating stats
    val stats = listOf(
        StatItem("99%", "Accuracy Rate", "🎯"),
        StatItem("<30s", "Analysis Time", "⚡"),
        StatItem("5+", "Document Types", "📄"),
        StatItem("100%", "AI Powered", "🤖")
    )

    var currentStatIndex by remember { mutableStateOf(0) }
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(600))
        cardOffset.animateTo(
            0f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    // Cycle through stats with fade in/out
    LaunchedEffect(Unit) {
        while (true) {
            delay(2500)
            visible = false
            delay(400)
            currentStatIndex = (currentStatIndex + 1) % stats.size
            visible = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Top glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TrustBlueLight.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .alpha(contentAlpha.value),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TrustLens",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Document Verification",
                        fontSize = 13.sp,
                        color = TrustCyan
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SurfaceCard, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔍", fontSize = 20.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Hero Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    TrustBlueLight.copy(alpha = 0.3f),
                                    TrustBlueDark.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛡️", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Verify Any Document",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Upload a document and our AI will analyze its authenticity, detect tampering, and generate a trust score.",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Animated Rotating Stat Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, TrustCyan.copy(alpha = 0.3f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    TrustBlueLight.copy(alpha = 0.1f),
                                    TrustCyan.copy(alpha = 0.05f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val statAlpha by animateFloatAsState(
                        targetValue = if (visible) 1f else 0f,
                        animationSpec = tween(400),
                        label = "statAlpha"
                    )
                    val statOffset by animateFloatAsState(
                        targetValue = if (visible) 0f else -30f,
                        animationSpec = tween(400),
                        label = "statOffset"
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .alpha(statAlpha)
                            .offset(y = statOffset.dp)
                    ) {
                        Text(
                            text = stats[currentStatIndex].emoji,
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stats[currentStatIndex].value,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TrustCyan
                        )
                        Text(
                            text = stats[currentStatIndex].label,
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }

                    // Dot indicators bottom right
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        stats.forEachIndexed { index, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (index == currentStatIndex) 8.dp else 5.dp)
                                    .background(
                                        color = if (index == currentStatIndex)
                                            TrustCyan else TextDisabled,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FeaturePill(emoji = "🤖", label = "AI Analysis", modifier = Modifier.weight(1f))
                FeaturePill(emoji = "🔎", label = "OCR Scan", modifier = Modifier.weight(1f))
                FeaturePill(emoji = "📊", label = "Trust Score", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA Button
            Button(
                onClick = { navController.navigate(Screen.Upload.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TrustCyan,
                    contentColor = TrustBlueDark
                )
            ) {
                Text(
                    text = "Start Verification",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("→", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TextDisabled)
            ) {
                Text(
                    text = "View Sample Report",
                    fontSize = 15.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FeaturePill(emoji: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceElevated)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}