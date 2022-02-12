package net.fabricmc.playtimeInfo.mixin;

import net.fabricmc.playtimeInfo.AFKPlayer;
import net.fabricmc.playtimeInfo.PlaytimeInfo;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;

import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListS2CPacket.class)
abstract class PlayerListS2CPacketMixin_AFKDisplay {
    @Mixin(PlayerListS2CPacket.Entry.class)
    private abstract static class EntryMixin{
        @Shadow @Final private Text displayNmae;

        @Shadow @Final private GameProfile profile;

        @Inject(
            method = "getDisplayName",
            at = @At(
                value = "HEAD"
            ),
            cancellable = true
        )
        private void modifyDisplayName(CallbackInfoReturnable<Text> cir) {
            ServerPlayerEntity player = PlaytimeInfo.SERVER.getPlayerManager().getPlayer(this.profile.getId());
            if (player != null && ((AFKPlayer) player).isAfk()) {
                System.out.println(player.getEntityName() + " is afk!");
                cir.setReturnValue(Text.of(player.getDisplayName().copy().formatted(Formatting.GRAY)+"(AFK)"));
            }
        }
    }
}
