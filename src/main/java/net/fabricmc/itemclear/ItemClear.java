package net.fabricmc.itemclear;

// import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Util;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.mojang.brigadier.Command;

public class ItemClear implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("item-clear-mod");
	public static ConfigInstance CONFIG;
	public static final ScheduledExecutorService BroadcastExecutor = Executors.newSingleThreadScheduledExecutor();
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Item-clear-mod loaded");
		ServerLifecycleEvents.SERVER_STARTING.register(this::init);
		ServerLifecycleEvents.SERVER_STARTED.register(this::initRoutineBroadcast);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onShutdown);
		/*  --------                  Init commands       ----------               */
		// /ItemClear clear  :  clear dropped item
		// /ItemClear reloadConfig : reload configurantion
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("ItemClear").requires(source -> source.hasPermissionLevel(4)
			).then(CommandManager.literal("reloadConfig").executes(
				ctx -> {
					CONFIG = IOManager.readConfig(); 	
					broadcast(ctx.getSource(), Text.of("§c[Item-clear-mod]Reloaded configuration."));
					LOGGER.info("[Item-clear-mod]Reloaded configuration");
					return Command.SINGLE_SUCCESS;}
				)).then(CommandManager.literal("clear").executes(
					ctx -> {
						broadcast(ctx.getSource(), Text.of(CONFIG.ClearMessage));
						ctx.getSource().getServer().getWorlds().forEach(
							world -> world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), p->{return true;}).forEach(e ->e.setDespawnImmediately())
						);
						return Command.SINGLE_SUCCESS;
					}
				)));
			}
		);
	}
	public void init(MinecraftServer server){
		IOManager.genConfig();
		CONFIG = IOManager.readConfig();
	}
	public void initRoutineBroadcast(MinecraftServer server){
		Runnable r = new Runnable() {
			int timerSec=0;
			@Override 
			public void run(){
				if(!server.isRunning()){
					return;
				}
				if (timerSec/60>=CONFIG.CycleMinutes){
					if(timerSec%60==0&&timerSec/60==CONFIG.CycleMinutes){
						broadcast(server, new LiteralText(CONFIG.AlarmMessage));
					}
					int secCount = timerSec-CONFIG.CycleMinutes*60;
					if(CONFIG.Enable15Countdown&&CONFIG.AlarmSec<30){CONFIG.AlarmSec=30;LOGGER.info("[Item-clear-mod]AlarmSec too small , reset to 30");}
					if(CONFIG.Enable15Countdown&&secCount==CONFIG.AlarmSec-15){
						broadcast(server, new LiteralText("§d[注意]將於15秒後清除掉落物"));
					}
					if(CONFIG.Enable15Countdown&&secCount==CONFIG.AlarmSec-5){
						broadcast(server, new LiteralText("§d[注意]將於5秒後清除掉落物"));
					}
					if(secCount==CONFIG.AlarmSec){
						broadcast(server, new LiteralText(CONFIG.ClearMessage));
						server.getWorlds().forEach(
							w -> w.getEntitiesByType(EntityType.ITEM, p->{return true;}).forEach(e ->e.setDespawnImmediately())
						);
						timerSec=0;
					}
				}
				
				timerSec+=1;
			}
			
		};
		BroadcastExecutor.scheduleAtFixedRate(r, 30, 1, TimeUnit.SECONDS);
	}
	public void onShutdown(MinecraftServer server){
		BroadcastExecutor.shutdown();
		LOGGER.info("Item-clear-mod shutdown");
	}
	public static int broadcast(ServerCommandSource source,Text message){
		source.getServer().getPlayerManager().getPlayerList().forEach(playerEntity->playerEntity.sendSystemMessage(message,Util.NIL_UUID));
		return Command.SINGLE_SUCCESS;
	}
	public static void broadcast(MinecraftServer server,Text message){
		server.getPlayerManager().getPlayerList().forEach(player -> player.sendSystemMessage(message, Util.NIL_UUID));
	}
}
