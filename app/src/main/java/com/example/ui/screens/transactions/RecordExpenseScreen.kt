package com.example.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.local.DefaultSeedData
import com.example.ui.MainViewModel
import com.example.utils.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordExpenseScreen(
    viewModel: MainViewModel,
    presetGatheringId: String? = null,
    presetGatheringName: String? = null,
    onBack: () -> Unit
) {
    val expenseCategories by viewModel.expenseCategories.collectAsState()
    val gatherings by viewModel.allGatherings.collectAsState()
    val churchProfile by viewModel.churchProfile.collectAsState()

    val currency = churchProfile?.defaultCurrency ?: "UGX"

    var title by remember { mutableStateOf("") }

    val defaultCategory = expenseCategories.firstOrNull()
    var selectedCategory by remember { mutableStateOf(defaultCategory) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var amountText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf(DefaultSeedData.paymentMethods.first()) }
    var paymentExpanded by remember { mutableStateOf(false) }

    var payee by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }

    var selectedGatheringId by remember { mutableStateOf(presetGatheringId) }
    var selectedGatheringName by remember { mutableStateOf(presetGatheringName ?: "None (General Expense)") }
    var gatheringExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Expense") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Record Church Expense",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Record Rent, Utilities, Transport, Allowances, or Equipment",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Expense Title *") },
                placeholder = { Text("e.g. Church Rent / Sound Equipment") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Expense Category *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    expenseCategories.filter { !it.isDisabled }.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount Input
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount ($currency) *") },
                placeholder = { Text("Enter amount greater than zero") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Method Dropdown
            ExposedDropdownMenuBox(
                expanded = paymentExpanded,
                onExpandedChange = { paymentExpanded = !paymentExpanded }
            ) {
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Payment Method") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = paymentExpanded,
                    onDismissRequest = { paymentExpanded = false }
                ) {
                    DefaultSeedData.paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method) },
                            onClick = {
                                paymentMethod = method
                                paymentExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = payee,
                onValueChange = { payee = it },
                label = { Text("Payee / Vendor / Person Paid (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Related Gathering Dropdown
            ExposedDropdownMenuBox(
                expanded = gatheringExpanded,
                onExpandedChange = { gatheringExpanded = !gatheringExpanded }
            ) {
                OutlinedTextField(
                    value = selectedGatheringName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Related Gathering (Optional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gatheringExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = gatheringExpanded,
                    onDismissRequest = { gatheringExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None (General Expense)") },
                        onClick = {
                            selectedGatheringId = null
                            selectedGatheringName = "None (General Expense)"
                            gatheringExpanded = false
                        }
                    )
                    gatherings.forEach { g ->
                        DropdownMenuItem(
                            text = { Text("${g.name} (${g.gatheringTypeName})") },
                            onClick = {
                                selectedGatheringId = g.id
                                selectedGatheringName = g.name
                                gatheringExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = referenceNumber,
                onValueChange = { referenceNumber = it },
                label = { Text("Invoice / Voucher Ref No. (Optional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description or Purpose (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val parsedAmount = CurrencyFormatter.parseAmount(amountText)
                    val category = selectedCategory

                    if (title.isBlank()) {
                        errorMessage = "Please enter an expense title."
                    } else if (category == null) {
                        errorMessage = "Please select an expense category."
                    } else if (parsedAmount <= 0L) {
                        errorMessage = "Enter an amount greater than zero."
                    } else {
                        viewModel.recordExpense(
                            title = title,
                            categoryId = category.id,
                            categoryName = category.name,
                            amount = parsedAmount,
                            dateMillis = System.currentTimeMillis(),
                            paymentMethod = paymentMethod,
                            payee = payee.ifBlank { null },
                            description = description.ifBlank { null },
                            attachmentUri = null,
                            referenceNumber = referenceNumber.ifBlank { null },
                            gatheringId = selectedGatheringId,
                            gatheringName = if (selectedGatheringId != null) selectedGatheringName else null
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Save Expense Record")
            }
        }
    }
}
