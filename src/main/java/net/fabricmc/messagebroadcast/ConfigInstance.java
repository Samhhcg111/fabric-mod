package net.fabricmc.messagebroadcast;

import java.util.ArrayList;
import java.util.List;

public class ConfigInstance {
    // this class will save as MessageBroadcastConfig.json
    public List<Message> messages = new ArrayList<>();
    public String InfoMessage;
    public String CycleMessage;
    public int CycleMsgMinutes;
    public boolean EnableCycleMsgMinutes;
    public ConfigInstance(){
        messages.add(new Message("00:00", "§a[例行性公告]伺服器將於一分鐘後重啟"));
        messages.add(new Message("12:00", "§a[例行性公告]伺服器將於一分鐘後重啟"));
        InfoMessage = "§a[伺服器規章]無，沒規則拉爽";
        CycleMessage = "§a[公告] 這是公告 輸入/InfoMessage 獲取詳細資訊";
        CycleMsgMinutes= 5;
        EnableCycleMsgMinutes = true;
    }
}
