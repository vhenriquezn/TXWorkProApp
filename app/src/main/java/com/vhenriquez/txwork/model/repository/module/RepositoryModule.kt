package com.vhenriquez.txwork.model.repository.module

import com.vhenriquez.txwork.model.repository.AuthRepository
import com.vhenriquez.txwork.model.repository.impl.AuthRepositoryImpl
import com.vhenriquez.txwork.model.repository.FirestoreRepository
import com.vhenriquez.txwork.model.repository.StorageRepository
import com.vhenriquez.txwork.model.repository.UserPreferencesRepository
import com.vhenriquez.txwork.model.repository.impl.FirestoreRepositoryImpl
import com.vhenriquez.txwork.model.repository.WorkerRepository
import com.vhenriquez.txwork.model.repository.impl.StorageRepositoryImpl
import com.vhenriquez.txwork.model.repository.impl.UserPreferencesRepositoryImpl
import com.vhenriquez.txwork.model.repository.impl.WorkerRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule{
    @Binds abstract fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds abstract fun provideFirestoreRepository(impl: FirestoreRepositoryImpl): FirestoreRepository

    @Binds abstract fun provideWorkerRepository(impl: WorkerRepositoryImpl): WorkerRepository

    @Binds abstract fun providesStorageRepository(impl: StorageRepositoryImpl): StorageRepository

    @Binds abstract fun providesUserPreferencesRepository(impl: UserPreferencesRepositoryImpl): UserPreferencesRepository



}
