package fi.dy.masa.minihud;

import fi.dy.masa.minihud.renderer.OverlayRendererJukeboxRange;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.block.Block;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.minihud.config.Configs;

import java.util.ArrayList;
import java.util.List;

public class MiniHUD implements ModInitializer
{
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_ID);

    public static final List<BlockPos> jukeboxes = new ArrayList<>();

    @Override
    public void onInitialize()
    {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());

        ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof JukeboxBlockEntity e) {
                jukeboxes.add(e.getPos());
            }
        });

        ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof JukeboxBlockEntity e) {
                jukeboxes.remove(e.getPos());
                OverlayRendererJukeboxRange.INSTANCE.onBlockStatusChange(e.getPos());
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            for (BlockPos pos : jukeboxes) {
                OverlayRendererJukeboxRange.INSTANCE.onBlockStatusChange(pos);
            }
        }));
    }

    public static void debugLog(String key, Object... args)
    {
        if (Configs.Generic.DEBUG_MESSAGES.getBooleanValue())
        {
            LOGGER.info(key, args);
        }
    }
}
