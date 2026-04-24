package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class HitboxHandler {

    private boolean hitboxEnabled = false;
    private boolean hKeyWasDown = false;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        boolean hKeyDown = Keyboard.isKeyDown(Keyboard.KEY_H);
        if (hKeyDown && !hKeyWasDown) {
            hitboxEnabled = !hitboxEnabled;
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    hitboxEnabled ? "§aHitboxy zapnuty [H] §c■§a enemy §2■§a friendly" : "§cHitboxy vypnuty [H]"
            ));
        }
        hKeyWasDown = hKeyDown;
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!hitboxEnabled) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        double px = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks();
        double py = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks();
        double pz = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-px, -py, -pz);
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.glLineWidth(2.0f);

        List<Entity> entities = mc.world.loadedEntityList;
        for (Entity entity : entities) {
            if (entity == mc.player) continue;

            float r, g, b;
            if (entity instanceof EntityMob) {
                // Enemy = červená
                r = 1.0f; g = 0.0f; b = 0.0f;
            } else if (entity instanceof EntityAnimal) {
                // Friendly = zelená
                r = 0.0f; g = 0.8f; b = 0.0f;
            } else if (entity instanceof EntityPlayer) {
                // Hráči = žlutá
                r = 1.0f; g = 1.0f; b = 0.0f;
            } else {
                continue;
            }

            GlStateManager.color(r, g, b, 0.7f);

            net.minecraft.util.math.AxisAlignedBB bb = entity.getEntityBoundingBox();
            drawAABB(bb);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void drawAABB(net.minecraft.util.math.AxisAlignedBB bb) {
        double x1 = bb.minX, y1 = bb.minY, z1 = bb.minZ;
        double x2 = bb.maxX, y2 = bb.maxY, z2 = bb.maxZ;

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x1, y1, z1); GL11.glVertex3d(x2, y1, z1);
        GL11.glVertex3d(x2, y1, z2); GL11.glVertex3d(x1, y1, z2);
        GL11.glVertex3d(x1, y1, z1); GL11.glVertex3d(x1, y2, z1);
        GL11.glVertex3d(x2, y2, z1); GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y2, z2); GL11.glVertex3d(x1, y2, z1);
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(x2, y1, z1); GL11.glVertex3d(x2, y2, z1);
        GL11.glVertex3d(x2, y1, z2); GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y1, z2); GL11.glVertex3d(x1, y2, z2);
        GL11.glEnd();
    }
}