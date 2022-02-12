package net.fabricmc.playtimeInfo.util;

public class TimeTransform {
    public static String prettyform(long time){
        long seconds = time/1000L;
        long minutes = seconds/60L;
        long hours = minutes/60L;
        minutes-= hours*60L;
        long days = hours/24L;
        hours -=(days*24L);
        
        String result = Long.toString(days)+" 天 "+Long.toString(hours)+" 時 "+Long.toString(minutes)+" 分 ";
        return result;
    }
}
