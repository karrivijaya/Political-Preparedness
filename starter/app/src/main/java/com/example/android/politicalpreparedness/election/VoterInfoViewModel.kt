package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class VoterInfoViewModel(database: ElectionDatabase, electionId: Int?, division: Division?) : ViewModel() {

    val repository = CivicRepository(database)
    //Add live data to hold voter info

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _ballotInfoUrl = MutableLiveData<String>()
    val ballotInfoUrl: LiveData<String>
        get() = _ballotInfoUrl

    private val _votingLocationsUrl= MutableLiveData<String>()
    val votingLocationsUrl: LiveData<String>
        get() = _votingLocationsUrl

    private val _electionInfoUrl = MutableLiveData<String>()
    val electionInfoUrl: LiveData<String>
        get() = _electionInfoUrl

    private val _voterInfoAddress = MutableLiveData<String>()
    val voterInfoAddress: LiveData<String>
        get() = _voterInfoAddress

    private val _isElectionInDB = MutableLiveData<Boolean>()
    val isElectionInDB: LiveData<Boolean>
        get() = _isElectionInDB

    var address = StringBuilder()

    // Add var and methods to populate voter info
    init{
        displayVoterInfo(electionId, division)
        viewModelScope.launch {
            try {
                val election = repository.getSelectedElection(electionId)
                _isElectionInDB.value = election != null
            }catch (e:Exception){
                e.message?.let { Log.e("VoterInfoViewModel", it) }
            }
        }
    }


    private fun displayVoterInfo(electionId:Int?, division: Division?){
        if(division != null){
            address.append(division.country).append("\b").append(division.state)
        }
        viewModelScope.launch{

            try {
                val voterInfoResponse = repository.getVoterInfo(address.toString(), electionId)

                _election.value = voterInfoResponse?.election

                voterInfoResponse?.state?.let { states ->
                    if (states.isNotEmpty()) {
                        val electionAdministrationBody = states[0].electionAdministrationBody
                        _ballotInfoUrl.value = electionAdministrationBody.ballotInfoUrl
                        _votingLocationsUrl.value = electionAdministrationBody.votingLocationFinderUrl
                        _electionInfoUrl.value = electionAdministrationBody.electionInfoUrl
                        _voterInfoAddress.value = electionAdministrationBody.correspondenceAddress?.toFormattedString()
                    }
                }
            }catch (e:Exception){
                e.message?.let { Log.e("VoterInfoViewModel", it) }
            }
        }
    }

    fun saveElectionToDatabase(){
        Log.i("VoterInfoViewModel","save election clicked")

        viewModelScope.launch {
            try {
                _election.value?.let {
                    Log.i("VoterInfoViewModel","inside save election")
                    repository.saveElectionToDatabase(it)
                    _isElectionInDB.value = true
                }
            }catch (e:Exception){
                e.message?.let { Log.e("VoterInfoViewModel",it) }
            }
        }
    }

    fun deleteElectionFromDatabase(){
        Log.i("VoterInfoViewModel","delete election clicked")
        viewModelScope.launch {
            try{
                _election.value?.let{
                    Log.i("VoterInfoViewModel","inside delete election")
                    repository.deleteElectionFromDatabase(it)
                    _isElectionInDB.value = false
                }
            }catch (e:Exception){
                e.message?.let { Log.e("VoterInfoViewModel", it) }
            }
        }
    }


    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}