package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FlyHandler {

    private boolean flyEnabled = false;

    public void toggle() {
        flyEnabled = !flyEnabled;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        mc.player.capabilities.allowFlying = flyEnabled;
        if (!flyEnabled) {
            mc.player.capabilities.isFlying = false;
        }

        mc.player.connection.sendPacket(
                new net.minecraft.network.play.client.CPacketPlayerAbilities(mc.player.capabilities)
        );
        mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                flyEnabled ? "§aLétání zapnuto" : "§cLétání vypnuto"
        ));
    }

    public boolean isEnabled() { return flyEnabled; }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        if (flyEnabled) {
            mc.player.fallDistance = 0.0f;
        }
    }
}