package com.example.semakerandroid.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.semakerandroid.PastView
import com.example.semakerandroid.Presenters.PastPresenter
import com.example.semakerandroid.R
import kotlinx.android.synthetic.main.fragment_editprofile.view.*

class PastFragment : MvpAppCompatFragment(), PastView {

    @InjectPresenter
    lateinit var pastPresenter: PastPresenter

    @ProvidePresenterTag(presenterClass = PastPresenter::class)
    fun providePastPresenterTag(): String = "Hello"

    @ProvidePresenter
    fun providePastPresenter() = PastPresenter()

    private var root: View? = null

    companion object {
        fun newInstance(): PastFragment =
            PastFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_past, null)
        return root
    }

    override fun hide() {
        root!!.linearLayout.visibility = CoordinatorLayout.INVISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.VISIBLE
    }

    override fun show() {
        root!!.linearLayout.visibility = CoordinatorLayout.VISIBLE
        root!!.progressBar.visibility = CoordinatorLayout.INVISIBLE
    }
}