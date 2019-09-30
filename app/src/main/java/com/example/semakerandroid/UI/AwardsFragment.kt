package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.AwardsView
import com.example.semakerandroid.Presenters.AwardsPresenter
import com.example.semakerandroid.R

class AwardsFragment : MvpAppCompatFragment(), AwardsView {

    @InjectPresenter
    lateinit var awardsPresenter: AwardsPresenter

    @ProvidePresenterTag(presenterClass = AwardsPresenter::class)
    fun provideAwardsPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun provideAwardsPresenter() = AwardsPresenter()

    companion object {
        fun newInstance(): AwardsFragment =
            AwardsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_awards, null)
    }

    override fun hide() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun show() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
