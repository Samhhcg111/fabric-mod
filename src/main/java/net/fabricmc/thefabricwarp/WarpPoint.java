package net.fabricmc.thefabricwarp;

public class WarpPoint {
    private String name;
    private String world;
    private int x;
    private int y;
    private int z;
    private float yaw;
    private float pitch;
    public WarpPoint(String WarpName,String world,int x ,int y, int z,float yaw,float pitch){
        this.name=WarpName;
        this.world=world;
        this.x=x;
        this.y=y;
        this.z=z;
        this.yaw=yaw;
        this.pitch=pitch;
    }
    public String getName(){
        return this.name;
    }
    public String getWorld(){
        return this.world;
    }
    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public int getZ(){
        return this.z;
    }
    public float getYaw(){
        return this.yaw;
    }
    public float getPitch(){
        return this.pitch;
    }
}
