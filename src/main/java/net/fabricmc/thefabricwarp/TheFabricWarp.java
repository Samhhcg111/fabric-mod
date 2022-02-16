package net.fabricmc.thefabricwarp;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.thefabricwarp.IO.IOManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheFabricWarp implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("the-fabric-warp");
	public static WarpStorage STORAGE;
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("the-fabric-warp mod loaded");
		ServerLifecycleEvents.SERVER_STARTED.register(this::init);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onStop);
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("setWarp").requires(source->source.hasPermissionLevel(4)
			).then(CommandManager.literal("set").then(CommandManager.argument("name", StringArgumentType.word()).executes(
				ctx->setWarp(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "name"))
			))).then(CommandManager.literal("delete").then(CommandManager.argument("name", StringArgumentType.word()).executes(
				ctx->delWarp(ctx.getSource().getPlayer(), StringArgumentType.getString(ctx, "name"))
			))).then(CommandManager.literal("clear").executes(
				ctx->clearAll(ctx.getSource().getPlayer())
			)));
		});
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("Warp").then(CommandManager.literal("List").executes(
				ctx->ListWarps(ctx.getSource().getPlayer())
			)).then(CommandManager.argument("name", StringArgumentType.word()).executes(
				ctx->Warp(ctx.getSource().getPlayer(),StringArgumentType.getString(ctx, "name"))
			)));
		});
	}
	public void init(MinecraftServer server){
		IOManager.genData();
		STORAGE = IOManager.readData();
	}
	public static int setWarp(ServerPlayerEntity player, String name){
		BlockPos Pos = player.getBlockPos();
		if(STORAGE.setWarpoint(name, player.getWorld().getRegistryKey().getValue().toString(), Pos.getX(), Pos.getY(), Pos.getZ(),player.getYaw(),player.getPitch())){
			player.sendSystemMessage(Text.of(
			"§6Set "+name+"("+player.getWorld().getRegistryKey().getValue().toString()+")"
			+" at X="+Integer.toString(Pos.getX())+" Y= "+Integer.toString(Pos.getY())+" Z= "+Integer.toString(Pos.getZ())
			+" Yaw= "+Float.toString(player.getYaw())+" Pitch= "+Float.toString(player.getPitch())
			), Util.NIL_UUID);
		}else{
			player.sendSystemMessage(Text.of("§6Exist warp name : "+name),Util.NIL_UUID);
		}
		return Command.SINGLE_SUCCESS;
	}
	public static int delWarp(ServerPlayerEntity player,String name){
		if(STORAGE.deleteWarpPoint(name)){
			player.sendSystemMessage(Text.of("§6Deleted warp point: "+name), Util.NIL_UUID);
		}else{
			player.sendSystemMessage(Text.of("§6No warp point found: "+name), Util.NIL_UUID);
		}
		return Command.SINGLE_SUCCESS;
	}
	public static int clearAll(ServerPlayerEntity player){
		STORAGE.clearAll();
		player.sendSystemMessage(Text.of("§6Clear all warp points"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	public static String ListMsg;
	public static int ListWarps(ServerPlayerEntity player){
		ListMsg = "§6WarpList: \n";
		if(STORAGE.getWarpPoints().isEmpty()){
			player.sendSystemMessage(Text.of(ListMsg+"Null"),Util.NIL_UUID);
			return Command.SINGLE_SUCCESS;
		}
		STORAGE.getWarpPoints().forEach(wp->{
			ListMsg+=wp.getName();
			ListMsg+="("+wp.getWorld()+")";
			ListMsg+=": X= "+wp.getX()+" Y= "+wp.getY()+" Z= "+wp.getZ();
			// ListMsg+=" Yaw= "+wp.getYaw()+" Pitch= "+wp.getPitch();
			ListMsg+="\n";
		});
		player.sendSystemMessage(Text.of(ListMsg),Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	public static int Warp(ServerPlayerEntity player,String name){
		for(WarpPoint wp:STORAGE.getWarpPoints()){
			if(wp.getName().equals(name)){
				Thread tp = new Thread(new Teleport(player, wp.getWorld(), wp.getX(), wp.getY(), wp.getZ(), wp.getYaw(), wp.getPitch()));
				tp.start();
				return Command.SINGLE_SUCCESS;
			}
		}
		player.sendSystemMessage(Text.of("§6WarpPoint dosen't exist"), Util.NIL_UUID);
		return Command.SINGLE_SUCCESS;
	}
	public void onStop(MinecraftServer server){
		IOManager.save(STORAGE);
	}
}
