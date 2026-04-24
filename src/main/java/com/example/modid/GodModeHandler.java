package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

public class GodModeHandler {

    private boolean godEnabled = false;
    private boolean gKeyWasDown = false;

    public void toggle() {
        godEnabled = !godEnabled;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        if (!godEnabled) {
            mc.player.noClip = false;
            // Obnov normální max životy
            mc.player.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        }

        mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                godEnabled ? "§aGod Mode zapnut" : "§cGod Mode vypnut"
        ));
    }

    public boolean isEnabled() { return godEnabled; }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        boolean gKeyDown = Keyboard.isKeyDown(Keyboard.KEY_G);
        if (gKeyDown && !gKeyWasDown) {
            toggle();
        }
        gKeyWasDown = gKeyDown;

        if (!godEnabled) return;

        EntityPlayer p = mc.player;
        // Normální max životy (20 = 10 srdíček)
        p.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        p.setHealth(20.0f);
        p.getFoodStats().setFoodLevel(20);
        p.extinguish();
        p.fallDistance = 0.0f;
        p.hurtResistantTime = 80;
        p.noClip = true;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onAttack(LivingAttackEvent event) {
        if (!godEnabled) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && event.getEntity().getUniqueID().equals(mc.player.getUniqueID())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onDamage(LivingDamageEvent event) {
        if (!godEnabled) return;
        if (!(event.getEntity() instanceof EntityPlayer)) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && event.getEntity().getUniqueID().equals(mc.player.getUniqueID())) {
            event.setCanceled(true);
        }
    }
}