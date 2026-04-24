package com.example.modid;

import com.example.modid.Tags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class ExampleMod {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

        MinecraftForge.EVENT_BUS.register(new KillAuraHandler());
        MinecraftForge.EVENT_BUS.register(new FlyHandler());
        MinecraftForge.EVENT_BUS.register(new XRayHandler());
        MinecraftForge.EVENT_BUS.register(new GodModeHandler());
        MinecraftForge.EVENT_BUS.register(new HitboxHandler());
        MinecraftForge.EVENT_BUS.register(new AimbotHandler());

        LOGGER.info("KillAura a FlyHandler uspesne registrovany!");
    }

}
