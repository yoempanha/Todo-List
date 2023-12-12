package com.example.todolist.di

import com.example.todolist.data.remote.service.FirebaseService
import com.example.todolist.data.remote.source.SourceRemoteImpl
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun providesFireStoreDatabase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    fun provideFirebaseService(firestore: FirebaseFirestore): FirebaseService {
        return SourceRemoteImpl(firestore)
    }
}