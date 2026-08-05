package com.example.core.data.remote.source

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FireStoreRemoteDataSource<T>(
    private val firebaseFireStore : FirebaseFirestore,
    private val collectionName: String,
    private val idOf: (T) -> String,
    private val toMap: (T) -> Map<String, Any?>,
    private val fromSnapshot: (DocumentSnapshot) -> T?
) {
    private val collection get() = firebaseFireStore.collection(collectionName)

    suspend fun push(entity: T){
        collection.document(idOf(entity)).set(toMap(entity)).await()
    }

    suspend fun pullChangedSince(timeStamp: Long): List<T>{
        return collection.whereGreaterThan("updatedAt", timeStamp).get().await()
            .documents.mapNotNull { fromSnapshot(it) }
    }
}