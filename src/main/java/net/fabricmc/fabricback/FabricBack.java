package net.fabricmc.fabricback;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import com.mojang.brigadier.Command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricBack implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("fabric-back");

	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("FabricBack loaded");
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated)->{
			dispatcher.register(CommandManager.literal("back").executes(
				ctx-> goBack(ctx.getSource())
			));
		});
	}

	public static int goBack(ServerCommandSource source){
		try{
			BackPlayerData data = (BackPlayerData)source.getPlayer();
			if(!data.hasPos()||data.getbackWorld()==null){
				source.getPlayer().sendSystemMessage(Text.of("§6你可能處於指令殺或虛空殺，回不去了，Sorry"), Util.NIL_UUID);
				return Command.SINGLE_SUCCESS;
			}
			Thread tp = new Thread(new Teleport(source.getPlayer(), data.getbackWorld(), data.getbackX(), data.getbackY(), data.getbackZ(), 0,0));
			tp.start();
		}catch(Exception exception){
			exception.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
}
