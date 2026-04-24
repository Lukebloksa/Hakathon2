package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class FlyHandler {

    private boolean flyEnabled = false;

    // Zmáčkni F (nebo jiný klíč) pro toggle létání
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        // Klávesa F = toggle fly (Keyboard.KEY_F)
        if (Keyboard.isKeyDown(Keyboard.KEY_F) && !mc.player.capabilities.isCreativeMode) {
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
    }
}