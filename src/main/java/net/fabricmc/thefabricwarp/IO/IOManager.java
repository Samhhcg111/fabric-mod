package net.fabricmc.thefabricwarp.IO;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.thefabricwarp.WarpStorage;

public class IOManager {
    public static void genData(){
        if(!Files.exists(Paths.get("./config"))){
            createConfigFolder();
        }
        if(!Files.exists(Paths.get("./config/TheFabricWarpData.json"))){
            String gson = new GsonBuilder().setPrettyPrinting().create().toJson(new WarpStorage());
            File file = new File("./config/TheFabricWarpData.json");
            write(file, gson);
        }
    }
    public static WarpStorage readData(){
        WarpStorage ws;
        try{
            ws = new Gson().fromJson(new FileReader("./config/TheFabricWarpData.json"), WarpStorage.class);
        }
        catch(IOException e){
            e.printStackTrace();
            ws = new WarpStorage();
        }
        return ws;
    }
    public static void createConfigFolder(){
        try{
            new File("./config").mkdir();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void save(WarpStorage ws){
        String gson = new GsonBuilder().setPrettyPrinting().create().toJson(ws);
        File file = new File("./config/TheFabricWarpData.json");
        write(file, gson);
    }
    public static void write(File file,String json){
        try{
            file.createNewFile();
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
