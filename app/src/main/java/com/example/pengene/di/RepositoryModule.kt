package com.example.pengene.di

import com.example.pengene.data.repository.AuthRepository
import com.example.pengene.data.repository.StorageRepository
import com.example.pengene.data.repository.WishlistRepository
import com.example.pengene.domain.repository.IAuthRepository
import com.example.pengene.domain.repository.IStorageRepository
import com.example.pengene.domain.repository.IWishlistRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepository: AuthRepository): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(wishlistRepository: WishlistRepository): IWishlistRepository

    @Binds
    @Singleton
    abstract fun bindStorageRepository(storageRepository: StorageRepository): IStorageRepository
}