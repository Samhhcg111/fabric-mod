package net.fabricmc.messagebroadcast;

import java.util.ArrayList;
import java.util.List;

public class ConfigInstance {
    public List<Message> messages = new ArrayList<>();
    public String CycleMessage;
    public int CycleMsgMinutes;
    public ConfigInstance(){
        messages.add(new Message("00:00", "§a[例行性公告]伺服器將於一分鐘後重啟"));
        messages.add(new Message("12:00", "§a[例行性公告]伺服器將於一分鐘後重啟"));
        CycleMessage = "§a[公告] 這是公告";
        CycleMsgMinutes= 5;
    }
}
