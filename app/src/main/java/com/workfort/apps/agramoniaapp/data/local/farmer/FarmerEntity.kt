package com.workfort.apps.agramoniaapp.data.local.farmer

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class FarmerEntity(val id: Int,
                        val name: String?,
                        val location: String?,
                        @SerializedName("profile_image") val profileImg: String?,
                        val phone: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(profileImg)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FarmerEntity> {
        override fun createFromParcel(parcel: Parcel): FarmerEntity {
            return FarmerEntity(parcel)
        }

        override fun newArray(size: Int): Array<FarmerEntity?> {
            return arrayOfNulls(size)
        }
    }
}