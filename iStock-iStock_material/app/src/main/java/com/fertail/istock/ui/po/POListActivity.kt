package com.fertail.istock.ui.po

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fertail.istock.BaseActivity
import com.fertail.istock.database.table.PoMainAndSubTable
import com.fertail.istock.database.table.PoSubTable
import com.fertail.istock.databinding.ActivityPoListBinding
import com.fertail.istock.databinding.PoListItemBinding
import com.fertail.istock.util.GenericRecyclerAdapter
import com.fertail.istock.view_model.PoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class POListActivity : BaseActivity() {

    lateinit var binding: ActivityPoListBinding
    var mAdapter = GenericRecyclerAdapter<PoMainAndSubTable>()
    private val poViewModel: PoViewModel by viewModels()


    var list = ArrayList<PoMainAndSubTable>()

    companion object {
        fun start(caller: Context) {
            val intent = Intent(caller, POListActivity::class.java)
            caller.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            poViewModel.getAllPO().collectLatest {
                list.clear()
                list.addAll(it)
                loadRecyclerViwe()
            }
        }

        clickLitsnr()
    }

    private fun clickLitsnr() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.idCreateNew.setOnClickListener {
            AddPoListSheetDialogFragment.open(supportFragmentManager)
        }
    }

    private fun loadRecyclerViwe() {

        binding.idNoData.isVisible = list.isEmpty()
        binding.rcyPoList.isVisible = list.isNotEmpty()

        mAdapter.listOfItems = list

        mAdapter.expressionViewHolderBinding = { eachItem, viewBinding ->
            val view = viewBinding as PoListItemBinding

            view.oldTagText.text = eachItem.poMain.po_id
            view.idTotal.text = eachItem.poSubList.size.toString()

            view.innerCard.setOnClickListener {
                POSubListActivity.start(this , eachItem)
            }
            view.arrowButton.setOnClickListener {
                POSubListActivity.start(this , eachItem)
            }

            view.idAddText.setOnClickListener {

                val tempdata = PoSubTable(getRandomNumberString())
                tempdata.po_id = eachItem.poMain.po_id
                poViewModel.addSubPoToTable(tempdata)

//                AddPoListSheetDialogFragment.open(supportFragmentManager, eachItem)
            }

            view.idAddItem.setOnClickListener {

                val tempdata = PoSubTable(getRandomNumberString())
                tempdata.po_id = eachItem.poMain.po_id
                poViewModel.addSubPoToTable(tempdata)
            }
        }


        mAdapter.expressionOnCreateViewHolder = { viewGroup ->
            PoListItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        }

        binding.rcyPoList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }

    fun getRandomNumberString(): String {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        val rnd = Random()
        val number: Int = rnd.nextInt(999999)

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number)
    }

}