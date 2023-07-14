package pose.api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pose_API implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("pose-api");
	private static MinecraftClient client;
	float yaw;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Pose API initialized!");
		client = MinecraftClient.getInstance();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			onUpdate();
		});
		}

	public void onUpdate() {
		if (client != null) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                Vec3d pos = player.getPos();
				yaw = player.getYaw() % 360.0f;
				float pitch = player.getPitch();

				if (yaw < 0)
                    yaw += 360.0f;

                LOGGER.info("Player position: X=" + pos.x + " Y=" + pos.y + " Z=" + pos.z + " Yaw=" + yaw + " Pitch=" + pitch);
            }
        }
	}
}
