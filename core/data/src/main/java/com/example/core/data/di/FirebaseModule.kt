package com.example.core.data.di

import com.example.core.data.local.entity.AccountEntity
import com.example.core.data.local.entity.BudgetEntity
import com.example.core.data.local.entity.TransactionEntity
import com.example.core.data.local.entity.toAccountEntity
import com.example.core.data.local.entity.toBudgetEntity
import com.example.core.data.local.entity.toFirestoreMap
import com.example.core.data.local.entity.toTransactionEntity
import com.example.core.data.remote.source.FireStoreRemoteDataSource
import com.example.core.domain.model.Budget
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideTransactionRemoteDataSource(firebaseFirestore: FirebaseFirestore): FireStoreRemoteDataSource<TransactionEntity> =
        FireStoreRemoteDataSource(
            firebaseFireStore = firebaseFirestore,
            collectionName = "transactions",
            idOf = { it.id },
            toMap = { it.toFirestoreMap() },
            fromSnapshot = { it.toTransactionEntity() },
        )

    @Provides
    fun provideAccountRemoteDataSource(firebaseFirestore: FirebaseFirestore): FireStoreRemoteDataSource<AccountEntity> =
        FireStoreRemoteDataSource(
            firebaseFireStore = firebaseFirestore,
            collectionName = "accounts",
            idOf = { it.id },
            toMap = { it.toFirestoreMap() },
            fromSnapshot = { it.toAccountEntity() },
        )


    @Provides
    fun provideBudgetRemoteDataSource(firebaseFirestore: FirebaseFirestore): FireStoreRemoteDataSource<BudgetEntity> =
        FireStoreRemoteDataSource(
            firebaseFireStore = firebaseFirestore,
            collectionName = "budgets",
            idOf = { it.id },
            toMap = { it.toFirestoreMap() },
            fromSnapshot = { it.toBudgetEntity() }
        )
}