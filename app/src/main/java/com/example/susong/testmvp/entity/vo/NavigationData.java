package com.example.susong.testmvp.entity.vo;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationData implements Parcelable {
    public boolean isShowMask;
    public int pageNum;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(isShowMask ? (byte) 1 : (byte) 0);
        dest.writeInt(this.pageNum);
    }

    public NavigationData() {
    }

    protected NavigationData(Parcel in) {
        this.isShowMask = in.readByte() != 0;
        this.pageNum = in.readInt();
    }

    public static final Creator<NavigationData> CREATOR = new Creator<NavigationData>() {
        public NavigationData createFromParcel(Parcel source) {
            return new NavigationData(source);
        }

        public NavigationData[] newArray(int size) {
            return new NavigationData[size];
        }
    };
}
