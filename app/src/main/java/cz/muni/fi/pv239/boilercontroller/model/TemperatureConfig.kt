package cz.muni.fi.pv239.boilercontroller.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class TemperatureConfig(
    val _id: String = "",
    val temperature: Number = 0,
    val time: String = "00:00"
) : Parcelable