package com.example.modid;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;

public class AimbotHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean enabled = false;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        // Kontrola, zda byla stisknuta naše klávesa
        if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
            enabled = !enabled;
            // Poslat zprávu do chatu (jen pro tebe), abys věděl, jestli je to ON nebo OFF
            String status = enabled ? "§aZAPNUTO" : "§cVypnuto";
            mc.player.sendStatusMessage(new TextComponentString("Aimbot: " + status), true);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!enabled || event.phase != TickEvent.Phase.END || mc.player == null || mc.world == null) return;

        // Kontrola luku
        if (mc.player.isHandActive() && mc.player.getActiveItemStack().getItem() instanceof ItemBow) {
            EntityLivingBase target = getClosestTarget();

            if (target != null) {
                faceEntityWithPrediction(target);
            }
        }
    }

    private void faceEntityWithPrediction(EntityLivingBase entity) {
        double diffX = entity.posX - mc.player.posX;
        double diffZ = entity.posZ - mc.player.posZ;

        double distance = mc.player.getDistance(entity);

        // --- JEDNODUCHÁ BALISTIKA ---
        // Čím dál je cíl, tím víc míříme nad něj (přičítáme k Y souřadnici)
        // Koeficient 0.007 je odhad pro plně natažený luk
        double bulletDrop = (distance * distance) * 0.007;
        double diffY = (entity.posY + entity.getEyeHeight()) - (mc.player.posY + mc.player.getEyeHeight()) + bulletDrop;

        double distXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (MathHelper.atan2(diffZ, diffX) * (180D / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(diffY, distXZ) * (180D / Math.PI)));

        // Okamžitý lock
        mc.player.rotationYaw = yaw;
        mc.player.rotationPitch = pitch;
    }

    private EntityLivingBase getClosestTarget() {
        return mc.world.loadedEntityList.stream()
                .filter(e -> e instanceof EntityLivingBase)
                .map(e -> (EntityLivingBase) e)
                .filter(e -> e != mc.player && e.isEntityAlive() && mc.player.canEntityBeSeen(e))
                .min(Comparator.comparingDouble(e -> mc.player.getDistanceSq(e)))
                .orElse(null);
    }
}