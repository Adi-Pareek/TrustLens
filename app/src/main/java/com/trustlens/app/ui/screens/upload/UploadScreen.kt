package com.trustlens.app.ui.screens.upload

import androidx.compose.ui.draw.scale
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.trustlens.app.data.model.VerificationState
import com.trustlens.app.ui.Screen
import com.trustlens.app.ui.theme.*
import com.trustlens.app.viewmodel.VerificationViewModel

@Composable
fun UploadScreen(
    navController: NavController,
    viewModel: VerificationViewModel = viewModel()
) {
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val verificationState by viewModel.verificationState.collectAsStateWithLifecycle()
    val uploadProgress by viewModel.uploadProgress.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

    val contentAlpha = remember { Animatable(0f) }
    val isUploading = verificationState is VerificationState.Loading

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(500))
    }

    // Navigate to dashboard when done
    LaunchedEffect(verificationState) {
        if (verificationState is VerificationState.Success) {
            navController.navigate(Screen.Dashboard.route)
        }
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedUri = it
            selectedFileName = it.lastPathSegment?.substringAfterLast("/") ?: "document"
        }
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
                        text = "Upload Document",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "PDF, JPG, PNG supported",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Upload Zone
            if (selectedUri == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .scale(pulseScale)
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(TrustCyan, TrustBlueLight)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            SurfaceCard.copy(alpha = 0.5f),
                            RoundedCornerShape(24.dp)
                        )
                        .clickable { filePicker.launch("*/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("📂", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tap to select document",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "or drop your file here",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FormatChip("PDF")
                            FormatChip("JPG")
                            FormatChip("PNG")
                            FormatChip("DOCX")
                        }
                    }
                }
            } else {
                // Selected File Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, ScoreHigh.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .background(
                                    TrustBlueLight.copy(alpha = 0.2f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📄", fontSize = 26.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = selectedFileName ?: "document",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(ScoreHigh, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Ready to verify",
                                    fontSize = 11.sp,
                                    color = ScoreHigh
                                )
                            }
                        }
                        if (!isUploading) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(SurfaceElevated, CircleShape)
                                    .clickable {
                                        selectedUri = null
                                        selectedFileName = null
                                        viewModel.reset()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", fontSize = 14.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progress Card
            if (isUploading) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Analyzing document...",
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${(uploadProgress * 100).toInt()}%",
                                fontSize = 14.sp,
                                color = TrustCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { uploadProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = TrustCyan,
                            trackColor = SurfaceElevated
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = statusMessage,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Info Cards
            if (!isUploading) {
                Text(
                    text = "What we analyze",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                AnalysisInfoCard("🔎", "OCR Text Extraction", "Extract and verify all text content")
                Spacer(modifier = Modifier.height(10.dp))
                AnalysisInfoCard("🌐", "Source Verification", "Cross-check against trusted databases")
                Spacer(modifier = Modifier.height(10.dp))
                AnalysisInfoCard("🤖", "AI Authenticity Check", "Deep learning detects tampering")
                Spacer(modifier = Modifier.height(10.dp))
                AnalysisInfoCard("📊", "Trust Score", "Get a 0-100 authenticity score")
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Verify Button
            Button(
                onClick = {
                    if (selectedUri != null && !isUploading) {
                        viewModel.verifyDocument(selectedUri!!)
                    } else if (selectedUri == null) {
                        filePicker.launch("*/*")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedUri != null) TrustCyan else TrustBlueLight,
                    contentColor = TrustBlueDark
                ),
                enabled = !isUploading
            ) {
                Text(
                    text = if (selectedUri != null) "Verify Document" else "Select Document",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                if (selectedUri != null && !isUploading) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🔍", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun FormatChip(format: String) {
    Box(
        modifier = Modifier
            .background(TrustBlueLight.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = format, fontSize = 11.sp, color = TrustCyan, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun AnalysisInfoCard(emoji: String, title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(TrustBlueLight.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(emoji, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}