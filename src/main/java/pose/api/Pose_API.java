package pose.api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports for the REST API
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;

public class Pose_API implements ModInitializer, HttpHandler {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("pose-api");
	private static MinecraftClient client;
	double x;
	double y;
	double z;
	float yaw;
	float pitch;

	private Undertow server;

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

		// Create an Undertow server instance
        server = Undertow.builder()
                .addHttpListener(8080, "localhost")  // Set the desired port and hostname
                .setHandler(getRoutingHandler())  // Set the routing handler as the request handler
                .build();

        // Start the server
        server.start();

		}

	public void onUpdate() {
		if (client != null) {
            ClientPlayerEntity player = client.player;
            if (player != null) {
                Vec3d pos = player.getPos();
				yaw = player.getYaw() % 360.0f;
				pitch = player.getPitch();

				if (yaw < 0)
                    yaw += 360.0f;

				x = pos.x;
				y = pos.y;
				z = pos.z;

                LOGGER.info("Player position: X=" + pos.x + " Y=" + pos.y + " Z=" + pos.z + " Yaw=" + yaw + " Pitch=" + pitch);
            }
        }
	}

	private RoutingHandler getRoutingHandler() {
        // Create a routing handler to handle different API endpoints
        RoutingHandler routingHandler = new RoutingHandler();

        // Add a route for the /player endpoint
        routingHandler.get("/player", exchange -> {
            handlePlayerRequest(exchange);
        });

        return routingHandler;
    }

	private void handlePlayerRequest(HttpServerExchange exchange) {
        // Retrieve the player's position and orientation
        if (client != null) {
                String playerData = "Player position: X=" + x + " Y=" + y + " Z=" + z +
                        " Yaw=" + yaw + " Pitch=" + pitch;

                // Set the response content type and send the player data as the response
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send(playerData);
            }
        }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // This method is required by the HttpHandler interface but can be left empty
    }
}
