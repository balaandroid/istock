package com.fertail.istock.ui.po

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.fertail.istock.Constant
import com.fertail.istock.R
import com.fertail.istock.database.table.PoMainAndSubTable
import com.fertail.istock.database.table.PoMainTable
import com.fertail.istock.database.table.PoSubTable
import com.fertail.istock.databinding.AddPoListBottomSheetBinding
import com.fertail.istock.databinding.FragmentEpuipmentInfoBinding
import com.fertail.istock.databinding.LayoutPersistentBottomSheetBinding
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.GetNounViewModel
import com.fertail.istock.view_model.PoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddPoListSheetDialogFragment : BottomSheetDialogFragment(), Constant{

    private var _binding: AddPoListBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val poViewModel : PoViewModel by viewModels()


    companion object {

        var action = "main"
        lateinit var data : PoMainAndSubTable

        fun open(supportFragmentManager: FragmentManager) {
            action = "main"
            AddPoListSheetDialogFragment().show(
                supportFragmentManager,
                "bottomSheetFrag"
            )
        }

        fun open(supportFragmentManager: FragmentManager, s: PoMainAndSubTable) {
            action = "sub"
            data = s
            AddPoListSheetDialogFragment().show(
                supportFragmentManager,
                "bottomSheetFrag"
            )
        }
        const val TAG = "AddPoListSheetDialogFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddPoListBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initOncreate()
    }

    private fun initOncreate() {
        binding.idCancel.setOnClickListener {
            dismiss()
        }


        binding.idSave.setOnClickListener {
            lifecycleScope.launch{

                if (action.equals("sub")){
                    val data = PoSubTable(binding.idPoNumber.text.toString());
                    data.po_id = Companion.data.poMain.po_id
                    poViewModel.addSubPoToTable(data)
                    dismiss()
                    CommonUtils.showAlert(requireContext(),"Po Saved Successfully!!")
                }else{
                    poViewModel.getAllPOById(binding.idPoNumber.text.toString()).let {

                        if(it.isEmpty()){
                            val data = PoMainTable(binding.idPoNumber.text.toString(), "test")
                            poViewModel.addPoToTable(data)
                            dismiss()
                            CommonUtils.showAlert(requireContext(),"Po Saved Successfully!!")

                        }else{
                            CommonUtils.showAlert(requireContext(),"Po Number Already Exist!!")
                        }

                    }
                }


            }
        }
    }
}