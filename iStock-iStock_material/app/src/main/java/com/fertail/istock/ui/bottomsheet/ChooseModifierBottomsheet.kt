package com.fertail.istock.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.Constant
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.VerificationDetailsActivity
import com.fertail.istock.databinding.BottomSheetFunctionalBinding
import com.fertail.istock.model.*
import com.fertail.istock.ui.verification.FuntionalLocationFragment
import com.fertail.istock.ui.verification.adapter.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_functional_location.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChooseModifierBottomsheet(
    internal var type: String,
    var data: ArrayList<AttributesItem>,
    var param: ItemSelectedForNoune
) : BottomSheetDialogFragment(), Constant {

    lateinit var buisnessAdapter: GetModifierAdapter

    private var _binding: BottomSheetFunctionalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetFunctionalBinding.inflate(inflater, container, false)

        binding.progressbar.visibility = View.VISIBLE

        binding.idTitle.text = type

        CoroutineScope(Dispatchers.Main ).launch{

            delay(2000)
            initRecyclerView()
        }

        return binding.root
    }


    private fun initRecyclerView() {

        binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        buisnessAdapter = GetModifierAdapter(data, param)
        binding.helpRecyclerView.adapter = buisnessAdapter

        binding.progressbar.visibility = View.GONE
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

