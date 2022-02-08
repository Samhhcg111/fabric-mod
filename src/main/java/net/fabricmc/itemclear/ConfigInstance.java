package net.fabricmc.itemclear;

public class ConfigInstance {
    public boolean EnableMod;
    public boolean Enable15Countdown;
    public int CycleMinutes;
    public int AlarmSec;
    public String AlarmMessage;
    public String ClearMessage;
    public ConfigInstance(){
        EnableMod=true;
        Enable15Countdown=true;
        CycleMinutes=5;
        AlarmSec=30;
        AlarmMessage = "§d[注意]將於30秒後清除掉落物";
        ClearMessage = "§d[注意]掉落物已清除，感謝你的注意";
    }
}
