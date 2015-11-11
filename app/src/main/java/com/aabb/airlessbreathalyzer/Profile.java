package com.aabb.airlessbreathalyzer;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by oscar on 10/14/15.
 */

public class Profile implements Parcelable {
    public String name;
    public int age;
    public int weight;
    public String race;
    public String sex;
    public int memScore;
    public int reflexScore;
    public double mathScore;


    public Profile(String profile) {
        String[] data = profile.split("`");
        this.name = data[0];
        this.age = Integer.getInteger(data[1]);
        this.weight = Integer.getInteger(data[2]);
        this.race = data[3];
        this.sex = data[4];
    }

    protected Profile(Parcel in) {
        name = in.readString();
        age = in.readInt();
        weight = in.readInt();
        race = in.readString();
        sex = in.readString();
        memScore = in.readInt();
        reflexScore = in.readInt();
        mathScore = in.readDouble();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(weight);
        dest.writeString(race);
        dest.writeString(sex);
        dest.writeInt(memScore);
        dest.writeInt(reflexScore);
        dest.writeDouble(mathScore);

    }
}
