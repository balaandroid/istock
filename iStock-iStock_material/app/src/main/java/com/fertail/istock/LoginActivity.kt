package com.fertail.istock

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fertail.istock.databinding.ActivityLoginBinding
import com.fertail.istock.model.LoginRequest
import com.fertail.istock.util.CommonUtils
import com.fertail.istock.view_model.LoginViewModel
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    lateinit var viewModel: LoginViewModel

    companion object {
        fun start(caller: Context) {
            val intent = Intent(caller, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            caller.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        EventBus.getDefault().register(this)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.appVersion?.text = CommonUtils.getAppVersion()
        observeViewModel()
        initViewClick()

        if (iStockApplication.appPreference.KEY_IS_LOGGED_IN) {
            DashboardActivity.start(this)
        } else {
            iStockApplication.appPreference.clearAppPreference()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNetworkChangeEvent(event: String) {
        if (event=="true") {
            Toast.makeText(this,"You are online!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"You are offline!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViewClick() {
        binding.btnLogin.setOnClickListener {

            val username = binding.idUserName.text.toString().trim()
            val password = binding.idPassword.text.toString().trim()
            val request = LoginRequest(username, password, "password")
            if (validateUserInput()) {
                viewModel.login(request)
            } else {
                Toast.makeText(this, "Enter correct user name or password", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun validateUserInput(): Boolean {
        val username = binding.idUserName.text.toString().trim()
        val password = binding.idPassword.text.toString().trim()

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false
        }
        return true
    }

    private fun observeViewModel() {
        viewModel.loginResponse.observe(this, Observer { countries ->
            countries?.let {

                if (it.accessToken == null) {
                    it.error?.let { it1 -> CommonUtils.showAlert(this, it1) }
                } else {

                    val username = binding.idUserName.text.toString().trim()
                    val password = binding.idPassword.text.toString().trim()
                    iStockApplication.appPreference.KEY_USER_NAME = username
                    iStockApplication.appPreference.KEY_USER_PASSWORD = password
                    iStockApplication.appPreference.KEY_IS_LOGGED_IN = true
                    iStockApplication.appPreference.KEY_ACCESS_TOKEN = it.accessToken

                    viewModel.getUserInfo(username)
//                DashboardActivity.start(this)

                }
            }
        })

        viewModel.userDetailsResponse.observe(this) {

            it.let {

                val gson = Gson()
                val json = gson.toJson(it)

                iStockApplication.appPreference.KEY_USER_DETAILS = json
                iStockApplication.appPreference.KEY_USER_ID = it.userid!!

                DashboardActivity.start(this)
            }

        }

        viewModel.loginError.observe(this, Observer { isError ->
            Toast.makeText(this, isError, Toast.LENGTH_SHORT).show()
        })
        viewModel.loading.observe(this, Observer { isLoading ->
            isLoading?.let {
                binding.progressbar.visibility = if (it) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        })
    }

}