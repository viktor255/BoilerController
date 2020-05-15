package cz.muni.fi.pv239.boilercontroller.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val userId: String,
    val email: String,
    val expiresIn: Long,
    val token: String
) : Parcelable