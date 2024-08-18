package com.domagojleskovic.bleadvert.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.domagojleskovic.bleadvert.EmailPasswordAuthenticator
import com.domagojleskovic.bleadvert.Reward
import com.domagojleskovic.bleadvert.viewmodels.RewardsViewModel

@Composable
fun Rewards(innerPadding: PaddingValues, rewardsViewModel: RewardsViewModel){

    val context = LocalContext.current
    val buttonCurvature = 32.dp
    val rewards by rewardsViewModel.rewards.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(innerPadding)
            .padding(16.dp),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
            items(rewards){ reward ->

            }
    }
}