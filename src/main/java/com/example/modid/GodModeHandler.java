package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GodModeHandler {

    private boolean godEnabled = false;
    private boolean gKeyWasDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        boolean gKeyDown = Keyboard.isKeyDown(Keyboard.KEY_G);
        if (gKeyDown && !gKeyWasDown) {
            godEnabled = !godEnabled;

            // Při vypnutí obnov kolize
            if (!godEnabled) {
                mc.player.noClip = false;
            }

            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    godEnabled ? "§aGod Mode zapnut [G] (průchod zdmi + nesmrtelnost)" : "§cGod Mode vypnut [G]"
            ));
        }
        gKeyWasDown = gKeyDown;

        if (godEnabled) {
            mc.player.setHealth(20.0f);
            mc.player.getFoodStats().setFoodLevel(20);
            mc.player.extinguish();
            mc.player.fallDistance = 0.0f;
            // Průchod přes zdi — ale hráč stále chodí normálně po zemi
            mc.player.noClip = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onDamage(LivingDamageEvent event) {
        if (!godEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (event.getEntity() == mc.player) {
            event.setCanceled(true);
        }
    }
}