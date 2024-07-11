package com.fertail.istock.ui.bottomsheet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.Constant
import com.fertail.istock.ItemSelectedForNoune
import com.fertail.istock.databinding.BottomSheetFunctionalBinding
import com.fertail.istock.ui.verification.adapter.LocationHierarchyAdapter
import com.fertail.istock.ui.verification.adapter.MainLoadStateAdapter
import com.fertail.istock.view_model.LocationHierarchyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LocationHierarchyBottomsheet(
    var param: ItemSelectedForNoune
) : BottomSheetDialogFragment(), Constant {


    private var _binding: BottomSheetFunctionalBinding? = null
    private val binding get() = _binding!!

    private val getNounViewModel: LocationHierarchyViewModel by viewModels()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetFunctionalBinding.inflate(inflater, container, false)

        binding.progressbar.visibility = View.VISIBLE

        binding.idTitle.text = "Location Hierarchy"
        binding.idSearch.visibility = View.VISIBLE

        initRecyclerView()



        return binding.root
    }


    private fun initRecyclerView() {

        binding.helpRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.progressbar.visibility = View.GONE
        val adapter = LocationHierarchyAdapter(param)
        binding.helpRecyclerView.adapter = adapter.withLoadStateFooter(
            MainLoadStateAdapter()
        )

        binding.idSearch.addTextChangedListener(object :TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                getNounViewModel.setQueryText(p0.toString())
                adapter.refresh()
            }

        })


        lifecycleScope.launch {
            getNounViewModel.data.collectLatest {
                adapter.submitData(it)
            }
        }

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

