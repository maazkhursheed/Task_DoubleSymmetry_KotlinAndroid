package com.rsmnm.Interfaces

import com.rsmnm.Models.LocationItem

/**
 * Created by saqib on 9/12/2018.
 */
public interface LocationPickedInterface {
    fun onLocationSelected(location: LocationItem)
}