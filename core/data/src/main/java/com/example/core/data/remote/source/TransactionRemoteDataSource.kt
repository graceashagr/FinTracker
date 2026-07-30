package com.example.core.data.remote.source

import com.example.core.data.local.entity.TransactionEntity
import com.example.core.data.local.entity.toFirestoreMap
import com.example.core.data.local.entity.toTransactionEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection get() = firestore.collection("transactions")

    suspend fun push(entity: TransactionEntity) {
        collection.document(entity.id).set(entity.toFirestoreMap()).await()
    }

    suspend fun pullChangedSince(timestamp: Long): List<TransactionEntity> {
        return collection.whereGreaterThan("updatedAt", timestamp)
            .get().await()
            .documents.mapNotNull { it.toTransactionEntity() }
    }

}

