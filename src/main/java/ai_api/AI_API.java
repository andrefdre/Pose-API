package ai_api;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.List;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports for the REST API
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;


public class AI_API implements ModInitializer, HttpHandler {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("pose-api");
	private static MinecraftClient client;
    
    // variables for the player_actions functions
    public static boolean left; 
    public static boolean right;
    public static boolean up;
    public static boolean down;
    public static boolean jump;
    public static boolean crouch;
    public static boolean sprint;
    public static boolean left_mouse;
    public static boolean right_mouse;
    public static double mouse_x;
    public static double mouse_y;

    // variables for the player_position functions
	double x;
	double y;
	double z;
	float yaw;
	float pitch;

    // variables for the player_inventory functions
    List<Item> itemTypesList = new ArrayList<>();
	List<Integer> itemCount = new ArrayList<>();

	private Undertow server;




	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Pose API initialized!");
		client = MinecraftClient.getInstance();

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			getPos();
            getInv();
            InputHandler();
            captureScreenshot();
		});

		// Create an Undertow server instance
        server = Undertow.builder()
                .addHttpListener(8070, "localhost")  // Set the desired port and hostname
                .setHandler(getRoutingHandler())  // Set the routing handler as the request handler
                .build();

        // Start the server
        server.start();

    }

    // ----------------------------------------------------------------------------------- //
    // ------------------------ FUNCTIONS TO GET INFO FROM GAME -------------------------- //
    // ----------------------------------------------------------------------------------- //

	public void getPos() {
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

                // LOGGER.info("Player position: X=" + pos.x + " Y=" + pos.y + " Z=" + pos.z + " Yaw=" + yaw + " Pitch=" + pitch);
            }
        }
	}

    public void InputHandler() {
        // Register a client tick event to handle input on every game tick
            // Handle your mouse input here
            // if (client.mouse.wasLeftButtonClicked()) {
            if (client.mouse.wasLeftButtonClicked()) {
                // Left mouse button was clicked
                // LOGGER.info("Left mouse button clicked!");
                left_mouse = true;
            }
            else {
                left_mouse = false;
            }

            // if (client.mouse.wasRightButtonClicked()) {
            if (client.mouse.wasRightButtonClicked()) {
                // Left mouse button was clicked
                // LOGGER.info("Right mouse button clicked!");
                right_mouse = true;
            }
            else {
                right_mouse = false;
            }

            if (client.options.jumpKey.isPressed()) {
                // LOGGER.info("Bro is jumping!");
                jump = true;
            }
            else {
                jump = false;
            }

            if (client.options.sprintKey.isPressed()) {
                // LOGGER.info("My man is sprinting!");
                sprint = true;
            }
            else {
                sprint = false;
            }

            if (client.options.forwardKey.isPressed()) {
                // LOGGER.info("Moving forward!");
                up = true;
            }
            else {
                up = false;
            }

            if (client.options.backKey.isPressed()) {
                // LOGGER.info("Moving backwards!");
                down = true;
            }
            else {
                down = false;
            }

            if (client.options.leftKey.isPressed()) {
                // LOGGER.info("Moving left!");
                left = true;
            }    
            else {
                left = false;
            }           

            if (client.options.rightKey.isPressed()) {
                // LOGGER.info("Moving right!");
                right = true;
            }
            else {
                right = false;
            }

            if (client.options.sneakKey.isPressed()) {
                // LOGGER.info("Crouching!");
                crouch = true;
            }   
            else {
                crouch = false;
            }      
            mouse_x = client.mouse.getX();
            mouse_y = client.mouse.getY();
            // LOGGER.info("Mouse X: " + mouse_x + ", Mouse Y: " + mouse_y);
            
    }

    public void getInv() {
		if (client != null) {
            PlayerEntity player = client.player;
            if (player != null) {
				DefaultedList<ItemStack> playerInventory = player.getInventory().main;
				// LOGGER.info("Player Inventory:");
                itemTypesList.clear();


				for (int i = 0; i < playerInventory.size(); i++) {
					ItemStack itemStack = playerInventory.get(i);
					// LOGGER.info("Slot " + i + ": " + itemStack.getItem().getTranslationKey() + " (Count: " + itemStack.getCount() + ")");
					Item item = itemStack.getItem();
                    int amount = player.getInventory().count(item);
					itemTypesList.add(item);
                    itemCount.add(amount);
				}
				
			}
	    }
    }
    
    public void captureScreenshot() {
        // Get the current framebuffer (the rendered image on the screen)
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();
        // int[] pixelData = new int[width * height];
        // client.getFramebuffer().readPixels(0, 0, width, height, true);

        // Extract pixel data from the framebuffer into the pixelData array
        // Note: This will store the data in the ARGB format

        // At this point, you have the pixel data in the pixelData array
        // You can convert this data into a BufferedImage or manipulate it as needed.
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // bufferedImage.setRGB(0, 0, width, height, pixelData, 0, width);
        int[] pixelData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
        // Now, you can further process the BufferedImage as needed.
        // For example, you can manipulate the pixel data, apply filters, etc.

        // After processing, you can access the pixel data as follows:
        // int[] processedPixelData = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();        
        LOGGER.info(pixelData.toString());

    }


        // Your further processing logic goes here.
        // ...

        // At this point, processedPixelData contains the bitmap data you need.

        // You can also save the BufferedImage as a PNG or other formats if required:
        // File outputFile = new File("output.png");
        // ImageIO.write(bufferedImage, "PNG", outputFile);
    

    // ----------------------------------------------------------------------------------- //
    // --------------------- FUNCTION THAT HANDLES THE API REQUESTS ---------------------- //
    // ----------------------------------------------------------------------------------- //

	private RoutingHandler getRoutingHandler() {
        // Create a routing handler to handle different API endpoints
        RoutingHandler routingHandler = new RoutingHandler();

        // Add a route for the /player endpoint
        routingHandler.get("/player_positions", exchange -> {
            handlePlayerRequest(exchange);
        });

        routingHandler.get("/player_inv", exchange -> {
            handlePlayerInvRequest(exchange);
        });
        routingHandler.get("/player_actions", exchange -> {
            handlePlayerInputRequest(exchange);
        });

        return routingHandler;
    }


    // ----------------------------------------------------------------------------------- //
    // -------------------- FUNCTIONS THAT PRINT OUT INFO TO THE API --------------------- //
    // ----------------------------------------------------------------------------------- //

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

    private void handlePlayerInvRequest(HttpServerExchange exchange) {
        // Retrieve the player's position and orientation
        if (client != null) {
             
			StringBuilder player_inventory = new StringBuilder();
            player_inventory.append("Hotbar inventory: \n");
            for (int i = 0; i < 10; i++) {					
                player_inventory.append("Slot " + i + ": " + itemTypesList.get(i) + ", amount: " + itemCount.get(i) + "\n");
            }
            player_inventory.append("\nRemaining inventory: \n");
            for (int i = 10; i < itemTypesList.size(); i++) {					
                player_inventory.append("Slot " + i + ": " + itemTypesList.get(i) + ", amount: " + itemCount.get(i) + "\n");
            }
            // Set the response content type and send the player data as the response
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(player_inventory.toString());
            exchange.getResponseSender().send(byteBuffer);
        }
			
    }   

    private void handlePlayerInputRequest(HttpServerExchange exchange) {
        // Retrieve the player's position and orientation
        if (client != null) {
                
            StringBuilder player_actions = new StringBuilder();
            
            player_actions.append("Right click status: " + right_mouse + "\n");
            player_actions.append("Left click status " + left_mouse + "\n");
            player_actions.append("Jump key status: " + jump + "\n");
            player_actions.append("Crouch key status: " + crouch+ "\n");
            player_actions.append("Sprint key status: "+ sprint + "\n");
            player_actions.append("W key status: " + up + "\n");
            player_actions.append("S key status: " + down + "\n");
            player_actions.append("A key status: " + left + "\n");
            player_actions.append("D key status: " + right + "\n");
            player_actions.append("\n");
            player_actions.append("Mouse X position: " + mouse_x + "\n");
            player_actions.append("Mouse Y position: " + mouse_y + "\n");
                        
            // Set the response content type and send the player data as the response
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(player_actions.toString());
            exchange.getResponseSender().send(byteBuffer);
        }
			
    }    


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // This method is required by the HttpHandler interface but can be left empty
    }
}