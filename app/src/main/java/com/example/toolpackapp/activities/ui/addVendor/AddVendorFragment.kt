package com.example.toolpackapp.activities.ui.addVendor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.databinding.FragmentAddVendorBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AddVendorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddVendorFragment : Fragment() {
    private var binding : FragmentAddVendorBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddVendorBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

}