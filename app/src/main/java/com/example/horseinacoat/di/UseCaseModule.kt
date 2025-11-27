package com.example.horseinacoat.di

import com.example.horseinacoat.domain.repository.UserRepository
import com.example.horseinacoat.domain.usecase.DeleteUserUseCase
import com.example.horseinacoat.domain.usecase.GetAllUsersUseCase
import com.example.horseinacoat.domain.usecase.GetRandomUserUseCase
import com.example.horseinacoat.domain.usecase.GetUserByIdUseCase
import com.example.horseinacoat.domain.usecase.GetUsersPaginatedUseCase
import com.example.horseinacoat.domain.usecase.GetUsersWithFiltersUseCase
import com.example.horseinacoat.domain.usecase.IsUserSavedUseCase
import com.example.horseinacoat.domain.usecase.SaveUserUseCase
import com.example.horseinacoat.presentation.viewModel.custom.FindFriendViewModel
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
    @Provides
    @Singleton
    fun provideDeleteUserUseCase(
        repository: UserRepository
    ): DeleteUserUseCase {
        return DeleteUserUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetUserByIdUseCase(
        repository: UserRepository
    ): GetUserByIdUseCase {
        return GetUserByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFindFriendViewModel(
        getAllUsersUseCase: GetAllUsersUseCase,
        getRandomUserUseCase: GetRandomUserUseCase,
        saveUserUseCase: SaveUserUseCase
    ): FindFriendViewModel {
        return FindFriendViewModel(
            getAllUsersUseCase,
            getRandomUserUseCase,
            saveUserUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetUsersPaginatedUseCase(
        repository: UserRepository
    ): GetUsersPaginatedUseCase {
        return GetUsersPaginatedUseCase(repository)
    }
}