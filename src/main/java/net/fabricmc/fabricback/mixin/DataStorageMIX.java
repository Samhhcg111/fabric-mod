package net.fabricmc.fabricback.mixin;

import net.fabricmc.fabricback.BackPlayerData;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

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
	@Inject(
		method = "onDeath",
		at = @At(
			value = "HEAD"
		)
	)
	private void updatePos(DamageSource source,CallbackInfo ci){
		ServerPlayerEntity player = ((ServerPlayerEntity)(Object)this);
		this.backWorld=player.getWorld().getRegistryKey().getValue().toString();
		this.backX=player.getBlockX();
		this.backY=player.getBlockY();
		this.backZ=player.getBlockZ();
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
	}

}
