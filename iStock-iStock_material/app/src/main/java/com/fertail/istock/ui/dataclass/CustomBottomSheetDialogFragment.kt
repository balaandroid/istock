package com.fertail.istock.ui.dataclass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fertail.istock.Constant
import com.fertail.istock.databinding.LayoutPersistentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomBottomSheetDialogFragment (val assets: String,val actionListener: actionSelected) : BottomSheetDialogFragment(), Constant{

    private var _binding: LayoutPersistentBottomSheetBinding? = null
    private val binding get() = _binding!!
    companion object {

        const val TAG = "CustomBottomSheetDialogFragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LayoutPersistentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.tvTitle.setOnClickListener {
            actionListener.optionChosen(CAMERATEXT, assets)
            dismiss()
        }

        binding.tvSubtitle.setOnClickListener {
            actionListener.optionChosen(GALLERYTEXT, assets)
            dismiss()
        }

        binding.tvTakeVideo.setOnClickListener {
            actionListener.optionChosen(CAPTUREVIDEO, assets)
            dismiss()
        }
    }


}

interface actionSelected {
    fun optionChosen(action : String , from : String)
}