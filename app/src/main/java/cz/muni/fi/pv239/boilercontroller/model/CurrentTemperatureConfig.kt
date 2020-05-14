package cz.muni.fi.pv239.boilercontroller.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentTemperatureConfig(
    val _id: String,
    val temperature: Number,
    val time: Long
) : Parcelable