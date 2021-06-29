package coyote.learnopengl.Textures;

import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Textures2{
    // The window handle
    private long window;

    final int BYTES_PER_FLOAT = 4;

    final int VERTEX_POS_SIZE   = 3; // x, y
    final int VERTEX_COLOR_SIZE = 4; // r, g, b, and a
    final int VERTEX_UV_SIZE = 2; // u,v

    final int VERTEX_POS_INDX   = 0;

    final int VERTEX_COLOR_INDX = 1;
    final int VERTEX_UV_INDX = 2;

    final int VERTEX_STRIDE =  ( BYTES_PER_FLOAT * (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE + VERTEX_UV_SIZE) );

    public static String tinyVertShader = "" +
            "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec4 aColor;\n" +
            "layout (location = 2) in vec2 aTexCoord;\n" +
            "\n" +
            "out vec4 ourColor;\n" +
            "out vec2 TexCoord;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\tgl_Position = vec4(aPos, 1.0);\n" +
            "\tourColor = aColor;\n" +
            "\tTexCoord = vec2(aTexCoord.x, aTexCoord.y);\n" +
            "}";


    public static String tinyFragShader = "" +
            "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "in vec4 ourColor;\n" +
            "in vec2 TexCoord;\n" +
            "\n" +
            "// texture samplers\n" +
            "uniform sampler2D texture1;\n" +
            "uniform sampler2D texture2;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "\t// linearly interpolate between both textures (80% container, 20% awesomeface)\n" +
            "vec4 color =  mix(texture(texture1, TexCoord), texture(texture2, TexCoord), 0.2);" +
            "\tFragColor = color;\n" +
            "}";

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

    private int createShaderProgram() {
        int vertShaderHandle = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShaderHandle, tinyVertShader);
        glCompileShader(vertShaderHandle);
        checkIfShaderCompiledSuccessfully(vertShaderHandle);

        int fragShaderHandle = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShaderHandle, tinyFragShader);
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
        return shaderProgram;
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

        int shaderProgram = createShaderProgram();

        float[] vertices = {
                // positions          // colors           // texture coords
                0.5f,  0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, // top right
                0.5f, -0.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, // bottom right
                -0.5f, -0.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, // bottom left
                -0.5f,  0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f  // top left
        };

        int[] indices = {
                0, 1, 3, // first triangle
                1, 2, 3  // second triangle
        };

        int vao = glGenVertexArrays();
        int vbo = glGenBuffers();
        int ebo = glGenBuffers();
        {
            glBindVertexArray(vao);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

            // position attribute
            glVertexAttribPointer(0, VERTEX_POS_SIZE, GL_FLOAT, false, VERTEX_STRIDE, 0);
            glEnableVertexAttribArray(0);

            // color attribute
            glVertexAttribPointer(1, VERTEX_COLOR_SIZE, GL_FLOAT, false, VERTEX_STRIDE, VERTEX_POS_SIZE * BYTES_PER_FLOAT);
            glEnableVertexAttribArray(1);

            // texture coord attribute
            glVertexAttribPointer(2, VERTEX_UV_SIZE, GL_FLOAT, false, VERTEX_STRIDE, (VERTEX_POS_SIZE + VERTEX_COLOR_SIZE) * BYTES_PER_FLOAT);
            glEnableVertexAttribArray(2);
        }
        int texture1 = glGenTextures();
        {
            glBindTexture(GL_TEXTURE_2D, texture1);
            // set the texture wrapping parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            // set texture filtering parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            STBImage.stbi_set_flip_vertically_on_load(true);
            IntBuffer w1 = BufferUtils.createIntBuffer(1);
            IntBuffer h1 = BufferUtils.createIntBuffer(1);
            IntBuffer d1 = BufferUtils.createIntBuffer(1);
            String path = "C:\\Users\\kaleb\\Documents\\GitHub\\coyote\\src\\main\\resources\\container.jpg";
            ByteBuffer image1 = STBImage.stbi_load(path, w1, h1, d1, 0);
            if (Objects.nonNull(image1) && image1.capacity() > 0) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w1.get(), h1.get(), 0, GL_RGB, GL_UNSIGNED_BYTE, image1);
                glGenerateMipmap(GL_TEXTURE_2D);
            } else {
                System.out.println("Failed to load texture");
            }
            STBImage.stbi_image_free(image1);
        }

        int texture2 = glGenTextures();
        {
            glBindTexture(GL_TEXTURE_2D, texture2);
            // set the texture wrapping parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);    // set texture wrapping to GL_REPEAT (default wrapping method)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            // set texture filtering parameters
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            IntBuffer w2 = BufferUtils.createIntBuffer(1);
            IntBuffer h2 = BufferUtils.createIntBuffer(1);
            IntBuffer d2 = BufferUtils.createIntBuffer(1);
            String path2 = "C:\\Users\\kaleb\\Documents\\GitHub\\coyote\\src\\main\\resources\\awesomeface.png";
            ByteBuffer image2 = STBImage.stbi_load(path2, w2, h2, d2, 0);

            if (Objects.nonNull(image2) && image2.capacity() > 0) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w2.get(), h2.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image2);
                glGenerateMipmap(GL_TEXTURE_2D);
            } else {
                System.out.println("Failed to load texture");
            }

            STBImage.stbi_image_free(image2);
        }

        glUseProgram(shaderProgram);
        glUniform1i(glGetUniformLocation(shaderProgram, "texture1"), 0);
        glUniform1i(glGetUniformLocation(shaderProgram, "texture2"), 1);

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

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, texture1);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, texture2);

            glUseProgram(shaderProgram);
            glBindVertexArray(vao);
            //glDrawArrays(GL_TRIANGLES, 0, 6);
            glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
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
        new Textures2().run();
    }
}