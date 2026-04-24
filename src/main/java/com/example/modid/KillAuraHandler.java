package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

@SideOnly(Side.CLIENT)
@Mod(modid = Tags.MOD_ID, name = "KillAura", version = Tags.VERSION)
public class KillAuraHandler {

    private boolean killAuraEnabled = false;
    private static final double RANGE = 5.0; // dosah v blocích
    private int tickCooldown = 0;

    // Klávesa K = toggle kill aura
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            killAuraEnabled = !killAuraEnabled;
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    killAuraEnabled ? "§aKill Aura zapnuta [K] (dosah: " + RANGE + " bloků)" : "§cKill Aura vypnuta [K]"
            ));
        }
    }

    // Každý tick: útočí na nejbližšího moba/zvíře v dosahu
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null || !killAuraEnabled) return;

        // Cooldown — útočíme každých 10 ticků (0.5s) aby to bylo realistické
        tickCooldown++;
        if (tickCooldown < 10) return;
        tickCooldown = 0;

        EntityPlayer player = mc.player;

        // Najdi entity v dosahu — útočí na moby a zvířata (ne hráče)
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

        if (target != null) {
            player.attackTargetEntityWithCurrentItem(target);
        }
    }
}