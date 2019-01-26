package com.workfort.apps.agramonia.data.local.blog

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class BlogEntity(val id: String?,
                      val title: String?,
                      val description: String?,
                      @SerializedName("image_list") val imageList: List<String>?)
    : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createStringArrayList()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeStringList(imageList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BlogEntity> {
        override fun createFromParcel(parcel: Parcel): BlogEntity {
            return BlogEntity(parcel)
        }

        override fun newArray(size: Int): Array<BlogEntity?> {
            return arrayOfNulls(size)
        }
    }
}