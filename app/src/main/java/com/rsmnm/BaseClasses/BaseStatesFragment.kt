package com.rsmnm.BaseClasses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.TextView

import com.rsmnm.R
import kotlinx.android.synthetic.main.fragment_states.*

/**
 * Created by rah on 07-Dec-17.
 */

abstract class BaseStatesFragment : BaseFragment() {

    protected abstract fun getStubLayout(): Int

    override fun getLayout(): Int = R.layout.fragment_states

    lateinit var stubView: ViewStub
    lateinit var layoutError: View
    lateinit var layoutLoading: View
    lateinit var btnRetry: View

    override fun inits() {
        btnRetry.setOnClickListener { onRetryClicked() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(layout, container, false)
        stubView = view.findViewById(R.id.view_stub)
        layoutError = view.findViewById(R.id.layout_connection_error)
        layoutLoading = view.findViewById(R.id.layout_loading)
        btnRetry = view.findViewById(R.id.btn_retry)

        stubView.layoutResource = getStubLayout()
        stubView.inflate()
        return view
    }

    protected abstract fun onRetryClicked()

    fun setContentType(type: ContentType) {
        when (type) {
            BaseStatesFragment.ContentType.error -> {
                stubView.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
                layoutLoading.visibility = View.GONE
            }
            BaseStatesFragment.ContentType.loading -> {
                stubView.visibility = View.GONE
                layoutError.visibility = View.GONE
                layoutLoading.visibility = View.VISIBLE
            }
            BaseStatesFragment.ContentType.content -> {
                stubView.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
                layoutLoading.visibility = View.GONE
            }
        }
    }

    enum class ContentType {
        error,
        loading,
        content
    }

}
