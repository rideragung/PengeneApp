package com.example.pengene.di

import com.example.pengene.domain.repository.IAuthRepository
import com.example.pengene.domain.repository.IStorageRepository
import com.example.pengene.domain.repository.IWishlistRepository
import com.example.pengene.domain.usecase.auth.*
import com.example.pengene.domain.usecase.wishlist.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    // Auth Use Cases
    @Provides
    @Singleton
    fun provideLoginUseCase(authRepository: IAuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(authRepository: IAuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(authRepository: IAuthRepository): LogoutUseCase {
        return LogoutUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(authRepository: IAuthRepository): GetCurrentUserUseCase {
        return GetCurrentUserUseCase(authRepository)
    }

    // Wishlist Use Cases
    @Provides
    @Singleton
    fun provideGetWishlistItemsUseCase(wishlistRepository: IWishlistRepository): GetWishlistItemsUseCase {
        return GetWishlistItemsUseCase(wishlistRepository)
    }

    @Provides
    @Singleton
    fun provideAddWishlistItemUseCase(wishlistRepository: IWishlistRepository): AddWishlistItemUseCase {
        return AddWishlistItemUseCase(wishlistRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateWishlistItemUseCase(wishlistRepository: IWishlistRepository): UpdateWishlistItemUseCase {
        return UpdateWishlistItemUseCase(wishlistRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteWishlistItemUseCase(wishlistRepository: IWishlistRepository): DeleteWishlistItemUseCase {
        return DeleteWishlistItemUseCase(wishlistRepository)
    }

    @Provides
    @Singleton
    fun provideUploadImageUseCase(storageRepository: IStorageRepository): UploadImageUseCase {
        return UploadImageUseCase(storageRepository)
    }
}