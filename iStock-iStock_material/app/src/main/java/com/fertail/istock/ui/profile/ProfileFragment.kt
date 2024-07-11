package com.fertail.istock.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
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

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    lateinit var userDetailsResponse : UserDetailsResponse

    private val genDictionary : GetNounViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)


        val gson = Gson()
        val myType = object : TypeToken<UserDetailsResponse>() {}.type
        userDetailsResponse = gson.fromJson<UserDetailsResponse>(iStockApplication.appPreference.KEY_USER_DETAILS, myType)

        binding.appVersion?.text = CommonUtils.getAppVersion()
        binding.title.setText(userDetailsResponse.firstName + " " +userDetailsResponse.lastName)
        binding.titleEmailIdResponse.setText(userDetailsResponse.emailId)
        binding.titlePhoneIdResponse.setText(userDetailsResponse.mobile)
        binding.titleUesrIdResponse.setText(userDetailsResponse.userName)

        binding.idUrl.setText(iStockService().getBaseUrl())

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