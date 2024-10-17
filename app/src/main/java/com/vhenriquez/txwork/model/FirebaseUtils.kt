package com.vhenriquez.txwork.model

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun <T> Task<T>.await():T{
    return suspendCancellableCoroutine { cont->
        addOnCompleteListener {
            if(it.exception != null){
                cont.resumeWith(Result.failure(it.exception!!))
            }else{
                cont.resume(it.result, null)
            }
        }
    }
}