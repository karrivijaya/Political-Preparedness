package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.repository.CivicRepository
import kotlinx.coroutines.launch
import java.lang.Exception

//TODO: Construct ViewModel and provide election datasource

enum class ApiStatus{
    LOADING,
    DONE,
    ERROR
}
enum class RetrieveDBStatus{
    LOADING,
    DONE,
    ERROR
}
class ElectionsViewModel(app:Application): AndroidViewModel(app) {

    private val database = ElectionDatabase.getInstance(app)
    private val repository = CivicRepository(database)

    // Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections:LiveData<List<Election>>
            get() = _upcomingElections

    var savedElections : LiveData<List<Election>?> = MutableLiveData()

    private val _apiStatus = MutableLiveData<ApiStatus>()
    val apiStatus: LiveData<ApiStatus>
        get() = _apiStatus

    private val _retrieveDBStatus = MutableLiveData<RetrieveDBStatus>()
    val retrieveDBStatus: LiveData<RetrieveDBStatus>
        get() = _retrieveDBStatus


    private val _navigateToSelectedElection = MutableLiveData<Election?>()

    val navigateToSelectedElection: LiveData<Election?>
    get() = _navigateToSelectedElection

    //TODO: Create live data val for saved elections

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database
    init{
        getUpcomingElections()
        getSavedElections()
    }

    private fun getUpcomingElections() {
        viewModelScope.launch {
            _apiStatus.value = ApiStatus.LOADING
            try {
                _upcomingElections.value = repository.refreshElectionData()
                _apiStatus.value = ApiStatus.DONE
            } catch (e: Exception) {
                _apiStatus.value = ApiStatus.ERROR
            }
        }
    }

    private fun getSavedElections(){
        viewModelScope.launch {
            _retrieveDBStatus.value = RetrieveDBStatus.LOADING
            try{
                savedElections = repository.getAllElections
                _retrieveDBStatus.value = RetrieveDBStatus.DONE
            }catch (e:Exception){
                _retrieveDBStatus.value = RetrieveDBStatus.ERROR
            }
        }
    }

    fun displayElectionDetails(election:Election){
        _navigateToSelectedElection.value = election

    }

    fun displayElectionDetailsComplete(){
        _navigateToSelectedElection.value = null
    }
    //TODO: Create functions to navigate to saved or upcoming election voter info

}