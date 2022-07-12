package com.rsmnm.Fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.rsmnm.BaseClasses.BaseFragment
import com.rsmnm.Models.CardItem
import com.rsmnm.Models.Resource
import com.rsmnm.Models.UserItem
import com.rsmnm.Networking.WebResponse
import com.rsmnm.R
import com.rsmnm.Utils.AppConstants
import com.rsmnm.ViewModels.PassengerViewModel
import com.rsmnm.Views.TitleBar
import com.stripe.android.Stripe
import com.stripe.android.TokenCallback
import com.stripe.android.model.Token
import hari.bounceview.BounceView
import kotlinx.android.synthetic.main.fragment_payment.*
import java.lang.Exception

/**
 * Created by saqib on 9/11/2018.
 */
class PaymentFragment : BaseFragment(), Observer<Resource<WebResponse<java.util.ArrayList<CardItem>>>> {

    lateinit var passengerViewModel: PassengerViewModel
    var cardList: ArrayList<CardItem> = ArrayList()
    var isBackAfterSave = false;

    companion object {
        fun newInstance(backAfterSave: Boolean) = PaymentFragment().apply {
            arguments = Bundle(2).apply {
                isBackAfterSave = backAfterSave
            }
        }
    }

    override fun getLayout(): Int = R.layout.fragment_payment

    override fun getTitleBar(titleBar: TitleBar) {
        titleBar.resetTitleBar().enableBack().setTitle("Payments")
    }

    override fun activityCreated(savedInstanceState: Bundle?) {

        passengerViewModel.getCard().observe(this@PaymentFragment, this)
    }

    override fun inits() {
        passengerViewModel = ViewModelProviders.of(fragmentActivity).get(PassengerViewModel::class.java)
    }

    override fun setEvents() {

        BounceView.addAnimTo(btn_delete)

        btn_submit.setOnClickListener {
            var card = card_input_widget.card
            if (card == null)
                makeSnackbar("Invalid Card")
            else {
                showLoader()
                val stripe = Stripe(context!!, AppConstants.STRIPE_KEY)
                stripe.createToken(card, object : TokenCallback {
                    override fun onSuccess(token: Token?) {
                        passengerViewModel.addCard(CardItem(token?.id, card.last4)).observe(this@PaymentFragment, Observer { response ->
                            when (response?.status) {
                                Resource.Status.success -> {
                                    if (isBackAfterSave)
                                        fragmentActivity.actionBack()
                                    else {
                                        hideLoader()
                                        card_input_widget.clear()
                                        makeSnackbar(response?.data)
                                        cardList.add(response?.data?.body!!)
                                        setData()
                                    }
                                }
                                else -> {
                                    hideLoader()
                                }
                            }
                        })
                    }

                    override fun onError(error: Exception?) {
                        makeSnackbar(error?.localizedMessage)
                    }
                })
            }
        }

        btn_delete.setOnClickListener {
            passengerViewModel.removeCard(cardList.get(0)).observe(this@PaymentFragment, Observer { response ->
                when (response?.status) {
                    Resource.Status.loading -> showLoader()
                    Resource.Status.success -> {
                        hideLoader()
                        makeSnackbar(response?.data)
                        cardList.clear()
                        setData()
                    }
                    else -> {
                        makeSnackbar(response?.data)
                        hideLoader()
                    }
                }
            })
        }
    }

    fun setData() {

        if (cardList.isEmpty()) {
            layout_add_card.visibility = View.VISIBLE
            layout_view_card.visibility = View.GONE
        } else {
            layout_add_card.visibility = View.GONE
            layout_view_card.visibility = View.VISIBLE

            val cardItem = cardList.get(0)
            txt_card_last_four.setText("**** **** " + cardItem.last_digits)


        }

    }


    override fun onChanged(response: Resource<WebResponse<java.util.ArrayList<CardItem>>>?) {

        when (response?.status) {
            Resource.Status.loading -> showLoader()
            Resource.Status.success -> {
                hideLoader()
                cardList.addAll(response.data?.body!!)
                setData()
            }
            Resource.Status.error -> {
                hideLoader()
            }
            else -> {
                makeSnackbar(response?.data)
                hideLoader()
            }
        }
    }
}