package com.ljb.mvp.kotlin.view.act

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.ljb.mvp.kotlin.R
import com.ljb.mvp.kotlin.common.Constant
import com.ljb.mvp.kotlin.contract.LoginContract
import com.ljb.mvp.kotlin.presenter.LoginPresenter
import com.ljb.mvp.kotlin.utils.PermissionUtils
import com.ljb.mvp.kotlin.widget.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_login.*
import mvp.ljb.kt.act.BaseMvpActivity

/**
 * @Author:Kotlin MVP Plugin
 * @Date:2019/04/20
 * @Description input description
 **/
class LoginActivity : BaseMvpActivity<LoginContract.IPresenter>(), LoginContract.IView {

    private val mLoadingDialog by lazy { LoadingDialog(this) }

    override fun getLayoutId() = R.layout.activity_login

    override fun registerPresenter() = LoginPresenter::class.java

    override fun init(savedInstanceState: Bundle?) {
        initPermission()
    }

    private fun initPermission() {
        PermissionUtils.requestPermission(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE),
                Constant.PermissionCode.CODE_INIT)
        { _, _ ->
            //在这里处理权限结果
        }
    }

    override fun initView() {
        btn_login.setOnClickListener { login() }
    }

    override fun initData() {
        val login = getPresenter().getLocLogin()
        if (TextUtils.isEmpty(login)) {
            showLoginView()
        } else {
            getPresenter().delayGoHomeTask()
        }
    }

    override fun loginSuccess() {
        goHome()
    }

    override fun loginError(errorMsg: String?) {
        tv_tip.visibility = View.VISIBLE
        if (errorMsg.isNullOrEmpty()) {
            tv_tip.setText(R.string.net_error)
        } else {
            tv_tip.text = errorMsg
        }
    }

    override fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun showLoginView() {
        val alpha = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        val scaleX = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat("scaleY", 0.5f, 1f)
        ObjectAnimator.ofPropertyValuesHolder(ll_login, alpha, scaleX, scaleY).setDuration(1000).start()
        ll_login.visibility = View.VISIBLE
    }


    private fun login() {
        if (et_github.text.isNullOrBlank()) {
            tv_tip.visibility = View.VISIBLE
            tv_tip.setText(R.string.input_user)
            return
        }
        getPresenter().login(et_github.text.trim().toString())
    }

    override fun dismissLoadDialog() {
        mLoadingDialog.dismiss()
    }

    override fun showLoadDialog() {
        mLoadingDialog.show()
    }

}
