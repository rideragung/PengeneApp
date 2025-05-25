package com.example.pengene.ui.screens.wishlist

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddEditWishlistScreen(
    itemId: String? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val itemName by viewModel.itemName.collectAsStateWithLifecycle()
    val estimatedPrice by viewModel.estimatedPrice.collectAsStateWithLifecycle()
    val description by viewModel.description.collectAsStateWithLifecycle()
    val selectedImageUri by viewModel.selectedImageUri.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val isEditing = itemId != null
    
    // State untuk mencegah multiple back press terlalu cepat
    var isBackButtonEnabled by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.setSelectedImageUri(it.toString()) }
    }

    // Initialize editing mode
    LaunchedEffect(itemId) {
        if (itemId != null) {
            // Load the specific item for editing
            viewModel.loadWishlistItemById(itemId)
        } else {
            viewModel.clearForm()
        }
    }
    
    // Handler untuk tombol back dengan debounce
    val handleBackPress: () -> Unit = {
        if (isBackButtonEnabled) {
            isBackButtonEnabled = false
            onNavigateBack()
            // Re-enable back button after delay
            coroutineScope.launch {
                delay(300) // Delay 300ms sebelum tombol back bisa ditekan lagi
                isBackButtonEnabled = true
            }
        }
    }
    
    // Handle system back button press with debounce
    BackHandler(enabled = isBackButtonEnabled) {
        handleBackPress()
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Wishlist" else "Tambah Wishlist",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = handleBackPress,
                        enabled = isBackButtonEnabled
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Preview gambar",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri != null) "Ganti Gambar" else "Pilih Gambar")
                    }
                }
            }

            // Item name
            OutlinedTextField(
                value = itemName,
                onValueChange = viewModel::setItemName,
                label = { Text("Nama Barang *") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                isError = itemName.isBlank() && errorMessage != null
            )

            // Estimated price
            OutlinedTextField(
                value = estimatedPrice,
                onValueChange = viewModel::setEstimatedPrice,
                label = { Text("Perkiraan Harga") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                placeholder = { Text("Contoh: 150000") }
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = viewModel::setDescription,
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                minLines = 3,
                maxLines = 5,
                placeholder = { Text("Deskripsi tambahan tentang barang ini...") }
            )

            // Error message
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Save button
            Button(
                onClick = {
                    viewModel.saveWishlistItem()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving && itemName.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isEditing) "Update" else "Simpan")
            }
        }
    }

    // Handle save success
    val previousSaving = remember { mutableStateOf(false) }
    
    LaunchedEffect(isSaving, errorMessage) {
        // Check if we just finished saving and there's no error
        if (previousSaving.value && !isSaving && errorMessage == null) {
            // This means the save operation completed successfully
            kotlinx.coroutines.delay(300)
            onSaveSuccess()
        }
        previousSaving.value = isSaving
    }
}