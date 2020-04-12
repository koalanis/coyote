package coyote;


import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class App {

    // The window handle
    private long window;

    private float[] vertices = {
            0.5f,  0.5f, 0.0f,  // top right
            0.5f, -0.5f, 0.0f,  // bottom right
            -0.5f, -0.5f, 0.0f,  // bottom left
            -0.5f,  0.5f, 0.0f   // top left
    };

    private int[] indices = {
        // note that we start from 0!
        0, 1, 3,   // first triangle
        1, 2, 3    // second triangle
    };

    private int shaderProgram;
    private int vertexShader;
    private int fragShader;

    private int vao;
    private int vbo;
    private int ebo;


    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        graphicsInit();
        shaderInit();
        loop();
        cleanup();

    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        boolean didGLFWInit = glfwInit();
        if ( !didGLFWInit )
            throw new IllegalStateException("Unable to initialize GLFW");


        // Configure GLFW

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 600, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // this must be called before any OpenGL state-machine calls are made
        GL.createCapabilities();

    }

    private void shaderInit() {
        // create shader program
        this.shaderProgram = GL32.glCreateProgram();

        System.out.println("Creating Shaders");

        // create vertex shader

        System.out.println("Creating Vertex Shaders");
        this.vertexShader = GL32.glCreateShader(GL32.GL_VERTEX_SHADER);
        GL32.glShaderSource(this.vertexShader, Shaders.simpleVertShader);
        GL32.glCompileShader(this.vertexShader);

        int s =  GL32.glGetShaderi(this.vertexShader, GL32.GL_COMPILE_STATUS);

        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Vertex Shader");
            System.out.println(GL32.glGetShaderInfoLog(this.vertexShader));
        }

        // create fragment shader
        System.out.println("Creating Fragment Shaders");

        this.fragShader = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
        GL32.glShaderSource(this.fragShader, Shaders.simpleFragShader);
        GL32.glCompileShader(this.fragShader);

        s = GL32.glGetShaderi(this.fragShader, GL32.GL_COMPILE_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Fragment Shader");
            System.out.println(GL32.glGetShaderInfoLog(this.fragShader));
        }



        GL32.glAttachShader(shaderProgram, vertexShader);
        GL32.glAttachShader(shaderProgram, fragShader);



        GL32.glLinkProgram(this.shaderProgram);
        s = GL32.glGetProgrami(shaderProgram, GL32.GL_LINK_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println(GL32.glGetProgramInfoLog(this.shaderProgram));
        }

        // after compiling shader program, free shader resources
        GL32.glDeleteShader(this.vertexShader);
        GL32.glDeleteShader(this.fragShader);
    }

    private void graphicsInit() {
        this.vao = GL33.glGenVertexArrays();
        this.vbo = GL33.glGenBuffers();
        this.ebo = GL33.glGenBuffers();


        GL33.glBindVertexArray(this.vao);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vbo);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, this.vertices, GL33.GL_STATIC_DRAW);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, this.indices, GL33.GL_STATIC_DRAW);

        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
        GL33.glEnableVertexAttribArray(0);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);

        // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        GL33.glBindVertexArray(0);

        // uncomment this call to draw in wireframe polygons.
//        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            GL33.glUseProgram(this.shaderProgram);
            GL33.glBindVertexArray(this.vao);
            GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

    }

    private void cleanup() {


        // optional: de-allocate all resources once they've outlived their purpose:
        // ------------------------------------------------------------------------
        GL33.glDeleteVertexArrays(this.vao);
        GL33.glDeleteBuffers(this.vbo);
        GL33.glDeleteBuffers(this.ebo);
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();

    }

    public static void main(String[] args) {
        new App().run();
    }

}