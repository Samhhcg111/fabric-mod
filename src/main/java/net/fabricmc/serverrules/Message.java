package net.fabricmc.messagebroadcast;

public class Message {
    protected String time;
    protected String message;
    public Message(String time , String message){
        this.time=time;
        this.message=message;
    }
    public String getMessage(){
        return this.message;
    }
    public String getTime(){
        return this.time;
    }
}
