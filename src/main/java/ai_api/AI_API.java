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

// libraries to generate JSONs
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import ai_api.InvJson;

import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;

import javax.imageio.ImageIO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Imports for the REST API
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


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
    private static BufferedImage bufferedImage;
    private static long window;

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

        int width = 720;
        int height = 640;
        initGL(width, height);

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            getPos();
            getInv();
            InputHandler();
            captureScreenshot();
        });

        // Create an Undertow server instance
        server = Undertow.builder()
                .addHttpListener(8070, "localhost") // Set the desired port and hostname
                .setHandler(getRoutingHandler()) // Set the routing handler as the request handler
                .build();

        // Start the server
        server.start();

    }

    private static void initGL(int width, int height) {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        window = GLFW.glfwCreateWindow(width, height, "Minecraft Screenshot", 0, 0);
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
    }

    // -----------------------------------------------------------------------------------
    // //
    // ------------------------ FUNCTIONS TO GET INFO FROM GAME
    // -------------------------- //
    // -----------------------------------------------------------------------------------
    // //

    private void getPos() {
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

                // LOGGER.info("Player position: X=" + pos.x + " Y=" + pos.y + " Z=" + pos.z + "
                // Yaw=" + yaw + " Pitch=" + pitch);
            }
        }
    }

    private void InputHandler() {
        // Register a client tick event to handle input on every game tick
        // Handle your mouse input here
        // if (client.mouse.wasLeftButtonClicked()) {
        if (client.mouse.wasLeftButtonClicked()) {
            // Left mouse button was clicked
            // LOGGER.info("Left mouse button clicked!");
            left_mouse = true;
        } else {
            left_mouse = false;
        }

        // if (client.mouse.wasRightButtonClicked()) {
        if (client.mouse.wasRightButtonClicked()) {
            // Left mouse button was clicked
            // LOGGER.info("Right mouse button clicked!");
            right_mouse = true;
        } else {
            right_mouse = false;
        }

        if (client.options.jumpKey.isPressed()) {
            // LOGGER.info("Bro is jumping!");
            jump = true;
        } else {
            jump = false;
        }

        if (client.options.sprintKey.isPressed()) {
            // LOGGER.info("My man is sprinting!");
            sprint = true;
        } else {
            sprint = false;
        }

        if (client.options.forwardKey.isPressed()) {
            // LOGGER.info("Moving forward!");
            up = true;
        } else {
            up = false;
        }

        if (client.options.backKey.isPressed()) {
            // LOGGER.info("Moving backwards!");
            down = true;
        } else {
            down = false;
        }

        if (client.options.leftKey.isPressed()) {
            // LOGGER.info("Moving left!");
            left = true;
        } else {
            left = false;
        }

        if (client.options.rightKey.isPressed()) {
            // LOGGER.info("Moving right!");
            right = true;
        } else {
            right = false;
        }

        if (client.options.sneakKey.isPressed()) {
            // LOGGER.info("Crouching!");
            crouch = true;
        } else {
            crouch = false;
        }
        mouse_x = client.mouse.getX();
        mouse_y = client.mouse.getY();
        // LOGGER.info("Mouse X: " + mouse_x + ", Mouse Y: " + mouse_y);

    }

    private void getInv() {
        if (client != null) {
            PlayerEntity player = client.player;
            if (player != null) {
                DefaultedList<ItemStack> playerInventory = player.getInventory().main;
                // LOGGER.info("Player Inventory:");
                itemTypesList.clear();
                itemCount.clear();

                for (int i = 0; i < playerInventory.size(); i++) {
                    ItemStack itemStack = playerInventory.get(i);
                    // LOGGER.info("Slot " + i + ": " + itemStack.getItem().getTranslationKey() + "
                    // (Count: " + itemStack.getCount() + ")");
                    Item item = itemStack.getItem();
                    int amount = player.getInventory().count(item);
                    LOGGER.info("Amount" + amount);
                    itemTypesList.add(item);
                    itemCount.add(amount);
                }

            }
        }
    }

    private void captureScreenshot() {
        // Get the current framebuffer (the rendered image on the screen)
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();

        GL11.glReadBuffer(GL11.GL_FRONT);
        IntBuffer buffer = BufferUtils.createIntBuffer(width * height);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[width * height];
        buffer.get(pixels);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                image.setRGB(x, height - y - 1, pixel);
            }
        }

        bufferedImage = image;
    }

    private static byte[] convertToPNG(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // -----------------------------------------------------------------------------------
    // //
    // --------------------- FUNCTION THAT HANDLES THE API REQUESTS
    // ---------------------- //
    // -----------------------------------------------------------------------------------
    // //

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

        routingHandler.get("/image", exchange -> {
            handleImageRequest(exchange);
        });

        return routingHandler;
    }

    // -----------------------------------------------------------------------------------
    // //
    // -------------------- FUNCTIONS THAT PRINT OUT INFO TO THE API
    // --------------------- //
    // -----------------------------------------------------------------------------------
    // //

    private void handlePlayerRequest(HttpServerExchange exchange) {
        // Retrieve the player's position and orientation
        if (client != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode playerData = objectMapper.createObjectNode();

            // Populate the JSON object with player data
            playerData.put("x", x);
            playerData.put("y", y);
            playerData.put("z", z);
            playerData.put("yaw", yaw);
            playerData.put("pitch", pitch);

            try {
                // Convert JSON object to JSON string
                String jsonData = objectMapper.writeValueAsString(playerData);
    
                // Set the response content type to JSON and send the JSON data as the response
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception if JSON conversion fails
            }
        }
    }

    private void handlePlayerInvRequest(HttpServerExchange exchange) {
        // Retrieve the player's position and orientation
        if (client != null) {

            ObjectMapper mapper = new ObjectMapper();

            Map<String, InvJson> slotsMap = new LinkedHashMap<>();

            for(int i = 0; i < itemTypesList.size(); i++) {
                slotsMap.put("Slot " + i, new InvJson(itemCount.get(i), itemTypesList.get(i).toString()));
            }

            String jsonString;
            try {
                jsonString = mapper.writeValueAsString(slotsMap);
            } catch (Exception e) {
                e.printStackTrace(); // Handle the exception as needed
                return;
            }

            // Convert my JSON to a byte buffer, in order for it to be properly sent through the api
            byte[] jsonDataBytes = jsonString.getBytes(StandardCharsets.UTF_8);
            ByteBuffer byteBuffer = ByteBuffer.wrap(jsonDataBytes);

            // Send the encoded JSON
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(byteBuffer);

        }
    }
 

    
    private void handlePlayerInputRequest(HttpServerExchange exchange) {
        
        // Retrieve the player's position and orientation
        if (client != null) {
          
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode player_actions = objectMapper.createObjectNode();

            player_actions.put("Right_click" , right_mouse);
            player_actions.put("Left_click" , left_mouse);
            player_actions.put("Jump_key" , jump);
            player_actions.put("Crouch_key" , crouch);
            player_actions.put("Sprint_key" , sprint);
            player_actions.put("W_key" , up);
            player_actions.put("S_key" , down);
            player_actions.put("A_key" , left);
            player_actions.put("D_key" , right);
            player_actions.put("Mouse_X_position" , mouse_x);
            player_actions.put("Mouse_Y_position" , mouse_y);

            // Set the response content type and send the player data as the response
            try {
                // Convert JSON object to JSON string
                String jsonData = objectMapper.writeValueAsString(player_actions);
    
                // Set the response content type to JSON and send the JSON data as the response
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(jsonData);
            } catch (Exception e) {
                e.printStackTrace();
                // Handle exception if JSON conversion fails
            }
        }

    }

    private static void handleImageRequest(HttpServerExchange exchange) {

        // Convert the BufferedImage to a byte array (PNG format)
        byte[] imageData = convertToPNG(bufferedImage);

        // Set the response content type to "image/png"
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "image/png");

        // Send the image data in the response
        ByteBuffer byteBuffer = ByteBuffer.wrap(imageData);
        exchange.getResponseSender().send(byteBuffer);
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // This method is required by the HttpHandler interface but can be left empty
    }
}