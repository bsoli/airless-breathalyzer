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
    public String word1;
    public String word2;
    public String word3;
    public String word4;
    public String word5;
    public double memScore;
    public double reflexScore;
    public double mathScore;


    public Profile(String profile) {
        String[] data = profile.split("`");
        this.name = data[0];
        this.age = Integer.parseInt(data[1]);
        this.weight = Integer.parseInt(data[2]);
        this.race = data[3];
        this.sex = data[4];
    }

    protected Profile(Parcel in) {
        name = in.readString();
        age = in.readInt();
        weight = in.readInt();
        race = in.readString();
        sex = in.readString();
        word1 = in.readString();
        word2 = in.readString();
        word3 = in.readString();
        word4 = in.readString();
        word5 = in.readString();
        memScore = in.readDouble();
        reflexScore = in.readDouble();
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
        dest.writeString(word1);
        dest.writeString(word2);
        dest.writeString(word3);
        dest.writeString(word4);
        dest.writeString(word5);
        dest.writeDouble(memScore);
        dest.writeDouble(reflexScore);
        dest.writeDouble(mathScore);
    }
}
