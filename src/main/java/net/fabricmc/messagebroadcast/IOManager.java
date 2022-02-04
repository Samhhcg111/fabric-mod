package net.fabricmc.messagebroadcast;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class IOManager {
    public static void genConfig(){
        if(!Files.exists(Paths.get("./config/fabric-broadcast.json"))){
            String gson = new GsonBuilder().setPrettyPrinting().create().toJson(new ConfigInstance());
            File file = new File("./config/fabric-broadcast.json");
            write(file, gson);
        }
    }
    public static ConfigInstance readConfig(){
        ConfigInstance configInstance;
        try{
            configInstance = new Gson().fromJson(new FileReader("./config/fabric-broadcast.json"), ConfigInstance.class);
        }
        catch(IOException e){
            e.printStackTrace();
            configInstance = new ConfigInstance();
        }
        return configInstance;
    }
    public static void write(File file,String json){
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        try(FileWriter writer = new FileWriter(file)){
            writer.write(json);
        }catch(IOException e){
            e.printStackTrace();
        }
    }   
}
