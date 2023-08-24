package dev.cosrnic.twitchlink.mixin;

import dev.cosrnic.twitchlink.TwitchLink;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Shadow private int ticks;

    @Inject(method = "render", at = @At("HEAD"))
    public void renderTwitchChat(DrawContext context, float tickDelta, CallbackInfo ci) {
        TwitchLink.getTwitchHud().render(context, ticks);
    }

}
