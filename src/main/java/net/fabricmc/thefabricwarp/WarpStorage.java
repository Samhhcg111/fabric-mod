package net.fabricmc.thefabricwarp;

import java.util.ArrayList;

public class WarpStorage  {
    private ArrayList<WarpPoint> WarpPoints;
    public WarpStorage(){
        WarpPoints = new ArrayList<WarpPoint>();
    }
    
    public boolean setWarpoint( String WarpName,String world,int x ,int y, int z,float yaw,float pitch){
        if(!WarpPoints.isEmpty()){
            for(int i=0;i<WarpPoints.size();i++){
                if(WarpPoints.get(i).getName().equals(WarpName)){
                    return false;
                }
            }
        }
        WarpPoints.add(new WarpPoint(WarpName, world, x, y, z,yaw,pitch));
        return true;
    }
    public boolean deleteWarpPoint(String name){
        for(int i=0;i<WarpPoints.size();i++){
            if(WarpPoints.get(i).getName().equals(name)){
               WarpPoints.remove(i);
               return true;
            }
        }
        return false;
    }
    public void clearAll(){
        WarpPoints.clear();
    }
    public ArrayList<WarpPoint> getWarpPoints(){
        return WarpPoints;
    }
}
