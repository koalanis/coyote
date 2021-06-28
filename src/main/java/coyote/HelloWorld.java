package coyote;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {

    // The window handle
    private long window;

    final int BYTES_PER_FLOAT = 4;

    final int VERTEX_POS_SIZE   = 3; // x, y
    final int VERTEX_COLOR_SIZE = 4; // r, g, b, and a
    final int VERTEX_UV_SIZE = 2; // u,v

    final int VERTEX_POS_INDX   = 0;
    final int VERTEX_COLOR_INDX = 1;
    final int VERTEX_UV_INDX = 2;

    final int VERTEX_STRIDE =  ( BYTES_PER_FLOAT * VERTEX_POS_SIZE );

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();
        destroy();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
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
        }
        // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        int vertShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShaderHandle, Shaders.tinyVertShader);
        glCompileShader(vertShaderHandle);
        checkIfShaderCompiledSuccessfully(vertShaderHandle);

        int fragShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShaderHandle, Shaders.tinyFragShader);
        glCompileShader(fragShaderHandle);
        checkIfShaderCompiledSuccessfully(fragShaderHandle);

        int shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShaderHandle);
        glAttachShader(shaderProgram, fragShaderHandle);
        glLinkProgram(shaderProgram);

        checkIfProgramLinkedSuccessfully(shaderProgram);
        glUseProgram(shaderProgram);
        glDeleteShader(vertShaderHandle);
        glDeleteShader(fragShaderHandle);

        float[] vertices = {
                0.5f,  0.5f, 0.0f,  // top right
                0.5f, -0.5f, 0.0f,  // bottom right
                -0.5f, -0.5f, 0.0f,  // bottom left
                -0.5f,  0.5f, 0.0f   // top left
        };

        int[] indices = {
                0, 1, 3,  // first Triangle
                1, 2, 3   // second Triangle
        };

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();

        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, VERTEX_POS_SIZE, GL_FLOAT,false, VERTEX_STRIDE, 0);
        glEnableVertexAttribArray(0);

        // note that this is allowed, the call to glVertexAttribPointer registered VBO as the vertex attribute's bound vertex buffer object so afterwards we can safely unbind
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
        //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
        // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
        glBindVertexArray(0);

        while ( !glfwWindowShouldClose(window) ) {

            // render
            // ------
            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            glUseProgram(shaderProgram);
            glBindVertexArray(vao);
            //glDrawArrays(GL_TRIANGLES, 0, 6);
            glDrawElements(GL_TRIANGLES, indices.length,GL_UNSIGNED_INT, 0);
            glBindVertexArray(0); // no need to unbind it every time


            glfwSwapBuffers(window); // swap the color buffers
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private void checkIfProgramLinkedSuccessfully(int shaderProgram) {
        if(glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_TRUE) {
            System.out.println("program compiled successfully");
        } else {
            System.out.printf("glGetProgramInfoLog :: %s%n", glGetProgramInfoLog(shaderProgram));
            throw new RuntimeException("Shader Program failed linking");
        }
    }

    private void checkIfShaderCompiledSuccessfully(int vertShaderHandle) {
        if(glGetShaderi(vertShaderHandle, GL_COMPILE_STATUS) == GL_TRUE) {
            System.out.println("shader compiled successfully");
        } else {
            System.out.printf("glGetShaderInfoLog :: %s%n", glGetShaderInfoLog(vertShaderHandle));
            throw new RuntimeException("Shader failed compilation");
        }
    }

    private void destroy() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

}