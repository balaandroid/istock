package com.fertail.istock.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.fertail.istock.ApiListActivity
import com.fertail.istock.DashboardActivity
import com.fertail.istock.R
import com.fertail.istock.api.iStockService
import com.fertail.istock.databinding.FragmentProfileBinding
import com.fertail.istock.iStockApplication
import com.fertail.istock.model.UserDetailsResponse
import com.fertail.istock.ui.BaseFragment
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.GetNounViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    private var clickCount = 0

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var userDetailsResponse : UserDetailsResponse

    private val genDictionary : GetNounViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        val gson = Gson()
        val myType = object : TypeToken<UserDetailsResponse>() {}.type
        userDetailsResponse = gson.fromJson<UserDetailsResponse>(iStockApplication.appPreference.KEY_USER_DETAILS, myType)

        binding.appVersion?.text = CommonUtils.getAppVersion()
        binding.title.text = userDetailsResponse.firstName + " " +userDetailsResponse.lastName
        binding.titleEmailIdResponse.setText(userDetailsResponse.emailId)
        binding.titlePhoneIdResponse.setText(userDetailsResponse.mobile)
        binding.titleUesrIdResponse.setText(userDetailsResponse.userName)

        binding.idUrl.text = iStockService().getBaseUrl()

        clickable()
        initToolbor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onResume() {
        super.onResume()
        if(CommonUtils.isConnectedToInternet(requireContext())){
            binding.toolbarProfile.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        }else{
            binding.toolbarProfile.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            binding.toolbarProfile.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
            Toast.makeText(requireContext(),"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            binding.toolbarProfile.toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.internet_alert_color))
            Toast.makeText(requireContext(),"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickable(){
        binding.idUrlContainer.setOnClickListener {
            clickCount++
            if (clickCount == 7) {
                showSecurityCodeDialog()
                clickCount = 0 // Reset the counter after showing the dialog
            }
        }

    }


    private fun showSecurityCodeDialog(){
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_security_code, null)
        val editTextCode1 = dialogView.findViewById<EditText>(R.id.editTextCode1)
        val editTextCode2 = dialogView.findViewById<EditText>(R.id.editTextCode2)
        val editTextCode3 = dialogView.findViewById<EditText>(R.id.editTextCode3)
        val editTextCode4 = dialogView.findViewById<EditText>(R.id.editTextCode4)

        val editTexts = listOf(editTextCode1, editTextCode2, editTextCode3, editTextCode4)
        for (i in 0 until editTexts.size - 1) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter Security Code")
            .setView(dialogView)
            .setPositiveButton("Submit") { dialog, _ ->
                val inputCode = editTexts.joinToString("") { it.text.toString() }
                if (inputCode == "1234") { // Replace with your actual security code
                    Toast.makeText(context, "Access Granted", Toast.LENGTH_SHORT).show()
                    val intent=Intent(context, ApiListActivity::class.java)
                    context!!.startActivity(intent)
                    // Handle the access granted logic here
                } else {
                    Toast.makeText(context, "Invalid Code", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setCancelable(false) // Prevent dialog from closing on outside touch
            .create()

        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Handle back press to clear text or other actions
                editTexts.forEach { it.text.clear() }
                dialog.dismiss()
                true
            } else {
                false
            }
        }

        dialog.show()
    }


    private fun initToolbor() {

        binding.toolbarProfile.btnMenu.visibility = View.GONE
        binding.toolbarProfile.btnBack.visibility = View.VISIBLE

        binding.toolbarProfile.toolbarTitle.text = resources.getText(R.string.menu_Profile)

        binding.toolbarProfile.btnBack.setOnClickListener {
            (activity as DashboardActivity?)?.backClicked()
        }

        binding.toolbarProfile.btnLogout.setOnClickListener {
            CommonUtils.showLogotPopup(requireContext())
        }

        binding.idDictionary.setOnClickListener {
            genDictionary.getDictionary()
        }
    }

}