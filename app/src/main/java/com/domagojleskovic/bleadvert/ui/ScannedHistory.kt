package com.domagojleskovic.bleadvert.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.domagojleskovic.bleadvert.DatabaseAccessObject
import com.domagojleskovic.bleadvert.Event
import com.domagojleskovic.bleadvert.ui.theme.DarkGrayishBlue
import com.domagojleskovic.bleadvert.ui.theme.DarkSurface
import com.domagojleskovic.bleadvert.ui.theme.Orange
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.ScannedHistoryViewModel






@Composable
fun ScannedHistoryItem(modifier: Modifier = Modifier, event: Event, isInDarkMode : Boolean) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "ArrowRotationAnimation"
    )

    Card(
        modifier = modifier
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if(isInDarkMode)
                DarkGrayishBlue else Color(0xFFE3E3E3)
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .rotate(rotationAngle)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = event.title,
                    style = Typography.titleMedium.copy(fontSize = 24.sp)
                )
            }
            if (isExpanded) {
                Text(
                    text = event.description,
                    style = Typography.labelMedium.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ScannedHistory(innerPadding: PaddingValues, scannedHistoryViewModel: ScannedHistoryViewModel){

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val scannedHistory = scannedHistoryViewModel.history.collectAsState().value

    if(scannedHistory.isEmpty()){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(text = "No events found...")
        }
    }else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSystemInDarkTheme) DarkSurface else Color.White)
        ){
            PageName(title = "History")
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
                    .padding(16.dp)
                    .padding(top = 100.dp)
                    .padding(
                        bottom = WindowInsets.systemBars
                            .asPaddingValues()
                            .calculateBottomPadding()
                    ),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scannedHistory){ event ->
                    ScannedHistoryItem(event = event, modifier = Modifier.fillMaxWidth(), isInDarkMode = isSystemInDarkTheme)
                }
            }
        }
    }
}