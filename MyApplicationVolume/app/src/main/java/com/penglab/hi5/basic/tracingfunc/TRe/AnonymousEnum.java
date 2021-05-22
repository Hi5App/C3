package com.penglab.hi5.basic.tracingfunc.TRe;
import java.util.HashMap;
//Enums must be named in Java, so the following enum has been named AnonymousEnum:
public enum AnonymousEnum
{
    ALIVE(-1),
    TRIAL(0),
    FAR_(1);

    private int intValue;
    private static HashMap<Integer, AnonymousEnum> mappings;
    private static HashMap<Integer, AnonymousEnum> getMappings()
    {
        if (mappings == null)
        {
            synchronized (AnonymousEnum.class)
            {
                if (mappings == null)
                {
                    mappings = new HashMap<Integer, AnonymousEnum>();
                }
            }
        }
        return mappings;
    }

    private AnonymousEnum(int value)
    {
        intValue = value;
        AnonymousEnum.getMappings().put(value, this);
    }

    public int getValue()
    {
        return intValue;
    }

    public static AnonymousEnum forValue(int value)
    {
        return getMappings().get(value);
    }
}
// marching with bounding box
// Please make sure
// 1. sub_markers are located between nm1 and fm1

//ORIGINAL LINE: template<class T>






