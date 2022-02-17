package net.fabricmc.fabricback.mixin;

import net.fabricmc.fabricback.BackPlayerData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public class DataStorageMIX implements BackPlayerData{
	@Unique private String backWorld;
	@Unique private int backX;
	@Unique private int backY;
    @Unique private int backZ;
	@Unique private boolean hasPos;
	@Override
	public String getbackWorld(){
		return this.backWorld;
	}

	@Override
	public int getbackX(){
		return this.backX;
	}

	@Override
	public int getbackY(){
		return this.backY;
	}

	@Override
	public int getbackZ(){
		return this.backZ;
	}
	@Override
	public boolean hasPos(){
		return this.hasPos;
	}
	@Inject(
		method = "onDeath",
		at = @At(
			value = "HEAD"
		)
	)
	private void updatePos(DamageSource source,CallbackInfo ci){
		Vec3d pos = source.getPosition();
		if(pos!=null){
			this.hasPos=true;
			this.backX=(int)pos.x;
			this.backY=(int)pos.y;
			this.backZ=(int)pos.z;
			this.backWorld=source.getAttacker().getWorld().getRegistryKey().getValue().toString();
		}else{
			this.hasPos=false;
		}
	}
	@Inject(
		method = "writeCustomDataToNbt",
		at = @At(
				value = "TAIL"
		)
	)
	private void saveData(NbtCompound tag, CallbackInfo ci) {
		if(this.backWorld!=null){
			tag.putString("DeathWorld", this.backWorld);
			tag.putInt("DeathX", this.backX);
			tag.putInt("DeathY", this.backY);
			tag.putInt("DeathZ", this.backZ);
			tag.putBoolean("HasDeathPos", this.hasPos);
		}
	}

	@Inject(
			method = "readCustomDataFromNbt",
			at = @At(
					value = "TAIL"
			)
	)
	private void readData(NbtCompound tag, CallbackInfo ci) {
		if (tag.contains("DeathWorld")) {
			this.backWorld = tag.getString("DeathWorld");
		}
		if (tag.contains("DeathX")) {
			this.backX = tag.getInt("DeathX");
		}
		if (tag.contains("DeathY")) {
			this.backY = tag.getInt("DeathY");
		}
		if (tag.contains("DeathZ")) {
			this.backZ = tag.getInt("DeathZ");
		}
		if(tag.contains("HasDeathPos")){
			this.hasPos=tag.getBoolean("HasDeathPos");
		}
	}

	@Inject(
			method = "copyFrom",
			at = @At(
					value = "TAIL"
			)
	)
	private void copyData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		this.backWorld=((BackPlayerData)oldPlayer).getbackWorld();
		this.backX=((BackPlayerData)oldPlayer).getbackX();
		this.backY=((BackPlayerData)oldPlayer).getbackY();
		this.backZ=((BackPlayerData)oldPlayer).getbackZ();
		this.hasPos=((BackPlayerData)oldPlayer).hasPos();
	}

}
