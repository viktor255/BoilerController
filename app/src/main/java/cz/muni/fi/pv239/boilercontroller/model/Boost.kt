package cz.muni.fi.pv239.boilercontroller.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class Boost(
    val _id: String,
    val author: String,
    val duration: Long,
    val temperature: Number,
    val time: Long
) : Parcelable
