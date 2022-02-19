package net.fabricmc.serverrules;

// import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class ServerRules implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("serverrules-mod");
	public static ConfigInstance CONFIG;
	public static final ScheduledExecutorService BroadcastExecutor = Executors.newSingleThreadScheduledExecutor();
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("serverrules mod loaded");
		ServerLifecycleEvents.SERVER_STARTING.register(this::init);
		/*  --------                  Init commands       ----------               */
		// /serverrule : send server rule message
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("serverrule").executes(
				ctx-> {
					ctx.getSource().getPlayer().sendSystemMessage(Text.of(CONFIG.RulesMessage), Util.NIL_UUID);
					return Command.SINGLE_SUCCESS;
				}
			));
		});
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("serverruleReload").executes(
				ctx-> reloadConfig(ctx.getSource())
			));
		});
	}
	public void init(MinecraftServer server){
		IOManager.genConfig();
		CONFIG = IOManager.readConfig();
	}
	public static int reloadConfig(ServerCommandSource source){
		IOManager.genConfig();
		CONFIG=IOManager.readConfig();
		try{
			source.getPlayer().sendSystemMessage(Text.of("§4 server rules reloaded"), Util.NIL_UUID);
		}catch(CommandSyntaxException exception){exception.printStackTrace();}
		LOGGER.info("reload server rules");
		return Command.SINGLE_SUCCESS;
	}
}
