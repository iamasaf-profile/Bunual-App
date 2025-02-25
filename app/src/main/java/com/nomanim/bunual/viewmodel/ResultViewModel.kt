package com.nomanim.bunual.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.nomanim.bunual.Constants
import com.nomanim.bunual.base.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : BaseViewModel(application) {

    fun mostViewedLiveData(): LiveData<QuerySnapshot> = mostViewedMutableLiveData
    private val mostViewedMutableLiveData = MutableLiveData<QuerySnapshot>()

    fun moreMostViewedLiveData(): LiveData<QuerySnapshot> = moreMostViewedMutableLiveData
    private val moreMostViewedMutableLiveData = MutableLiveData<QuerySnapshot>()

    fun getMostViewedAds(firestore: FirebaseFirestore, numberOfAds: Long) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .orderBy("numberOfViews", Query.Direction.DESCENDING)
                .limit(numberOfAds).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            mostViewedMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }

    fun getMoreMostViewedAds(firestore: FirebaseFirestore, lastValue: QuerySnapshot, numberOfAds: Long) {
        CoroutineScope(Dispatchers.IO).launch(handler) {
            firestore.collection(Constants.ADS_COLLECTION_NAME)
                .orderBy("numberOfViews", Query.Direction.DESCENDING)
                .startAfter(lastValue.documents[lastValue.size() - 1])
                .limit(numberOfAds).get()
                .addOnCompleteListener { response ->
                    if (response.isSuccessful) {
                        val documents = response.result
                        if (documents != null) {
                            moreMostViewedMutableLiveData.postValue(documents!!)
                        }
                    } else {
                        errorMutableLiveData.postValue(response.exception.toString())
                    }
                }
        }
    }
}