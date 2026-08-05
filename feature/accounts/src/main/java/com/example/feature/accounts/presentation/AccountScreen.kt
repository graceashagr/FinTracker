package com.example.feature.accounts.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.core.domain.model.Account
import com.example.core.domain.model.AccountType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel()
) {
    val state by viewModel.accountUiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    AccountScreenContent(
        accounts = state.accounts,
        isLoading = state.isLoading,
        onAddAccount = { name, type ->
            viewModel.addAccount(name, type)
        },
        onDeleteAccount = { accountId ->
            viewModel.deleteAccount(accountId)
        },
        showAddDialog = showAddDialog,
        onShowAddDialogChange = { value ->
            showAddDialog = value
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreenContent(
    accounts: List<Account>,
    isLoading: Boolean,
    onAddAccount: (String, AccountType) -> Unit,
    onDeleteAccount: (String) -> Unit,
    showAddDialog: Boolean = false,
    onShowAddDialogChange: (Boolean) -> Unit = {},
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onShowAddDialogChange(!showAddDialog)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Account"
                )
            }
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            LazyColumn {
                items(
                    items = accounts,
                    key = { it.id }
                ) { item ->
                    ListItem(
                        headlineContent = {
                            Text(text = item.name)
                        },
                        supportingContent = {
                            Text(text = item.type.name)
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    onDeleteAccount(item.id)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Account"
                                )
                            }
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            var accountName by remember { mutableStateOf("") }
            var dropdownExpanded by remember { mutableStateOf(false) }
            var accountType by remember { mutableStateOf(AccountType.CHECKING) }
            BasicAlertDialog(
                onDismissRequest = { onShowAddDialogChange(false) },
                modifier = Modifier,
                properties = DialogProperties(),
                content = {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = accountName,
                            onValueChange = { value ->
                                accountName = value
                            },
                            label = { Text("Account Name") },
                            modifier = Modifier.testTag("accountNameInput")
                        )
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it },
                        ) { 
                            OutlinedTextField(
                                value = accountType.name,
                                onValueChange = {

                                },
                                readOnly = true,
                                label = { Text("Account Type") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                modifier = Modifier
                                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                    .testTag("accountTypeInput")
                            )

                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.testTag("accountTypeDropdown")
                            ) {
                                AccountType.entries.forEach { type->
                                    DropdownMenuItem(
                                        text = { Text(type.name) },
                                        onClick = {
                                            accountType = type
                                            dropdownExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                        modifier = Modifier.testTag("accountTypeItem_${type.name}")
                                    )
                                }
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onShowAddDialogChange(false)
                                    onAddAccount(
                                        accountName,
                                        accountType
                                    )
                                }
                            ) {
                                Text("Add Account")
                            }
                            Button(
                                onClick = {
                                    onShowAddDialogChange(false)
                                }
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            )
        }
    }


}


@Preview
@Composable
fun AccountScreenPreview() {
    MaterialTheme {
        AccountScreenContent(
            accounts = listOf(
                Account(id = "1", name = "Checking Account", type = AccountType.CHECKING),
                Account(id = "2", name = "Cash Wallet", type = AccountType.CASH),
                Account(id = "3", name = "Credit Card", type = AccountType.CREDIT_CARD)
            ),
            isLoading = false,
            onAddAccount = { _, _ -> },
            onDeleteAccount = {}
        )
    }
}