package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment: Fragment() {

    //Declare ViewModel
    private val electionsViewModel: ElectionsViewModel by lazy{
        val activity = requireNotNull(this.activity){
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, ElectionsViewModelFactory(activity.application)).
                get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.electionsViewModel = electionsViewModel

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters
        val upcomingElectionAdapter = ElectionListAdapter(ElectionListener { election ->
            electionsViewModel.displayElectionDetails(election)
        })
        //TODO: Populate recycler adapters
        binding.electionRecyclerView.adapter = upcomingElectionAdapter

        val savedElectionAdapter = ElectionListAdapter(ElectionListener{ election ->
            electionsViewModel.displayElectionDetails(election)
        })
        binding.savedElectionRecylerView.adapter = savedElectionAdapter

        electionsViewModel.upcomingElections.observe(viewLifecycleOwner, Observer<List<Election>>{ elections ->
            Log.i("ElectionsFragment"," inside upcoming Elections")
            elections?.let{
                upcomingElectionAdapter.submitList(elections)
            }
        })


        electionsViewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            Log.i("ElectionsFragment","inside saved elections")
            elections?.let{
                savedElectionAdapter.submitList(elections)
            }
        })

        electionsViewModel.navigateToSelectedElection.observe(viewLifecycleOwner, Observer {election ->
            if(null != election) {
                findNavController().navigate(
                        ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.id, election.division))
                electionsViewModel.displayElectionDetailsComplete()
            }
        })

        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}