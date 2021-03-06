package com.aabb.airlessbreathalyzer;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by oscar on 10/14/15.
 */

public class Profile implements Parcelable {
    /*
    Basic custom class that will store parameters for the profile
    The tricky part here is making it Parcelable. The reason that we made
    it parcelable is because this will be passed by multiple intents. In
    order to avoid having to pass all of this data as key value pairs, we
    wrap it into a parcelable object and can pass it as a whole object between
    intents.
     */

    public String name;
    public int age;
    public int weight;
    public String sex;
    public String word1;
    public String word2;
    public String word3;
    public double memScore;
    public double reflexScore;
    public double mathScore;


    public Profile(String profile) {
        String[] data = profile.split("`");
        this.name = data[0];
        this.age = Integer.parseInt(data[1]);
        this.weight = Integer.parseInt(data[2]);
        this.sex = data[3];
    }

    /*
    Constructor that takes a Parcel. We use this a lot
     */
    protected Profile(Parcel in) {
        name = in.readString();
        age = in.readInt();
        weight = in.readInt();
        sex = in.readString();
        word1 = in.readString();
        word2 = in.readString();
        word3 = in.readString();
        memScore = in.readDouble();
        reflexScore = in.readDouble();
        mathScore = in.readDouble();
    }

    /*
    This is the stuff that we have to override to make it parcelable.
    The most important method is writeToParcel
     */
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

    /*
    Here we store every field of the object into the Parcel that we're transporting
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeInt(weight);
        dest.writeString(sex);
        dest.writeString(word1);
        dest.writeString(word2);
        dest.writeString(word3);
        dest.writeDouble(memScore);
        dest.writeDouble(reflexScore);
        dest.writeDouble(mathScore);
    }
}
