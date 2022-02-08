# Fabric Example Mod

## Setup

Work with fabric api
Config by modifying fabric-broadcast.json:
 {
  "messages": [
    {
      "time": "00:00",  //24hr format
      "message": "§a[例行性公告]伺服器將於一分鐘後重啟"
    },
    {
      "time": "12:00", //24hr format
      "message": "§a[例行性公告]伺服器將於一分鐘後重啟"
    }
  ],
  "InfoMessage": "§a[伺服器規章]無，沒規則拉爽",
  "CycleMessage": "§a[公告] 這是公告 輸入/InfoMessage 獲取詳細資訊",
  "CycleMsgMinutes": 5,
  "EnableCycleMsgMinutes": true
}

## License

This template is available under the CC0 license. Feel free to learn from it and incorporate it in your own projects.
