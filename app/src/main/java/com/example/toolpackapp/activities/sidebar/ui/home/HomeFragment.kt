package com.example.toolpackapp.activities.sidebar.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toolpackapp.activities.PackageAdapter
import com.example.toolpackapp.activities.packages
import com.example.toolpackapp.databinding.FragmentHomeBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private lateinit var packageadapter: PackageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        managerHomeView()
    }

    private fun managerHomeView() {
        val query = FirebaseFirestore.getInstance().collection("packages")
            .orderBy("deliveryDate", Query.Direction.DESCENDING)

        val options: FirestoreRecyclerOptions<packages> =
            FirestoreRecyclerOptions.Builder<packages>().setQuery(query, packages::class.java)
                .build()

        binding?.managerHomeView?.setHasFixedSize(true)
        binding?.managerHomeView?.layoutManager = LinearLayoutManager(requireContext())
        binding?.managerHomeView?.adapter = packageadapter
//        adapter = PackageAdapter(options).also { binding?.managerHomeView?.adapter = it }
    }

    override fun onStart() {
        super.onStart()
        packageadapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        packageadapter.stopListening()
    }

}