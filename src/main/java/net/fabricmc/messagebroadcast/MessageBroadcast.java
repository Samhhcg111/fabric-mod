package net.fabricmc.messagebroadcast;

// import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.mojang.brigadier.Command;

public class MessageBroadcast implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("message-broadcast-mod");
	public static ConfigInstance CONFIG;
	public static final ScheduledExecutorService BroadcastExecutor = Executors.newSingleThreadScheduledExecutor();
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Message-broadcast mod loaded");
		ServerLifecycleEvents.SERVER_STARTING.register(this::init);
		ServerLifecycleEvents.SERVER_STARTED.register(this::initRoutineBroadcast);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onShutdown);
		/*  --------                  Init commands       ----------               */
		// /MessageBroadcast "message"  : Broadcast "message"
		// /MessageBroadcast reloadConfig : reload configurantion
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("MessageBroadcast").then(CommandManager.literal("Broadcast").then(CommandManager.argument("message", MessageArgumentType.message()).executes(
				ctx->broadcast(ctx.getSource(),MessageArgumentType.getMessage(ctx,"message"))))
				).then(CommandManager.literal("reloadConfig").executes(
				ctx -> {
					CONFIG = IOManager.readConfig(); 	
					ctx.getSource().getPlayer().sendSystemMessage(Text.of("§c[Message-broadcast mod]Reloaded configuration."), Util.NIL_UUID);
					LOGGER.info("[Message-broadcast mod]Reloaded configuration");
					return Command.SINGLE_SUCCESS;}
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
			int timer_min=0;
			@Override 
			public void run(){
				if(!server.isRunning()){
					return;
				}
				Calendar now = Calendar.getInstance();
				for(Message m : CONFIG.messages){
					String time_s[] = m.time.split(":");
					int m_hr=Integer.parseInt(time_s[0]);					
					int m_min=Integer.parseInt(time_s[1]);
					if (m_hr == now.get(Calendar.HOUR_OF_DAY) && m_min == now.get(Calendar.MINUTE)){
						server.getPlayerManager().getPlayerList().forEach(playerEntity -> playerEntity.sendSystemMessage(new LiteralText(m.message),Util.NIL_UUID));
					}				
				}
				
				if(timer_min>CONFIG.CycleMsgMinutes){
					timer_min = 0;
					server.getPlayerManager().getPlayerList().forEach(player -> player.sendSystemMessage(new LiteralText(CONFIG.CycleMessage), Util.NIL_UUID));
				}
				timer_min+=1;
			}
			
		};
		BroadcastExecutor.scheduleAtFixedRate(r, 0, 1, TimeUnit.MINUTES);
	}
	public void onShutdown(MinecraftServer server){
		BroadcastExecutor.shutdown();
		LOGGER.info("Message-broadcast mod shutdown");
	}
	public static int broadcast(ServerCommandSource source,Text message){
		String m = "§c[特殊公告]"+message.asString();
		Text announce = Text.of(m);
		source.getServer().getPlayerManager().getPlayerList().forEach(playerEntity->playerEntity.sendSystemMessage(announce,Util.NIL_UUID));
		return Command.SINGLE_SUCCESS;
	}
}
