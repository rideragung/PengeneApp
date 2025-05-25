package com.example.pengene.ui.screens.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pengene.domain.model.WishlistItem
import com.example.pengene.domain.usecase.wishlist.AddWishlistItemUseCase
import com.example.pengene.domain.usecase.wishlist.DeleteWishlistItemUseCase
import com.example.pengene.domain.usecase.wishlist.GetWishlistItemsUseCase
import com.example.pengene.domain.usecase.wishlist.UpdateWishlistItemUseCase
import com.example.pengene.domain.usecase.wishlist.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val getWishlistItemsUseCase: GetWishlistItemsUseCase,
    private val addWishlistItemUseCase: AddWishlistItemUseCase,
    private val updateWishlistItemUseCase: UpdateWishlistItemUseCase,
    private val deleteWishlistItemUseCase: DeleteWishlistItemUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _wishlistItems = MutableStateFlow<List<WishlistItem>>(emptyList())
    val wishlistItems: StateFlow<List<WishlistItem>> = _wishlistItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Form states
    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName.asStateFlow()

    private val _estimatedPrice = MutableStateFlow("")
    val estimatedPrice: StateFlow<String> = _estimatedPrice.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri.asStateFlow()

    private val _editingItem = MutableStateFlow<WishlistItem?>(null)
    val editingItem: StateFlow<WishlistItem?> = _editingItem.asStateFlow()

    init {
        loadWishlistItems()
    }

    fun loadWishlistItems() {
        viewModelScope.launch {
            getWishlistItemsUseCase().collect { items ->
                _wishlistItems.value = items
            }
        }
    }

    fun setItemName(name: String) {
        _itemName.value = name
    }

    fun setEstimatedPrice(price: String) {
        _estimatedPrice.value = price
    }

    fun setDescription(desc: String) {
        _description.value = desc
    }

    fun setSelectedImageUri(uri: String?) {
        _selectedImageUri.value = uri
    }

    fun startEditing(item: WishlistItem) {
        _editingItem.value = item
        _itemName.value = item.itemName
        _estimatedPrice.value = item.estimatedPrice?.toString() ?: ""
        _description.value = item.description ?: ""
        _selectedImageUri.value = item.imageUrl
    }

    fun clearForm() {
        _editingItem.value = null
        _itemName.value = ""
        _estimatedPrice.value = ""
        _description.value = ""
        _selectedImageUri.value = null
        _errorMessage.value = null
    }

    fun saveWishlistItem() {
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null

            try {
                // Validate item name
                if (itemName.value.isBlank()) {
                    _errorMessage.value = "Nama barang tidak boleh kosong"
                    _isSaving.value = false
                    return@launch
                }
                
                // Validate price format if provided
                val price = if (estimatedPrice.value.isNotBlank()) {
                    estimatedPrice.value.toDoubleOrNull() ?: run {
                        _errorMessage.value = "Format harga tidak valid"
                        _isSaving.value = false
                        return@launch
                    }
                } else null
                
                var imageUrl = selectedImageUri.value

                // Upload image if selected and not already a URL
                if (selectedImageUri.value != null && !selectedImageUri.value!!.startsWith("http")) {
                    try {
                        val uploadResult = uploadImageUseCase(selectedImageUri.value!!)
                        if (uploadResult.isSuccess) {
                            imageUrl = uploadResult.getOrNull()
                        } else {
                            _errorMessage.value = "Gagal upload gambar: ${uploadResult.exceptionOrNull()?.message ?: "Terjadi kesalahan saat upload gambar"}"
                            _isSaving.value = false
                            return@launch
                        }
                    } catch (e: Exception) {
                        _errorMessage.value = "Gagal upload gambar: ${e.message ?: "Terjadi kesalahan saat upload gambar"}"
                        _isSaving.value = false
                        return@launch
                    }
                }

                val item = if (editingItem.value != null) {
                    editingItem.value!!.copy(
                        itemName = itemName.value,
                        estimatedPrice = price,
                        description = description.value.ifBlank { null },
                        imageUrl = imageUrl
                    )
                } else {
                    WishlistItem(
                        itemName = itemName.value,
                        estimatedPrice = price,
                        description = description.value.ifBlank { null },
                        imageUrl = imageUrl
                    )
                }

                val result = if (editingItem.value != null) {
                    updateWishlistItemUseCase(item)
                } else {
                    addWishlistItemUseCase(item)
                }

                if (result.isSuccess) {
                    // Success - clear form and reload items
                    loadWishlistItems()
                    clearForm()
                } else {
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal menyimpan item"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteWishlistItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = deleteWishlistItemUseCase(itemId)
            if (result.isSuccess) {
                loadWishlistItems()
            } else {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal menghapus item"
            }

            _isLoading.value = false
        }
    }

    /**
     * Mengganti status pembelian item wishlist tanpa menampilkan loading indicator
     * untuk pengalaman pengguna yang lebih mulus
     */
    fun togglePurchased(item: WishlistItem) {
        viewModelScope.launch {
            try {
                // Langsung update UI terlebih dahulu (optimistic update)
                val updatedItem = item.copy(isPurchased = !item.isPurchased)
                
                // Update lokal list untuk UI yang responsif
                val currentItems = _wishlistItems.value.toMutableList()
                val index = currentItems.indexOfFirst { it.id == item.id }
                if (index != -1) {
                    currentItems[index] = updatedItem
                    _wishlistItems.value = currentItems
                }
                
                // Kirim update ke backend tanpa mengubah loading state
                val result = updateWishlistItemUseCase(updatedItem)
                
                if (!result.isSuccess) {
                    // Jika gagal, kembalikan state sebelumnya dan tampilkan error
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "Gagal mengubah status pembelian"
                    
                    // Kembalikan UI ke state sebelumnya
                    val revertItems = _wishlistItems.value.toMutableList()
                    val revertIndex = revertItems.indexOfFirst { it.id == item.id }
                    if (revertIndex != -1) {
                        revertItems[revertIndex] = item // Gunakan item asli (sebelum diubah)
                        _wishlistItems.value = revertItems
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Terjadi kesalahan saat mengubah status pembelian"
                
                // Jika terjadi exception, pastikan UI dikembalikan ke state awal
                val revertItems = _wishlistItems.value.toMutableList()
                val revertIndex = revertItems.indexOfFirst { it.id == item.id }
                if (revertIndex != -1) {
                    revertItems[revertIndex] = item
                    _wishlistItems.value = revertItems
                }
            }
            // Tidak perlu mengatur loading state sama sekali untuk operasi ini
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    
    fun loadWishlistItemById(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Pastikan data wishlist sudah dimuat terlebih dahulu
            getWishlistItemsUseCase().collect { items -> 
                // Update wishlist items terlebih dahulu
                _wishlistItems.value = items
                
                // Sekarang cari item berdasarkan ID
                val item = items.find { it.id == itemId }
                if (item != null) {
                    // Start editing dengan item yang ditemukan
                    startEditing(item)
                    _isLoading.value = false
                    return@collect
                } else {
                    // Item tidak ditemukan, tampilkan error
                    _errorMessage.value = "Item tidak ditemukan (ID: $itemId)"
                    _isLoading.value = false
                }
            }
        }
    }
}