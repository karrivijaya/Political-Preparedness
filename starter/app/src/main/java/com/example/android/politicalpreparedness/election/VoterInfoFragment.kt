package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.android.synthetic.main.fragment_voter_info.view.*

class VoterInfoFragment : Fragment() {

    private var electionId: Int? = null
    private var division: Division? = null

    //Add ViewModel values and create ViewModel

    private val voterInfoViewModel: VoterInfoViewModel by lazy {
        val activity = requireNotNull(this.activity){

        }
        val database = ElectionDatabase.getInstance(activity.application)

        ViewModelProvider(this, VoterInfoViewModelFactory(database, electionId, division)).get(VoterInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        val binding = FragmentVoterInfoBinding.inflate(inflater)

        // Add binding values
        binding.lifecycleOwner = this

        electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId

        division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision

        binding.voterInfoViewModel = voterInfoViewModel

        // Populate voter info -- hide views without provided data.

        voterInfoViewModel.electionInfoUrl.observe(viewLifecycleOwner, Observer { electionInfoUrl ->
            if(electionInfoUrl != null){
                binding.stateHeader.visibility = View.VISIBLE
            }
            else{
                binding.stateHeader.visibility = View.GONE
            }

        })
        voterInfoViewModel.ballotInfoUrl.observe(viewLifecycleOwner, Observer { ballotInfoUrl ->
            if(ballotInfoUrl != null){
                binding.stateBallot.visibility = View.VISIBLE
            }
            else{
                binding.stateBallot.visibility = View.GONE
            }
        })

        voterInfoViewModel.votingLocationsUrl.observe(viewLifecycleOwner, Observer { votingLocationsUrl ->
            if(votingLocationsUrl != null){
                binding.stateLocations.visibility = View.VISIBLE
            }
            else{
                binding.stateLocations.visibility = View.GONE
            }
        })

        voterInfoViewModel.voterInfoAddress.observe(viewLifecycleOwner, Observer { voterInfoAddress ->
            if(voterInfoAddress != null){
                binding.stateCorrespondenceHeader.visibility = View.VISIBLE
                binding.addressGroup.visibility = View.VISIBLE
            }
            else{
                binding.stateCorrespondenceHeader.visibility = View.GONE
                binding.addressGroup.visibility = View.GONE
            }

        })

        voterInfoViewModel.isElectionInDB.observe(viewLifecycleOwner, Observer { isElectionInDB ->
            when (isElectionInDB) {
                true -> {
                    binding.btnFollowOrUnfollowElection.apply {
                        setText(getString(R.string.unfollow_election))
                        setOnClickListener {
                            voterInfoViewModel.deleteElectionFromDatabase()
                        }
                    }
                }

                false -> {
                    binding.btnFollowOrUnfollowElection.apply {
                        setText(getString(R.string.follow_election))
                        setOnClickListener {
                            voterInfoViewModel.saveElectionToDatabase()
                        }
                    }

                }
            }
        })

        //Handle loading of URLs
        binding.stateHeader.setOnClickListener {
            val url = voterInfoViewModel.electionInfoUrl.value
            if(url != null){
                loadUrl(url)
            }
        }

        binding.stateBallot.setOnClickListener {
            val url = voterInfoViewModel.ballotInfoUrl.value
            if(url != null) {
                loadUrl(url)
            }
        }

        binding.stateLocations.setOnClickListener {
            val url = voterInfoViewModel.votingLocationsUrl.value
            if(url != null){
                loadUrl(url)
            }
        }

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root
    }

    //TODO: Create method to load URL intents

    private fun loadUrl(url: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

}