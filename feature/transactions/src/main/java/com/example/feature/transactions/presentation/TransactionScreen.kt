package com.example.feature.transactions.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransactionScreen(
    viewModel : TransactionViewModel = hiltViewModel(),
    showDebugControls: Boolean = false
){

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.addClicked(
                        accountId = "demo-account",
                        categoryId = "demo-category",
                        amount = 1000,
                        note = "Coffee"
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction"
                )
            }

        }
    ) { paddingValues ->
        if(state.isLoading){
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues)
            ) {
                if (showDebugControls) {
                    Row(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { viewModel.onForceSyncClick() }) {
                            Text("Force sync")
                        }
                        Spacer(Modifier.width(24.dp))
                        Text("Pending: ${state.pendingCount}")
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                ) {
                    items(
                        items = state.transactions,
                        key = { it.id }
                    ) { item ->

                        val dismissedState = rememberSwipeToDismissBoxState(
                            confirmValueChange = {
                                if (it == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.deleteClick(item.id)
                                    true
                                } else false
                            }
                        )
                        SwipeToDismissBox(
                            state = dismissedState,
                            backgroundContent = {

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text("Deleting...")
                                }
                            },
                            content = {
                                ListItem(
                                    headlineContent = { Text("${item.note}") },
                                    supportingContent = { Text(" ${item.amountCents / 100.0}") },
                                    trailingContent = {
//                                        Button(
//                                            onClick = {
//
//                                            }
//                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Create,
                                                contentDescription = "Edit Transaction",
                                                modifier = Modifier.clickable{
                                                    viewModel.updateClick(
                                                        item.copy(note = "${item.note} (edited)")
                                                    )
                                                }
                                            )
//                                        }
                                    }
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TransactionScreenPreview(){
    TransactionScreen()
}