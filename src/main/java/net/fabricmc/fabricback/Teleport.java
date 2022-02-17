package net.fabricmc.fabricback;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

public class Teleport implements Runnable{
    private ServerWorld world;
    private int x;
    private int y;
    private int z;
    private float yaw;
    private float pitch;
    private ServerPlayerEntity player;
    private BlockPos initPos;
    public Teleport(ServerPlayerEntity player,String world,int x,int y,int z,float yaw,float pitch){
        this.player=player;
        this.x=x;
        this.y=y;
        this.z=z;
        this.yaw=yaw;
        this.pitch=pitch;
        this.initPos=player.getBlockPos();
        
        player.getServer().getWorlds().forEach(w->{
            if(w.getRegistryKey().getValue().toString().equals(world)){
                this.world=w;
            }
        });
    }
    @Override
    public void run(){
        long initTime=Util.getMeasuringTimeMs();
        int phase=0;
        boolean Once=false;
        while(true){
            long sec=Util.getMeasuringTimeMs()-initTime;
            int Sec=(int)sec/1000;
            BlockPos Pos = player.getBlockPos();
            if(!Once){
                player.sendSystemMessage(Text.of("§6Start teleport .DON'T MOVE "), Util.NIL_UUID);
                Once=true;
            }
            if(initPos.getX()==Pos.getX()&&initPos.getY()==Pos.getY()&&initPos.getZ()==Pos.getZ()){
                if(Sec==1){
                    initTime=Util.getMeasuringTimeMs();
                    if(phase==0){
                        player.sendSystemMessage(Text.of("§63"), Util.NIL_UUID);
                    }
                    if(phase==1){
                        player.sendSystemMessage(Text.of("§62"), Util.NIL_UUID);
                    }
                    if(phase==2){
                        player.sendSystemMessage(Text.of("§61"), Util.NIL_UUID);
                    }
                    if(phase==3){
                        player.teleport(world, x, y, z,yaw, pitch);
                        break;
                    }
                    phase+=1;
                }
            }else{
                player.sendSystemMessage(Text.of("§6Cancel teleport"), Util.NIL_UUID);
                    break;
            }
        }
       
    }
}
