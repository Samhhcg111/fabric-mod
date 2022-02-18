package net.fabricmc.playtimeInfo.mixin;

import net.fabricmc.playtimeInfo.AFKPlayer;
import net.fabricmc.playtimeInfo.PlaytimeInfo;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListS2CPacket.Entry.class)
abstract class PlayerListS2CPacketMixin_AFKDisplay {
        @Inject(
            method = "getDisplayName",
            at = @At(
                value = "HEAD"
            )
            ,cancellable = true
        )
        private void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
            ServerPlayerEntity player = PlaytimeInfo.SERVER.getPlayerManager().getPlayer(((PlayerListS2CPacket.Entry)(Object)this).getProfile().getId());
            if (player != null && ((AFKPlayer) player).isAfk()) {
                System.out.println(player.getEntityName()+" is afk");
                String afkName = player.getDisplayName().copy().getString();
                cir.setReturnValue(Text.of("§7"+afkName +"(AFK)"));
            }
        }
}
