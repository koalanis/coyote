package coyote;

import coyote.tensor.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Simulation {

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
    private int simpleShaderProgram;

    private int vertexShader;
    private int geoShader;
    private int fragShader;


    private Obj obj;

    private int vao;
    private int vbo;
    private int ebo;

    private int windowWidth = 800;
    private int windowHeight = 600;
    private int view_projection_matrix_location;
    private int light_position_location;
    private Mat4 view_proj;

    private FloatBuffer fb;
    private Vec3 light_position;
    private float[] backgroundRBG;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        objInit();
        graphicsInit();
        shaderInit();
        simpleShaderInit();
        sceneInit();
        loop();
        cleanup();

    }

    private void init() {

        backgroundRBG = new float[3];

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
        window = glfwCreateWindow(windowWidth, windowHeight, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            if ( key == GLFW_KEY_SPACE && action == GLFW_RELEASE )
                randomBackgroundColor();
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
        GL32.glShaderSource(this.vertexShader, Shaders.simpleFragShader);
        GL32.glCompileShader(this.vertexShader);

        int s =  GL32.glGetShaderi(this.vertexShader, GL32.GL_COMPILE_STATUS);

        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Vertex Shader");
            System.out.println(GL32.glGetShaderInfoLog(this.vertexShader));
        }

        // create fragment shader
        System.out.println("Creating Fragment Shaders");

        this.geoShader = GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER);
        GL32.glShaderSource(this.geoShader, Shaders.geoShaderSrc);
        GL32.glCompileShader(this.geoShader);

        s = GL32.glGetShaderi(this.geoShader, GL32.GL_COMPILE_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Geo Shader");
            System.out.println(GL32.glGetShaderInfoLog(this.geoShader));
        }


        // create fragment shader
        System.out.println("Creating Fragment Shaders");

        this.fragShader = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
        GL32.glShaderSource(this.fragShader, Shaders.fragShaderSrc);
        GL32.glCompileShader(this.fragShader);

        s = GL32.glGetShaderi(this.fragShader, GL32.GL_COMPILE_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Fragment Shader");
            System.out.println(GL32.glGetShaderInfoLog(this.fragShader));
        }



        GL32.glAttachShader(shaderProgram, vertexShader);
        GL32.glAttachShader(shaderProgram, geoShader);
        GL32.glAttachShader(shaderProgram, fragShader);

        GL32.glBindAttribLocation(this.shaderProgram, 0, "vertex_position");
        GL32.glBindFragDataLocation(this.shaderProgram, 0, "fragment_color");

        GL32.glLinkProgram(this.shaderProgram);
        s = GL32.glGetProgrami(shaderProgram, GL32.GL_LINK_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println(GL32.glGetProgramInfoLog(this.shaderProgram));
        }

        this.view_projection_matrix_location = GL32.glGetUniformLocation(this.shaderProgram, "view_projection");
        this.light_position_location = GL32.glGetUniformLocation(this.shaderProgram, "light_position");

        // after compiling shader program, free shader resources
        GL32.glDeleteShader(this.vertexShader);
        GL33.glDeleteShader(this.geoShader);
        GL32.glDeleteShader(this.fragShader);

        System.out.println("Finshed Creating Shaders");


    }

    private void randomBackgroundColor() {
        Random ran = new Random();
        backgroundRBG[0] = ran.nextFloat();
        backgroundRBG[1] = ran.nextFloat();
        backgroundRBG[2] = ran.nextFloat();
    }
    private void simpleShaderInit() {
        // create shader program
        this.simpleShaderProgram = GL33.glCreateProgram();

        System.out.println("Creating Shaders");

        // create vertex shader

        System.out.println("Creating Vertex Shaders");
        this.vertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
        GL33.glShaderSource(this.vertexShader, Shaders.simpleVertShader);
        GL33.glCompileShader(this.vertexShader);

        int s =  GL32.glGetShaderi(this.vertexShader, GL33.GL_COMPILE_STATUS);

        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Vertex Shader");
            System.out.println(GL33.glGetShaderInfoLog(this.vertexShader));
        }

        // create fragment shader
        System.out.println("Creating Fragment Shaders");

        this.fragShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
        GL33.glShaderSource(this.fragShader, Shaders.simpleFragShader);
        GL33.glCompileShader(this.fragShader);

        s = GL33.glGetShaderi(this.fragShader, GL32.GL_COMPILE_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println("Error Creating Fragment Shader");
            System.out.println(GL33.glGetShaderInfoLog(this.fragShader));
        }



        GL33.glAttachShader(simpleShaderProgram, vertexShader);
        GL33.glAttachShader(simpleShaderProgram, fragShader);


        GL33.glLinkProgram(this.simpleShaderProgram);
        s = GL33.glGetProgrami(simpleShaderProgram, GL33.GL_LINK_STATUS);
        System.out.println(s);
        if(s == 0) {
            System.out.println(GL33.glGetProgramInfoLog(this.simpleShaderProgram));
        }

        // after compiling shader program, free shader resources
        GL33.glDeleteShader(this.vertexShader);
        GL33.glDeleteShader(this.fragShader);

        System.out.println("Finished Creating Shaders");
    }

    private void objInit() {
        File file = new File(
                Objects.requireNonNull(ObjLoader.class.getClassLoader().getResource("triangle.obj")).getFile()
        );
        try {
            this.obj = ObjLoader.loadObj(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(obj);

        this.vertices = this.obj.vertices.array();
        this.indices = this.obj.indices.array();
    }

    public float cot(float f)
    {
        return 1.0f/((float)Math.tan(f));
    }

    public void sceneInit() {


        Vec3 up = new Vec3(0.0f, 0.1f, 0.4f);
        Vec3 center = new Vec3(0.0f, 0.1f, -0.1f);
        Vec3 eye = new Vec3(0.0f, 0.1f, 0.5f);


        Vec3 look = Vec3.subtract(center, eye);
        look.normalize();

        Vec3 tangent = Vec3.cross(look, up);



        // look at matrix process

//        float [] mat1 = {
//                cameraRight.x(),  cameraUp.x(),    cameraDirection.x(), cameraPosition.x(),
//                cameraRight.y(),  cameraRight.y(), cameraDirection.y(), cameraPosition.y(),
//                cameraRight.z(),  cameraUp.z(),    cameraDirection.z(), cameraPosition.z(),
//                0.0f, 0.0f, 0.0f, 1.0f,
//        };

//        Mat4 view = new Mat4(mat1);
        Mat4 view = Mat4.of(Mat.transpose( new Mat4(
                new Vec4(tangent.x(), tangent.y(), tangent.z(), -1.0f * eye.x()),
                new Vec4(up.x(), up.y(), up.z(), -1.0f * eye.y()),
                new Vec4(-1.0f*look.x(), -1.0f*look.y(), -1.0f*look.z(), -1.0f * eye.z()),
                new Vec4(0.0f, 0.0f, 0.0f, 1.0f))));



        System.out.println(view);
        System.out.println("View Matrix");
        // getting the projection matrix

        float aspect = (windowWidth*1.0f) / (windowHeight * 1.0f);
        float fovy = (float) Math.toRadians(45.0f);
        float near = 0.0001f;
        float far = 1000.0f;

//        float top = ((float) Math.tan(fov/2.0f)) * near;
//        float bottom = -1.0f * top;
//        float right = top*aspectRatio;
//        float left =  -1.0f *top*aspectRatio;

//        float[] proj_mat = {
//                (2.0f*near)/(right-left), 0.0f, (right + left)/(right - left), 0.0f,
//                0.0f, (2.0f*near)/(top-bottom), (top + bottom)/(top - bottom), 0.0f,
//                0.0f, 0.0f, -1.0f*(far + near)/(far - near), -1.0f*(2.0f*far*near)/(far - near),
//                0.0f, 0.0f, -1.0f, 0.0f
//        };
//
//        Mat4 proj = new Mat4(proj_mat);
//        proj = Mat4.of(Mat.transpose(proj));
//        System.out.println(proj);


        Mat4 proj = Mat4.of(Mat.transpose(new Mat4(
                new Vec4(cot(fovy/2.0f)/aspect, 0.0f, 0.0f, 0.0f),
                new Vec4( 0.0f, cot(fovy/2.0f), 0.0f, 0.0f),
                new Vec4( 0.0f, 0.0f, -1.0f*(far + near)/(far - near), (-2.0f*far*near)/(far - near)),
                new Vec4(0.0f, 0.0f, -1.0f, 0.0f)))
        );
        System.out.println(proj);




        this.light_position = new Vec3(10.0f, 0.0f, 10.0f);



        Matrix4f m = new Matrix4f()
                .perspective((float) Math.toRadians(45.0f), aspect, 0.00001f, 1000.0f)
                .lookAt(0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f);

        this.fb = BufferUtils.createFloatBuffer(16);
        m.get(fb);



        view_proj = Mat4.of(Mat.mult(proj, view));

        System.out.println(view_proj);
    }

    private void graphicsInit() {
        this.vao = GL33.glGenVertexArrays();
        this.vbo = GL33.glGenBuffers();
        this.ebo = GL33.glGenBuffers();


        GL33.glBindVertexArray(this.vao);

        GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vbo);
        GL33.glBufferData(GL33.GL_ARRAY_BUFFER, this.vertices, GL33.GL_STATIC_DRAW);
        GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 0, 0);
        GL33.glEnableVertexAttribArray(0);

        GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, this.ebo);
        GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, this.indices, GL33.GL_STATIC_DRAW);



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
        glClearColor(backgroundRBG[0], backgroundRBG[1], backgroundRBG[2], 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.


        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            GL33.glUseProgram(this.simpleShaderProgram);
            GL33.glUseProgram(this.shaderProgram);

            GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, this.vbo);
            GL33.glBufferData(GL33.GL_ARRAY_BUFFER, this.vertices, GL33.GL_STATIC_DRAW);

            GL33.glUniformMatrix4fv(this.view_projection_matrix_location, false, this.fb);



            GL33.glUniform3fv(this.light_position_location, this.light_position.toData());

            GL33.glBindVertexArray(this.vao);
            GL33.glDrawElements(GL33.GL_TRIANGLES, this.indices.length, GL33.GL_UNSIGNED_INT, 0);


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
}
