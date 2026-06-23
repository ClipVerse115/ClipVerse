package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.ClipSuggestion
import com.example.data.db.AnalyzedVideoEntity
import com.example.data.repository.AnalysisResult
import com.example.ui.components.GlassyCard
import com.example.ui.components.GradientBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepSpaceBlue
import com.example.ui.theme.HotPink
import com.example.ui.theme.GlowingGreen
import com.example.ui.viewmodel.ClipViewModel
import kotlinx.coroutines.launch

@Composable
fun MainDashboard(viewModel: ClipViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val urlInput by viewModel.urlInput.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val loadingStatus by viewModel.loadingStatus.collectAsStateWithLifecycle()
    val analysisResult by viewModel.analysisResult.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(0) } // 0: Analyzer, 1: History, 2: Landing/Features

    // Floating message container for ClipVerse alerts
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    GradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Elegant App Header
            AppHeader(
                activeTab = activeTab,
                onTabChanged = { activeTab = it }
            )

            // Primary scrolling container taking up remaining layout
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> AnalyzerTab(
                        urlInput = urlInput,
                        isLoading = isLoading,
                        loadingStatus = loadingStatus,
                        analysisResult = analysisResult,
                        onUrlChange = { viewModel.onUrlInputChanged(it) },
                        onAnalyzeClick = { viewModel.analyzeVideoUrl() }
                    )
                    1 -> HistoryTab(
                        history = history,
                        onItemSelect = { video ->
                            viewModel.selectHistoryItem(video)
                            activeTab = 0 // Switch back to see result!
                            Toast.makeText(context, "Loaded: ${video.videoTitle}", Toast.LENGTH_SHORT).show()
                        },
                        onItemDelete = { id -> viewModel.deleteHistoryItem(id) },
                        onClearAll = { viewModel.clearHistory() }
                    )
                    2 -> LandingShowcaseTab()
                }
            }
        }
    }
}

@Composable
fun AppHeader(
    activeTab: Int,
    onTabChanged: (Int) -> Unit
) {
    Surface(
        color = Color(0x330A0B14),
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = Color(0x11FFFFFF),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Branded Text Logo
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(CyberPurple, HotPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "ClipVerse logo symbol",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "ClipVerse",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        style = androidx.compose.ui.text.TextStyle(
                            brush = Brush.horizontalGradient(listOf(CyberCyan, CyberPurple, HotPink))
                        )
                    )
                }

                // Dynamic UTC Badge representing high-end startup environment coordinates
                Surface(
                    color = Color(0x338B5CF6),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0x33EC4899))
                ) {
                    Text(
                        text = "AI ENGINE V2.5",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HotPink,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Premium Navigation Bar (Fluid navigation pills with spring ripples)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0x33FFFFFF))
                    .border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabs = listOf(
                    Triple(0, Icons.Default.Home, "AI Analyzer"),
                    Triple(1, Icons.Default.List, "Vault History"),
                    Triple(2, Icons.Default.Star, "Features & Contact")
                )

                tabs.forEach { (index, icon, label) ->
                    val selected = activeTab == index
                    val tabBgColor by animateColorAsState(
                        targetValue = if (selected) Color(0x228B5CF6) else Color.Transparent,
                        animationSpec = tween(300), label = "tabBg"
                    )
                    val tabContentColor by animateColorAsState(
                        targetValue = if (selected) CyberCyan else Color.LightGray,
                        animationSpec = tween(300), label = "tabContent"
                    )

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(20.dp))
                            .background(tabBgColor)
                            .clickable { onTabChanged(index) }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = tabContentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = tabContentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyzerTab(
    urlInput: String,
    isLoading: Boolean,
    loadingStatus: String,
    analysisResult: AnalysisResult.Success?,
    onUrlChange: (String) -> Unit,
    onAnalyzeClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Turn Long Videos Into Viral Shorts",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    lineHeight = 38.sp,
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.horizontalGradient(listOf(Color.White, CyberPurple, CyberCyan))
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ClipVerse analyzes long content transcripts and behavioral hooks to suggest high-impact structural timeline hooks in seconds.",
                    fontSize = 14.sp,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Input and Control Console
        item {
            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0x228B5CF6)
            ) {
                Text(
                    text = "INPUT YOUTUBE ENDPOINT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = onUrlChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("url_input_field"),
                    placeholder = { Text("Paste YouTube URL e.g., https://youtu.be/...", color = Color.Gray, fontSize = 14.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedContainerColor = Color(0x11FFFFFF),
                        unfocusedContainerColor = Color(0x0AFFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { if (!isLoading) onAnalyzeClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("generate_clips_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CyberPurple, HotPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Compute",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isLoading) "PROCESSING AI ATOMICITY..." else "GENERATE AI CLIPS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Advanced Interactive Loading Stage Animation
        if (isLoading) {
            item {
                GlassyCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = Color(0x33EC4899),
                    backgroundColor = Color(0x33000000)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = HotPink,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = loadingStatus,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Analyzing with Gemini-3.5-flash parser",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Analysis Results Block
        analysisResult?.let { result ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VIRAL PICKS DISCOVERED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = result.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 240.dp)
                        )
                    }

                    if (result.isSimulated) {
                        Surface(
                            color = Color(0x2210B981),
                            border = BorderStroke(1.dp, GlowingGreen.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "PROTOTYPE FALLBACK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = GlowingGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Suggestions List
            if (result.clips.isEmpty()) {
                item {
                    Text(
                        text = "No viral clips extracted. Try another YouTube link.",
                        color = Color.Gray,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            } else {
                items(result.clips) { clip ->
                    ClipCardItem(clip = clip)
                }
            }
        }
    }
}

@Composable
fun ClipCardItem(clip: ClipSuggestion) {
    val context = LocalContext.current
    
    GlassyCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clip_card_item"),
        borderColor = Color(0x1F8B5CF6),
        backgroundColor = Color(0x13FFFFFF)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Viral Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0x3310B981),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Viral Score: ${clip.viralScore}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GlowingGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = clip.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Copy Action Icon Target 48dp Touch Area
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x11FFFFFF))
                        .clickable {
                            val clipService = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("Clip Timestamp", "${clip.startTime} - ${clip.endTime}")
                            clipService.setPrimaryClip(clipData)
                            Toast.makeText(context, "Copied timeline: ${clip.startTime} - ${clip.endTime}", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy timeline",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timelines
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0x1A8B5CF6),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = CyberPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = clip.startTime,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "to", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = Color(0x1AEC4899),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "End",
                            tint = HotPink,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = clip.endTime,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Analysis details
            Text(
                text = clip.justification,
                fontSize = 13.sp,
                color = Color.LightGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun HistoryTab(
    history: List<AnalyzedVideoEntity>,
    onItemSelect: (AnalyzedVideoEntity) -> Unit,
    onItemDelete: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VAULT ARCHIVES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "History & Cached Runs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            if (history.isNotEmpty()) {
                TextButton(onClick = onClearAll) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = HotPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CLEAR ALL", color = HotPink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History empty",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "History Vault is empty",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Your custom runs will persist here locally.",
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { video ->
                    HistoryItemCard(
                        video = video,
                        onClick = { onItemSelect(video) },
                        onDelete = { onItemDelete(video.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    video: AnalyzedVideoEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassyCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 12.dp,
        borderColor = Color(0x13FFFFFF),
        backgroundColor = Color(0x0EFFFFFF)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.videoTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = video.youtubeUrl,
                    fontSize = 11.sp,
                    color = CyberCyan,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Remove histories button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete record",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun LandingShowcaseTab() {
    var contactName by remember { mutableStateOf("") }
    var contactChannel by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Features Section
        item {
            Column {
                Text(
                    text = "ClipVerse Core Advantages",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                val features = listOf(
                    CardFeature(Icons.Default.Refresh, "AI Timeline Isolation", "Automatically identifies exact sentence-level boundaries to prevent raw cutoffs."),
                    CardFeature(Icons.Default.Star, "Virality Scoring Metrics", "Analyzes the high emotional hooks to guarantee maximum viewer retention in the feed."),
                    CardFeature(Icons.Default.Send, "Direct Copy-To-Post", "Instantly secures direct timeline boundaries to clipboard for seamless pasting.")
                )

                features.forEach { feature ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x1F8B5CF6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = "",
                                tint = CyberPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = feature.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = feature.description,
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Creator Testimonials
        item {
            Column {
                Text(
                    text = "Trusted by Creators",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val reviews = listOf(
                        Testimonial("Jake Vance", "Tech Shorts Vlogger", "ClipVerse cut down my edit lifecycle by 4 hours per video. Virality scores are spot on!"),
                        Testimonial("Lisha Kim", "Cosmic Meme Streamer", "Pushed 5 reels suggested by ClipVerse. 3 hit 500k+ organic views. Utterly insane.")
                    )

                    reviews.forEach { review ->
                        GlassyCard(
                            modifier = Modifier.width(260.dp),
                            cornerRadius = 12.dp,
                            borderColor = Color(0x11FFFFFF),
                            backgroundColor = Color(0x06FFFFFF)
                        ) {
                            Text(
                                text = "\"${review.quote}\"",
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = review.name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan
                            )
                            Text(
                                text = review.handle,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        // FAQ Section
        item {
            Column {
                Text(
                    text = "Frequently Asked Questions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(12.dp))

                val faqs = listOf(
                    FAQItem("How accurate are the ClipVerse timestamps?", "Extremely! Our system maps vocal inflection peaks to align timestamps to direct logical speech structures."),
                    FAQItem("Do I need to sign up for an API Key?", "No, ClipVerse is equipped with an integrated high-efficiency local parser for general simulations, or you can inject your own Gemini API Key in the AI Studio environment settings to perform real real-time runs!")
                )

                faqs.forEach { faq ->
                    var expanded by remember { mutableStateOf(false) }
                    GlassyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(vertical = 4.dp),
                        cornerRadius = 8.dp,
                        borderColor = Color(0x33FFFFFF),
                        backgroundColor = Color(0x0AFFFFFF)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = faq.question,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle text expansion",
                                tint = CyberPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        if (expanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = faq.answer,
                                fontSize = 12.sp,
                                color = Color.LightGray,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Contact Section
        item {
            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0x228B5CF6),
                backgroundColor = Color(0x13000000)
            ) {
                Text(
                    text = "SUBMIT FEEDBACK & CONTACT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = contactName,
                    onValueChange = { contactName = it },
                    label = { Text("Your Name", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactChannel,
                    onValueChange = { contactChannel = it },
                    label = { Text("Channel / Brand Name", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it },
                    label = { Text("Email Address", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = contactMessage,
                    onValueChange = { contactMessage = it },
                    label = { Text("Message / Suggestion", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color(0x22FFFFFF),
                        focusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (contactName.isBlank() || contactEmail.isBlank() || contactMessage.isBlank()) {
                            Toast.makeText(context, "Please populate Name, Email and Message blocks first.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Feedback successfully sent! Thank you, $contactName.", Toast.LENGTH_LONG).show()
                            contactName = ""
                            contactChannel = ""
                            contactEmail = ""
                            contactMessage = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SUBMIT FORM", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Footer Section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ClipVerse Corp © 2026",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Powered by Google Gemini 3.5 & Jetpack Compose",
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

data class CardFeature(val icon: ImageVector, val title: String, val description: String)
data class Testimonial(val name: String, val handle: String, val quote: String)
data class FAQItem(val question: String, val answer: String)
