package com.example.modid;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class XRayHandler {

    private boolean xrayEnabled = false;
    private boolean xKeyWasDown = false;
    private final List<BlockPos> orePositions = new ArrayList<>();
    private int scanCooldown = 0;
    private static final int SCAN_RADIUS = 16;

    private boolean isOre(Block block) {
        return block == Blocks.DIAMOND_ORE
                || block == Blocks.EMERALD_ORE
                || block == Blocks.GOLD_ORE
                || block == Blocks.IRON_ORE
                || block == Blocks.COAL_ORE
                || block == Blocks.REDSTONE_ORE
                || block == Blocks.LIT_REDSTONE_ORE
                || block == Blocks.LAPIS_ORE
                || block == Blocks.QUARTZ_ORE;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        boolean xKeyDown = Keyboard.isKeyDown(Keyboard.KEY_X);
        if (xKeyDown && !xKeyWasDown) {
            xrayEnabled = !xrayEnabled;
            orePositions.clear();
            mc.player.sendMessage(new net.minecraft.util.text.TextComponentString(
                    xrayEnabled ? "§aX-Ray zapnut [X]" : "§cX-Ray vypnut [X]"
            ));
        }
        xKeyWasDown = xKeyDown;

        if (!xrayEnabled) return;

        scanCooldown++;
        if (scanCooldown < 40) return;
        scanCooldown = 0;

        orePositions.clear();
        World world = mc.world;
        BlockPos playerPos = mc.player.getPosition();

        for (int x = -SCAN_RADIUS; x <= SCAN_RADIUS; x++) {
            for (int y = -SCAN_RADIUS; y <= SCAN_RADIUS; y++) {
                for (int z = -SCAN_RADIUS; z <= SCAN_RADIUS; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (isOre(block)) {
                        orePositions.add(pos);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!xrayEnabled || orePositions.isEmpty()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) return;

        double px = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks();
        double py = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks();
        double pz = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-px, -py, -pz);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.8f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (BlockPos pos : new ArrayList<>(orePositions)) {
            IBlockState state = mc.world.getBlockState(pos);

            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = mc.getBlockRendererDispatcher()
                    .getBlockModelShapes()
                    .getTexture(state);

            if (sprite == null) continue;

            float u1 = sprite.getMinU(), u2 = sprite.getMaxU();
            float v1 = sprite.getMinV(), v2 = sprite.getMaxV();

            double x = pos.getX(), y = pos.getY(), z = pos.getZ();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            // Spodek
            buffer.pos(x,   y,   z  ).tex(u1, v2).endVertex();
            buffer.pos(x+1, y,   z  ).tex(u2, v2).endVertex();
            buffer.pos(x+1, y,   z+1).tex(u2, v1).endVertex();
            buffer.pos(x,   y,   z+1).tex(u1, v1).endVertex();
            // Vršek
            buffer.pos(x,   y+1, z+1).tex(u1, v2).endVertex();
            buffer.pos(x+1, y+1, z+1).tex(u2, v2).endVertex();
            buffer.pos(x+1, y+1, z  ).tex(u2, v1).endVertex();
            buffer.pos(x,   y+1, z  ).tex(u1, v1).endVertex();
            // Přední
            buffer.pos(x,   y,   z  ).tex(u1, v2).endVertex();
            buffer.pos(x,   y+1, z  ).tex(u1, v1).endVertex();
            buffer.pos(x+1, y+1, z  ).tex(u2, v1).endVertex();
            buffer.pos(x+1, y,   z  ).tex(u2, v2).endVertex();
            // Zadní
            buffer.pos(x+1, y,   z+1).tex(u1, v2).endVertex();
            buffer.pos(x+1, y+1, z+1).tex(u1, v1).endVertex();
            buffer.pos(x,   y+1, z+1).tex(u2, v1).endVertex();
            buffer.pos(x,   y,   z+1).tex(u2, v2).endVertex();
            // Levá
            buffer.pos(x,   y,   z+1).tex(u1, v2).endVertex();
            buffer.pos(x,   y+1, z+1).tex(u1, v1).endVertex();
            buffer.pos(x,   y+1, z  ).tex(u2, v1).endVertex();
            buffer.pos(x,   y,   z  ).tex(u2, v2).endVertex();
            // Pravá
            buffer.pos(x+1, y,   z  ).tex(u1, v2).endVertex();
            buffer.pos(x+1, y+1, z  ).tex(u1, v1).endVertex();
            buffer.pos(x+1, y+1, z+1).tex(u2, v1).endVertex();
            buffer.pos(x+1, y,   z+1).tex(u2, v2).endVertex();

            tessellator.draw();
        }

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}