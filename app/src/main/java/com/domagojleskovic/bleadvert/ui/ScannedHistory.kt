package com.domagojleskovic.bleadvert.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScannedHistory(innerPadding: PaddingValues){

    if(true){ // Ako je lista scanned historija prazna centriraj
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ){
            Text(text = "No events found...")
        }
    }else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
                .padding(16.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item{

            }
        }
    }
}