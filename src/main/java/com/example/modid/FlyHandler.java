package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class FlyHandler {

    private boolean flyEnabled = false;
    private boolean fKeyWasDown = false;

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

        EntityPlayer p = mc.player;
        p.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        p.setHealth(20.0f);
        p.fallDistance = 0.0f;
        p.hurtResistantTime = 80;
        p.noClip = true;
    }

    public boolean isEnabled() { return flyEnabled; }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        boolean fKeyDown = Keyboard.isKeyDown(Keyboard.KEY_F);
        if (fKeyDown && !fKeyWasDown) {
            toggle();
        }
        fKeyWasDown = fKeyDown;

        // No fall vždy — bez ohledu na létání
        mc.player.fallDistance = 0.0f;
    }
}