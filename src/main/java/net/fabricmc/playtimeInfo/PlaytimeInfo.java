package net.fabricmc.playtimeInfo;

import java.util.Collection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.playtimeInfo.util.TimeTransform;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class PlaytimeInfo implements DedicatedServerModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("playtimeinfo");
	public static MinecraftServer SERVER;
	@Override
	public void onInitializeServer() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("PlaytimeInfo mod loaded");
		ServerLifecycleEvents.SERVER_STARTED.register(server -> SERVER=server);
		//######### commands handle #############
		// /playtime time :       			get playtime
		// /playtime temTime :				get templaytime
		// /playtime get [targets] :		get [players] playtime and templaytime 
		// /playtime getall :				get all player's playtime and templaytime
		// /playtime reset all :			reset all player's playtime amd templaytime
		// /playtime set  playtime [target] : set player's playtime
		// /playtime set  temtime [target] :  set player's templaytime
		CommandRegistrationCallback.EVENT.register((dispatcher,dedicated) -> {
			dispatcher.register(CommandManager.literal("playtime").then(CommandManager.literal("time").executes(
				ctx -> sendplaytime(ctx.getSource())
				)
			).then(CommandManager.literal("temTime").executes(
				ctx -> sendTemplaytime(ctx.getSource())
			)).then(CommandManager.literal("get").requires(source -> source.hasPermissionLevel(4)).then(CommandManager.argument("targets", EntityArgumentType.players()).executes(
				ctx -> getPlayTime(ctx.getSource(),EntityArgumentType.getEntities(ctx, "targets"))
			))).then(CommandManager.literal("getall").executes(
				ctx -> getAllPlayTime(ctx.getSource())
			)).then(CommandManager.literal("reset").requires(source->source.hasPermissionLevel(4)).then(CommandManager.literal("all")).executes(
				ctx -> resetAll(ctx.getSource())
			)).then(CommandManager.literal("set").requires(source->source.hasPermissionLevel(4)
				).then(CommandManager.literal("playtime").then(CommandManager.argument("target", EntityArgumentType.player()).then(CommandManager.argument("minutes", LongArgumentType.longArg()).executes(
					ctx -> setPlaytime(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target"), LongArgumentType.getLong(ctx, "minutes"))
				)))
				).then(CommandManager.literal("temtime").then(CommandManager.argument("target",EntityArgumentType.player()).then(CommandManager.argument("minutes",LongArgumentType.longArg()).executes(
					ctx -> setTemtime(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "target"), LongArgumentType.getLong(ctx, "minutes"))
				))))));
		});
	}
	public static int sendplaytime(ServerCommandSource source){
		try{
			AFKPlayer afkPlayer = (AFKPlayer)source.getPlayer();
			source.getPlayer().sendSystemMessage(Text.of("§6總遊玩時數是"+TimeTransform.prettyform(afkPlayer.getPlaytime())), Util.NIL_UUID);	
			return Command.SINGLE_SUCCESS;
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
			return -1;
		}
		
	}
	public static int sendTemplaytime(ServerCommandSource source){
		try{
			AFKPlayer afkPlayer = (AFKPlayer)source.getPlayer();
			source.getPlayer().sendSystemMessage(Text.of("§6這次登入玩了"+TimeTransform.prettyform(afkPlayer.getTempPlaytime())), Util.NIL_UUID);	
			return Command.SINGLE_SUCCESS;
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
			return -1;
		}
		
	}
	private static String PlaytimeMsg="";
	public static int getPlayTime(ServerCommandSource source,Collection<? extends net.minecraft.entity.Entity> entities){
		PlaytimeMsg="§6";
		entities.forEach(e->{
			AFKPlayer afkPlayer = (AFKPlayer)e;
			PlaytimeMsg +=e.getDisplayName().getString()+" : ";
			if(afkPlayer !=null){
				PlaytimeMsg += TimeTransform.prettyform(afkPlayer.getPlaytime());
				PlaytimeMsg += "\n(Tem)"+TimeTransform.prettyform(afkPlayer.getTempPlaytime())+"\n";
			}else{
				PlaytimeMsg = "查無此玩家資料 \n";
			}
		});
		try{
			source.getPlayer().sendSystemMessage(Text.of(PlaytimeMsg), Util.NIL_UUID);
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
	public static int getAllPlayTime(ServerCommandSource source){
		PlaytimeMsg="§6";
		source.getServer().getPlayerManager().getPlayerList().forEach(e->{
			AFKPlayer afkPlayer = (AFKPlayer)e;
			PlaytimeMsg +=e.getDisplayName().getString()+" : ";
			if(afkPlayer !=null){
				PlaytimeMsg += TimeTransform.prettyform(afkPlayer.getPlaytime());
				PlaytimeMsg += "\n(Tem)"+TimeTransform.prettyform(afkPlayer.getTempPlaytime())+"\n";
			}else{
				PlaytimeMsg = "查無此玩家資料 \n";
			}
		});
		try{
			source.getPlayer().sendSystemMessage(Text.of(PlaytimeMsg), Util.NIL_UUID);
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
	public static int resetAll(ServerCommandSource source){
		source.getServer().getPlayerManager().getPlayerList().forEach(p ->{
			AFKPlayer afkPlayer = (AFKPlayer)p;
			afkPlayer.setPlaytime(0);
			afkPlayer.setTempPlaytime(0);
		});
		try{
			source.getPlayer().sendSystemMessage(Text.of("§6[PlaytimeInfo]reset all"), Util.NIL_UUID);
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
		}
		LOGGER.info("[PlaytimeInfo]reset all");
		return Command.SINGLE_SUCCESS;
	}
	public static int setPlaytime(ServerCommandSource source,ServerPlayerEntity target,long minutes){
		AFKPlayer afkPlayer = (AFKPlayer)target;
		afkPlayer.setPlaytime(minutes*60000L);
		try{
			source.getPlayer().sendSystemMessage(Text.of("§6[PlaytimeInfo]"+source.getPlayer().getDisplayName().getString()+"'s playtime is set"), Util.NIL_UUID);
			LOGGER.info("[PlaytimeInfo]"+source.getPlayer().getDisplayName().getString()+"'s playtime is set");
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
	public static int setTemtime(ServerCommandSource source,ServerPlayerEntity target,long minutes){
		AFKPlayer afkPlayer = (AFKPlayer)target;
		afkPlayer.setTempPlaytime(minutes*60000L);
		try{
			source.getPlayer().sendSystemMessage(Text.of("§6[PlaytimeInfo]"+source.getPlayer().getDisplayName().getString()+"'s Templaytime is set"), Util.NIL_UUID);
			LOGGER.info("[PlaytimeInfo]"+source.getPlayer().getDisplayName().getString()+"'s Templaytime is set");
		}catch(CommandSyntaxException exception){
			exception.printStackTrace();
		}
		return Command.SINGLE_SUCCESS;
	}
}
