package cz.muni.fi.pv239.boilercontroller.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class BoostConfig(
    val _id: String,
    val duration: Long,
    val temperature: Int
) : Parcelable