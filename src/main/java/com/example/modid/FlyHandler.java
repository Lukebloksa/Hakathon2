package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class FlyHandler {

    private boolean flyEnabled = false;
    private boolean fKeyWasDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        boolean fKeyDown = Keyboard.isKeyDown(Keyboard.KEY_F);

        // Toggle pouze při prvním stisku (ne každý tick)
        if (fKeyDown && !fKeyWasDown) {
            flyEnabled = !flyEnabled;
            mc.player.capabilities.allowFlying = flyEnabled;

            if (!flyEnabled) {
                mc.player.capabilities.isFlying = false;
            }

            mc.player.connection.sendPacket(
                    new net.minecraft.network.play.client.CPacketPlayerAbilities(mc.player.capabilities)
            );

            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    flyEnabled ? "§aLétání zapnuto [F]" : "§cLétání vypnuto [F]"
            ));
        }
        if (flyEnabled || mc.player.capabilities.isFlying) {
            mc.player.fallDistance = 0.0F;
        }

        fKeyWasDown = fKeyDown;
    }
}