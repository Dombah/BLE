package com.domagojleskovic.bleadvert.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.Reward
import com.domagojleskovic.bleadvert.ui.theme.BrightRed
import com.domagojleskovic.bleadvert.ui.theme.DarkGrayishBlue
import com.domagojleskovic.bleadvert.ui.theme.DarkSurface
import com.domagojleskovic.bleadvert.ui.theme.MainBlue
import com.domagojleskovic.bleadvert.ui.theme.Orange
import com.domagojleskovic.bleadvert.ui.theme.Typography
import com.domagojleskovic.bleadvert.viewmodels.RewardsViewModel
@Composable
fun RewardItem(
    modifier: Modifier = Modifier,
    reward: Reward,
    isInDarkMode : Boolean = isSystemInDarkTheme()
) {
    val uiColor = if(isInDarkMode) Color.White else Color.Black
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if(isInDarkMode)
                DarkGrayishBlue else Color(0xFFE3E3E3)
        ),
    ) {
        Row(
            modifier = modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SubcomposeAsyncImage(
                model = reward.image,
                contentDescription = "Reward Image",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(60.dp)
                        )
                    }

                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = reward.title,
                    color = uiColor,
                    style = Typography.headlineMedium.copy(fontSize = 20.sp),
                )
                Text(
                    text = reward.description,
                    color = uiColor.copy(alpha = 0.5f),
                    style = Typography.titleSmall.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun RewardsScreen(innerPadding: PaddingValues, rewardsViewModel: RewardsViewModel){

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val uiColor = if(isSystemInDarkTheme) Color.White else Color.Black
    val listOfRewards = rewardsViewModel.rewards.collectAsState().value
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme) DarkSurface else Color.White)
    ) {
        PageName("Rewards")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 175.dp)
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = if(isSystemInDarkTheme)
                    BrightRed.copy(alpha = 0.25f) else MainBlue.copy(alpha = 0.75f)
            ),
            shape = RoundedCornerShape(
                topEnd = 16.dp,
                bottomStart = 16.dp,
                topStart = 4.dp,
                bottomEnd = 4.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "You've scanned:",
                    color = Color.White,
                    style = Typography.labelMedium.copy(fontSize = 20.sp),

                    )
                Text(
                    text = "${EmailPasswordAuthenticator.currentUser?.scans} Beacons!",
                    color = Color.White,
                    style = Typography.headlineLarge.copy(fontSize = 32.sp),

                    )
            }
        }
        if(listOfRewards.isNotEmpty()){
            LazyColumn(
                modifier = Modifier
                    .padding(top = 300.dp, start = 8.dp, end = 8.dp)
                    .padding(bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding())
                    .consumeWindowInsets(innerPadding)
                    .fillMaxWidth(),
            ) {
                items(listOfRewards) { reward ->
                    RewardItem(
                        reward = reward,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }else{
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "No rewards found",
                        color = uiColor,
                        style = Typography.labelLarge.copy(fontSize = 30.sp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Start scanning around to earn cool rewards",
                        color = uiColor.copy(alpha = 0.8f),
                        style = Typography.labelMedium.copy(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        }

    }
}

