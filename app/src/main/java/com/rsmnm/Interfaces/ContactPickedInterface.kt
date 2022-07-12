package com.rsmnm.Interfaces

import com.rsmnm.Models.LocationItem

/**
 * Created by saqib on 9/12/2018.
 */
interface ContactPickedInterface {
    fun onContactSelected(name: String, contact: String)
}