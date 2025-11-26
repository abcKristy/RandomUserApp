package com.example.horseinacoat.di

import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.GetUsersWithFiltersUseCase
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetRandomUserUseCase(
        repository: UserRepository
    ): GetRandomUserUseCase {
        return GetRandomUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllUsersUseCase(
        repository: UserRepository
    ): GetAllUsersUseCase {
        return GetAllUsersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveUserUseCase(
        repository: UserRepository
    ): SaveUserUseCase {
        return SaveUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideIsUserSavedUseCase(
        repository: UserRepository
    ): IsUserSavedUseCase {
        return IsUserSavedUseCase(repository)
    }
    @Provides
    @Singleton
    fun provideGetUsersWithFiltersUseCase(
        repository: UserRepository
    ): GetUsersWithFiltersUseCase {
        return GetUsersWithFiltersUseCase(repository)
    }
}