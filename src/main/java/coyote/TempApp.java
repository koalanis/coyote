//package coyote;
//
//import coyote.tensor.Mat;
//import coyote.tensor.Vec3;
//import org.ejml.simple.SimpleMatrix;
//import org.lwjgl.Version;
//import org.lwjgl.glfw.GLFWErrorCallback;
//import org.lwjgl.glfw.GLFWVidMode;
//import org.lwjgl.opengl.GL;
//import org.lwjgl.opengl.GL32;
//import org.lwjgl.system.MemoryStack;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Objects;
//import java.util.Scanner;
//
//import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
//import static org.lwjgl.glfw.GLFW.*;
//import static org.lwjgl.glfw.GLFW.glfwTerminate;
//import static org.lwjgl.system.MemoryStack.stackPush;
//import static org.lwjgl.system.MemoryUtil.NULL;
//
//public class TempApp {
//}
//package coyote;
//
//
//        import coyote.tensor.Mat;
//        import coyote.tensor.Vec3;
//        import org.lwjgl.*;
//        import org.lwjgl.glfw.*;
//        import org.lwjgl.opengl.*;
//        import org.lwjgl.system.*;
//
//        import java.nio.*;
//
//        import static org.lwjgl.glfw.Callbacks.*;
//        import static org.lwjgl.glfw.GLFW.*;
//        import static org.lwjgl.opengl.GL11.*;
//        import static org.lwjgl.system.MemoryStack.*;
//        import static org.lwjgl.system.MemoryUtil.*;
//
//
//public class App {
//
//
//    private static void MatAndVecTest() {
//
//        try {
//
//            Mat mat = new Mat(3, 3);
//            mat.set(0, 0, 1.0f);
//            mat.set(1, 1, 2.0f);
//            mat.set(2, 2, 3.0f);
//
//            Mat vec = new Mat(3, 1);
//
//            System.out.println(mat);
//
//            vec.set(0, 0, 1.0f);
//            vec.set(1, 0, 1.0f);
//            vec.set(2, 0, 1.0f);
//
//            System.out.println(vec);
//
//
//            Mat vec_1 = Mat.mult(mat, vec);
//            System.out.println(vec_1);
//
//            Mat vec_2= Mat.transpose(vec_1);
//            System.out.println(vec_2);
//
//
//            Vec3 vec3 = new Vec3(1.0f);
//            Vec3 vec4 = new Vec3(1.0f, 2.0f, 11.0f);
//
//
//
//            System.out.println(vec3);
//            System.out.println(vec4);
//
//            Vec3 vec5 = Vec3.add(vec3, vec4);
//            System.out.println(vec5);
//
//            Vec3 vec6 = Vec3.subtract(vec3, vec4);
//            System.out.println("vec6");
//
//
//            System.out.println(vec6);
//
//            float normVec6 = Vec3.norm(vec6);
//            System.out.println(normVec6);
//
//            Vec3 vec7 = Vec3.normalize(vec6);
//            System.out.println(vec7);
//
//            vec3.set(0.0f, 0.0f, 1.0f);
//            vec4.set(1.0f, 0.0f, 0.0f);
//
//            Vec3 vec8 = Vec3.cross(vec3, vec4);
//            System.out.println(vec8);
//
////            Obj obj = ObjLoader.queryObjFile();
////            System.out.println(obj);
//
//
//        } catch(Exception e) {
//            e.printStackTrace(System.err);
//            System.err.println(e.getLocalizedMessage());
//        }
//    }
//
//
//    public static void main(String[] args) {
//
//
////        MatAndVecTest();
//
//
//        Simulation sim  = new Simulation();
//        sim.run();
//
//
//    }
//
//}
//
//package coyote;
//
//        import org.ejml.simple.SimpleMatrix;
//        import org.lwjgl.Version;
//        import org.lwjgl.glfw.GLFWErrorCallback;
//        import org.lwjgl.glfw.GLFWVidMode;
//        import org.lwjgl.opengl.GL;
//        import org.lwjgl.opengl.GL32;
//        import org.lwjgl.system.MemoryStack;
//
//        import java.io.File;
//        import java.io.FileNotFoundException;
//        import java.nio.FloatBuffer;
//        import java.nio.IntBuffer;
//        import java.util.ArrayList;
//        import java.util.Arrays;
//        import java.util.Objects;
//        import java.util.Scanner;
//
//        import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
//        import static org.lwjgl.glfw.GLFW.*;
//        import static org.lwjgl.glfw.GLFW.glfwTerminate;
//        import static org.lwjgl.system.MemoryStack.stackPush;
//        import static org.lwjgl.system.MemoryUtil.NULL;
//
//public class App2 {
//
//    // The window handle
//    private long window;
//
//    private ArrayList<Float> verticesList;
//    private ArrayList<Integer> indiciesList;
//
//
//    private float[] vertices;
//
//    private float[] triangle  = {
//            -0.5f, -0.5f, 0.0f,
//            0.5f, -0.5f, 0.0f,
//            0.0f,  0.5f, 0.0f
//    };
//    private int vbo;
//    private int[] vbos = new int[2];
//
//    private int vertexShader;
//    private int geometryShader;
//    private int fragShader;
//    public int K_VERTEX_BUFFER = 0;
//    public int K_INDEX_BUFFER = 1;
//    private int shaderProgram;
//    private int vao;
//    private int[] faces;
//
//    private float[] lightPos = new float [] {10.f, 0.0f, 10.0f};
//
//    private int view_projection_matrix_location;
//    private int light_position_matrix_location;
//    private int windowWidth = 800;
//    private int windowHeight = 600;
//    private SimpleMatrix viewProjectionMatrix;
//    private FloatBuffer vertexBuffer;
//    private IntBuffer faceBuffer;
//
//    private void run() {
//        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
//
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Please enter an obj: ");
//        String input = scanner.next();
//        System.out.println("You entered :: " + input);
//        String fileName = input + ".obj";
//
//
//        try {
//            File file = new File(
//                    Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile()
//            );
//            loadObj(file);
//
//            System.out.println(String.format("obj loaded has %s vertices, and %s indices", this.verticesList.size(), this.indiciesList.size()));
//
//            this.vertexBuffer = verticesList.stream().collect(
//                    ()-> FloatBuffer.allocate( verticesList.size()),
//                    FloatBuffer::put,
//                    (left, right) -> {
//                        throw new UnsupportedOperationException("only be called in parallel stream");
//                    }
//            );
//
//
//
//
//            this.faceBuffer = indiciesList.stream().collect(
//                    ()-> IntBuffer.allocate( indiciesList.size()),
//                    IntBuffer::put,
//                    (left, right) -> {
//                        throw new UnsupportedOperationException("only be called in parallel stream");
//                    }
//            );
//
//
//            System.out.println(Arrays.toString(this.vertexBuffer.array()));
//            System.out.println(Arrays.toString(this.faceBuffer.array()));
//
//
//        } catch (NullPointerException | FileNotFoundException e) {
//            System.err.println("Could not find file with name: " + fileName);
//        }
//
//
//        if(true) {
//            startSim();
//        }
//
//    }
//
//    private void loadObj(File objFile) throws FileNotFoundException {
//
//        this.verticesList = new ArrayList<>();
//        this.indiciesList = new ArrayList<>();
//
//        Scanner scanner = new Scanner(objFile);
//        while(scanner.hasNext()) {
//            String line = scanner.nextLine();
//            Scanner lineScanner = new Scanner(line);
//            String flag = lineScanner.next();
//
//            if(flag.equals("v")) {
//                float v1, v2, v3;
//                v1 = lineScanner.nextFloat();
//                v2 = lineScanner.nextFloat();
//                v3 = lineScanner.nextFloat();
//                this.verticesList.add(v1);
//                this.verticesList.add(v2);
//                this.verticesList.add(v3);
//
//            }
//            else if (flag.equals("f")) {
//                int v1, v2, v3;
//                v1 = lineScanner.nextInt();
//                v2 = lineScanner.nextInt();
//                v3 = lineScanner.nextInt();
//                this.indiciesList.add(v1);
//                this.indiciesList.add(v2);
//                this.indiciesList.add(v3);
//            }
//        }
//
//        scanner.close();
//    }
//
//    private void startSim() {
//        init();
//        loop();
//
//
//
//
//
//        // Free the window callbacks and destroy the window
//        glfwFreeCallbacks(window);
//        glfwDestroyWindow(window);
//
//        // Terminate GLFW and free the error callback
//        glfwTerminate();
//        try {
//            glfwSetErrorCallback(null).free();
//        } catch (Exception ex) {
//            System.err.println(ex.getMessage());
//        }
//    }
//
//    private void init() {
//        try {
//
//            // Setup an error callback. The default implementation
//            // will print the error message in System.err.
//            GLFWErrorCallback.createPrint(System.err).set();
//
//            // Initialize GLFW. Most GLFW functions will not work before doing this.
//            if ( !glfwInit() )
//                throw new IllegalStateException("Unable to initialize GLFW");
//
//            // Configure GLFW
//            glfwDefaultWindowHints(); // optional, the current window hints are already the default
//            glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
//            glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
//
//
//
//            // Create the window
//            window = glfwCreateWindow(this.windowWidth, this.windowHeight, "Hello World!", NULL, NULL);
//            if ( window == NULL )
//                throw new RuntimeException("Failed to create the GLFW window");
//
//            // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//            glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
//                if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
//                    glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//            });
//
//            // Get the thread stack and push a new frame
//            try ( MemoryStack stack = stackPush() ) {
//                IntBuffer pWidth = stack.mallocInt(1); // int*
//                IntBuffer pHeight = stack.mallocInt(1); // int*
//
//                // Get the window size passed to glfwCreateWindow
//                glfwGetWindowSize(window, pWidth, pHeight);
//
//                // Get the resolution of the primary monitor
//                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//
//                // Center the window
//                glfwSetWindowPos(
//                        window,
//                        (vidmode.width() - pWidth.get(0)) / 2,
//                        (vidmode.height() - pHeight.get(0)) / 2
//                );
//            } // the stack frame is popped automatically
//
//            // Make the OpenGL context current
//            glfwMakeContextCurrent(window);
//            // Enable v-sync
//            glfwSwapInterval(1);
//
//            // Make the window visible
//            glfwShowWindow(window);
//
//
//            // This line is critical for LWJGL's interoperation with GLFW's
//            // OpenGL context, or any context that is managed externally.
//            // LWJGL detects the context that is current in the current thread,
//            // creates the GLCapabilities instance and makes the OpenGL
//            // bindings available for use.
//            GL.createCapabilities();
//
//            // Set the clear color
//            GL32.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
//            GL32.glClear(GL32.GL_COLOR_BUFFER_BIT);
//
//
//
//            // init vertex buffer object
//            this.vao = GL32.glGenVertexArrays();
//            // init vertex buffer object
//            GL32.glBindVertexArray(this.vao);
//
//            GL32.glGenBuffers(vbos);
//
//            // Vertex positions
//            GL32.glBindBuffer(GL32.GL_ARRAY_BUFFER, this.vbos[K_VERTEX_BUFFER]);
//
//
//            GL32.glBufferData(GL32.GL_ARRAY_BUFFER, this.vertexBuffer, GL32.GL_STATIC_DRAW);
//
//            GL32.glVertexAttribPointer(0, 3, GL32.GL_FLOAT, false, 0, 0);
//            GL32.glEnableVertexAttribArray(0);
//
//            GL32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER, this.vbos[K_INDEX_BUFFER]);
////        GL32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER, this.faces, GL32.GL_STATIC_DRAW);
//            GL32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER, this.faceBuffer, GL32.GL_STATIC_DRAW);
//
//
//
//
//            this.createShaders();
//
////            Vec3f maxBounds = new Vec3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
////            Vec3f minBounds = new Vec3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
////
////            for(int i = 0; i < this.verticesList.size(); i+=3) {
////                Vec3f vert = new Vec3f(this.verticesList.get(i), this.verticesList.get(i+1), this.verticesList.get(i+2));
////                if(maxBounds.length() < vert.length()) {
////                    maxBounds.set(vert);
////                }
////
////                if(minBounds.length() > vert.length()) {
////                    minBounds.set(vert);
////                }
////            }
////
////            //  consider printing stuff
////
////
////            Vec3f up = new Vec3f(0.0f, 0.1f, 0.4f);
////
////            Vec3f cameraTarget = new Vec3f(0.0f, 0.1f, 0.0f);
////            Vec3f cameraPosition = new Vec3f(0.0f, 0.1f, 0.4f);
////
////
////            Vec3f cameraDirection = new Vec3f();
////            cameraDirection.sub(cameraPosition, cameraTarget);
////            cameraDirection.normalize();
////
////            Vec3f cameraRight = new Vec3f();
////            cameraRight.cross(up, cameraDirection);
////            cameraRight.normalize();
////
////            Vec3f cameraUp = new Vec3f();
////            cameraUp.cross(cameraDirection, cameraRight);
////            cameraUp.normalize();
////
////
////            // look at matrix process
////
////
////
////            float [] mat1 = {
////                    cameraRight.x, cameraRight.y, cameraRight.z, 0.0f,
////                    cameraUp.x, cameraUp.y, cameraUp.z, 0.0f,
////                    cameraDirection.x, cameraDirection.y, cameraDirection.z, 0.0f,
////                    cameraPosition.x, cameraPosition.y, cameraPosition.z, 1.0f,
////            };
////
////
////
////            SimpleMatrix viewMatrix = new SimpleMatrix(4, 4, false, mat1);
////            viewMatrix = viewMatrix.transpose();
////
////            System.out.println("View Matrix");
////            viewMatrix.print();
////            // getting the projection matrix
////
////            float aspectRatio = (windowWidth*1.0f) / (windowHeight * 1.0f);
////            float fov = 45.0f;
////            float near = 0.0001f;
////            float far = 1000.0f;
////
////            float top = ((float) Math.tan(fov/2.0f)) * near;
////            float bottom = -1.0f * top;
////            float right = top*aspectRatio;
////            float left =  -1.0f *top*aspectRatio;
////
////            float[] proj_mat = {
////                    (2.0f*near)/(right-left), 0.0f, 0.0f, 0.0f,
////                    0.0f, (2.0f*near)/(top-bottom), 0.0f, 0.0f,
////                    (right + left)/(right - left), (top + bottom)/(top - bottom), (-1.0f) * (far + near)/(far - near), -1.0f,
////                    0.0f, 0.0f, -1.0f * (2.0f*far*near) / (far - near), 0.0f,
////            };
////            SimpleMatrix perspMatrix = new SimpleMatrix(4, 4, false, proj_mat);
////            perspMatrix = perspMatrix.transpose();
////            System.out.println("Perspective Matrix");
////            perspMatrix.print();
////
////
////            this.viewProjectionMatrix = perspMatrix.mult(viewMatrix);
////
////            System.out.println("View_Projection Matrix");
////            viewProjectionMatrix.print();
//        } catch(Exception e) {
//            System.err.println(e.getLocalizedMessage());
//        }
//    }
//
//    private void createShaders() {
//
//
//        // create shader program
//        this.shaderProgram = GL32.glCreateProgram();
//
//        System.out.println("Creating Shaders");
//
//        // create vertex shader
//
//        System.out.println("Creating Vertex Shaders");
//        this.vertexShader = GL32.glCreateShader(GL32.GL_VERTEX_SHADER);
//        GL32.glShaderSource(this.vertexShader, Shaders.vertexShaderSrc);
//        GL32.glCompileShader(this.vertexShader);
//
//        int s =  GL32.glGetShaderi(this.vertexShader, GL32.GL_COMPILE_STATUS);
//
//        System.out.println(s);
//        if(s == 0) {
//            System.out.println("Error Creating Vertex Shader");
//            System.out.println(GL32.glGetShaderInfoLog(this.vertexShader));
//        }
//
//        // create geometry shader
//        System.out.println("Creating Geometry Shaders");
//
//        this.geometryShader = GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER);
//        GL32.glShaderSource(this.geometryShader, Shaders.geoShaderSrc);
//        GL32.glCompileShader(this.geometryShader);
//
//        s =  GL32.glGetShaderi(this.geometryShader, GL32.GL_COMPILE_STATUS);
//        System.out.println(s);
//        if(s == 0) {
//            System.out.println("Error Creating Geometry Shader");
//            System.out.println(GL32.glGetShaderInfoLog(this.geometryShader));
//        }
//
//        // create fragment shader
//        System.out.println("Creating Fragment Shaders");
//
//        this.fragShader = GL32.glCreateShader(GL32.GL_FRAGMENT_SHADER);
//        GL32.glShaderSource(this.fragShader, Shaders.fragShaderSrc);
//        GL32.glCompileShader(this.fragShader);
//
//        s = GL32.glGetShaderi(this.fragShader, GL32.GL_COMPILE_STATUS);
//        System.out.println(s);
//        if(s == 0) {
//            System.out.println("Error Creating Fragment Shader");
//            System.out.println(GL32.glGetShaderInfoLog(this.fragShader));
//        }
//
//
//
//        GL32.glAttachShader(shaderProgram, vertexShader);
//        GL32.glAttachShader(shaderProgram, geometryShader);
//        GL32.glAttachShader(shaderProgram, fragShader);
//
//
//        GL32.glBindAttribLocation(this.shaderProgram, 0, "vertex_position");
//        GL32.glBindFragDataLocation(this.shaderProgram, 0, "fragment_color");
//
//
//        GL32.glLinkProgram(this.shaderProgram);
//        s = GL32.glGetProgrami(shaderProgram, GL32.GL_LINK_STATUS);
//        System.out.println(s);
//        if(s == 0) {
//            System.out.println(GL32.glGetProgramInfoLog(this.shaderProgram));
//        }
//
//        // after compiling shader program, free shader resources
//        GL32.glDeleteShader(this.vertexShader);
//        GL32.glDeleteShader(this.geometryShader);
//        GL32.glDeleteShader(this.fragShader);
//
//        this.view_projection_matrix_location = 0;
//        this.light_position_matrix_location = 0;
//
//        view_projection_matrix_location = GL32.glGetUniformLocation(this.shaderProgram, "view_projection");
//        light_position_matrix_location = GL32.glGetUniformLocation(this.shaderProgram, "light_position");
//
//
//    }
//
//
//    public float[] get4X4(SimpleMatrix matrix) {
//        float [] mat = new float[16];
//        for(int i = 0; i < 16; i++) {
//            mat[i] = (float) matrix.get(i);
//        }
//
//        return mat;
//    }
//
//    private void loop() {
//
//        // Run the rendering loop until the user has attempted to close
//        // the window or has pressed the ESCAPE key.
//        while ( !glfwWindowShouldClose(window) ) {
//
//            GL32.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
//
//
//            GL32.glClear(GL32.GL_COLOR_BUFFER_BIT | GL32.GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//
//            GL32.glUseProgram(this.shaderProgram);
//
//            GL32.glUniformMatrix4fv(view_projection_matrix_location,
//                    false,
//                    this.get4X4(this.viewProjectionMatrix));
//
//            GL32.glUniform3fv(light_position_matrix_location, lightPos);
//
//            GL32.glBindVertexArray(this.vao);
//            GL32.glDrawElements(GL32.GL_TRIANGLES, this.indiciesList.size(), GL32.GL_UNSIGNED_INT, 0);
//            // Poll for window events. The key callback above will only be
//            // invoked during this call.
//            glfwSwapBuffers(window); // swap the color buffers
//            glfwPollEvents();
//        }
//
//        glfwDestroyWindow(window);
//        glfwTerminate();
//    }
//
//    public static void main(String[] args) {
//        new App2().run();
//    }
//
//}
