package com.fertail.istock.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.fertail.istock.databinding.FragmentNewBinding
import kotlinx.android.synthetic.main.fragment_new.*

class NewFragment : Fragment() {

    private var _binding: FragmentNewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding.apply {
            first_drop_down.setOnClickListener {
                if(first_drop_down_content.isVisible==true) {
                    first_drop_down_content.visibility = View.GONE
                } else {
                    first_drop_down_content.visibility = View.VISIBLE
                    second_drop_down_content.visibility = View.GONE
                    third_drop_down_content.visibility = View.GONE
                    fourth_drop_down_content.visibility = View.GONE
                    fifth_drop_down_content.visibility = View.GONE
                }
            }
            second_drop_down.setOnClickListener {
                if(second_drop_down_content.isVisible==true) {
                    second_drop_down_content.visibility = View.GONE
                } else {
                    second_drop_down_content.visibility = View.VISIBLE
                    first_drop_down_content.visibility = View.GONE
                    third_drop_down_content.visibility = View.GONE
                    fourth_drop_down_content.visibility = View.GONE
                    fifth_drop_down_content.visibility = View.GONE
                }
            }
            third_drop_down.setOnClickListener {
                if(third_drop_down_content.isVisible==true) {
                    third_drop_down_content.visibility = View.GONE
                } else {
                    third_drop_down_content.visibility = View.VISIBLE
                    second_drop_down_content.visibility = View.GONE
                    first_drop_down_content.visibility = View.GONE
                    fourth_drop_down_content.visibility = View.GONE
                    fifth_drop_down_content.visibility = View.GONE
                }
            }
            fourth_drop_down.setOnClickListener {
                if(fourth_drop_down_content.isVisible==true) {
                    fourth_drop_down_content.visibility = View.GONE
                } else {
                    fourth_drop_down_content.visibility = View.VISIBLE
                    third_drop_down_content.visibility = View.GONE
                    second_drop_down_content.visibility = View.GONE
                    first_drop_down_content.visibility = View.GONE
                    fifth_drop_down_content.visibility = View.GONE
                }
            }
            fifth_drop_down.setOnClickListener {
                if(fifth_drop_down_content.isVisible==true) {
                    fifth_drop_down_content.visibility = View.GONE
                } else {
                    fifth_drop_down_content.visibility = View.VISIBLE
                    fourth_drop_down_content.visibility = View.GONE
                    third_drop_down_content.visibility = View.GONE
                    second_drop_down_content.visibility = View.GONE
                    first_drop_down_content.visibility = View.GONE
                }
            }
        }
    }
}