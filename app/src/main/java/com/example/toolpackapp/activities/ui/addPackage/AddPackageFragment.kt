package com.example.toolpackapp.activities.ui.addPackage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toolpackapp.databinding.FragmentAddPackageBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AddPackageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddPackageFragment : Fragment() {

    private var binding: FragmentAddPackageBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentAddPackageBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }

}