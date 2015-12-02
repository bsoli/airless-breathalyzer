package com.aabb.airlessbreathalyzer;

public class UpdownCasting {

    public float angleCasting(float strAngle)
    {
        float angle;
        //int ceilBF=(int)Math.ceil(floatHeight);
        float diff=strAngle%10;
        float flourBF=strAngle-diff;
        System.out.println("f1"+strAngle);
        System.out.println("diff"+flourBF);
        System.out.println("diff was"+diff);

        if(diff<5){
            //System.out.println("0.0....");
            angle=flourBF;
        }
        else
        {
            //System.out.println("!=0.0......");
            angle=10.0f+flourBF;
        }
        //System.out.println("float after downcasting"+angle);

        return angle;
    }

}
