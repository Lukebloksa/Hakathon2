package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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

            if (!godEnabled) {
                mc.player.noClip = false;
            }

            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    godEnabled ? "§aGod Mode zapnut [G]" : "§cGod Mode vypnut [G]"
            ));
        }
        gKeyWasDown = gKeyDown;

        if (godEnabled) {
            EntityPlayer p = mc.player;
            // Každý tick obnov životy a hladinu
            p.setHealth(20.0f);
            p.getFoodStats().setFoodLevel(20);
            p.extinguish();
            p.fallDistance = 0.0f;
            p.hurtResistantTime = 80; // maximální hurt cooldown = nelze dostat damage
            p.noClip = true;
        }
    }

    // Zablokuj útok dřív než se zpracuje
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onAttack(LivingAttackEvent event) {
        if (!godEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (event.getEntity() == mc.player) {
            event.setCanceled(true);
        }
    }

    // Záloha — zablokuj i damage event
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onDamage(LivingDamageEvent event) {
        if (!godEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;
        if (event.getEntity() == mc.player) {
            event.setCanceled(true);
        }
    }
}