package com.example.toolpackapp.activities.ui.addBuildingSite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.databinding.FragmentAddBuildingSiteBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AddBuildingSiteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBuildingSiteFragment : Fragment() {

    private var binding: FragmentAddBuildingSiteBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddBuildingSiteBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

}