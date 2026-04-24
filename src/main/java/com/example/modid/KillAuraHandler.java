package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

@SideOnly(Side.CLIENT)
public class KillAuraHandler {

    private boolean killAuraEnabled = false;
    private boolean kKeyWasDown = false;
    private static final double RANGE = 10.0;
    private int tickCooldown = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        // Toggle K klávesy
        boolean kKeyDown = Keyboard.isKeyDown(Keyboard.KEY_K);
        if (kKeyDown && !kKeyWasDown) {
            killAuraEnabled = !killAuraEnabled;
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    killAuraEnabled ? "§aKill Aura zapnuta [K]" : "§cKill Aura vypnuta [K]"
            ));
        }
        kKeyWasDown = kKeyDown;

        if (!killAuraEnabled) return;

        // Cooldown
        tickCooldown++;
        if (tickCooldown < 10) return;
        tickCooldown = 0;

        EntityPlayer player = mc.player;

        List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(
                player,
                player.getEntityBoundingBox().grow(RANGE)
        );

        Entity target = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (!(entity instanceof EntityMob) && !(entity instanceof EntityAnimal)) continue;
            double dist = player.getDistanceSq(entity);
            if (dist < closestDist) {
                closestDist = dist;
                target = entity;
            }
        }

        // Opravený útok přes playerController
        if (target != null) {
            mc.playerController.attackEntity(player, target);
            player.swingArm(net.minecraft.util.EnumHand.MAIN_HAND);
        }
    }
}