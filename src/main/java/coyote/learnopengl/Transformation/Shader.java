package coyote.learnopengl.Transformation;


import com.sun.tools.javac.util.StringUtils;
import coyote.utils.Utils;
import jdk.internal.net.http.common.Pair;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

public class Shader {

    int shaderId;

    public Shader(int shaderId) {
        this.shaderId = shaderId;
    }

    public void use() {
        GL33.glUseProgram(shaderId);
    }















    public static Shader createShaderFromFiles(String vShaderSource, String gShaderSource, String fShaderSource) throws IOException {
        int shaderHandle = createShaderProgramFromFiles(vShaderSource, gShaderSource, fShaderSource);
        return new Shader(shaderHandle);
    }

    public static Shader createShader(String vShader, String gShader, String fShader) {
        int shaderHandle = createShaderProgram(vShader, gShader, fShader);
        return new Shader(shaderHandle);
    }

    public static int createShaderProgramFromFiles(String vShaderSource, String gShaderSource, String fShaderSource) throws IOException {
        try {
            String v = Utils.readFile(vShaderSource, StandardCharsets.US_ASCII);
            String g = Utils.readFile(gShaderSource, StandardCharsets.US_ASCII);
            String f = Utils.readFile(fShaderSource, StandardCharsets.US_ASCII);
            return Shader.createShaderProgram(v,g,f);
        } catch(IOException e) {
            System.err.printf("Error when trying to create shader programs from files %s \n", e.getLocalizedMessage());
            throw e;
        }
    }

    public static int createShaderProgram(String vshader, String gshader, String fshader) {
        List<Integer> shaderHandles =  new ArrayList<>();
        if(vshader != null && !vshader.isEmpty()) {
            int vertShaderHandle = createShaderSource(vshader, GL32.GL_VERTEX_SHADER);
            shaderHandles.add(vertShaderHandle);
        }

        if(gshader != null && !gshader.isEmpty()) {
            int geoShaderHandle = createShaderSource(gshader, GL32.GL_GEOMETRY_SHADER);
            shaderHandles.add(geoShaderHandle);
        }

        if(fshader != null && !fshader.isEmpty()) {
            int fragShaderHandle = createShaderSource(fshader, GL32.GL_VERTEX_SHADER);
            shaderHandles.add(fragShaderHandle);
        }

        int shaderProgram = glCreateProgram();

        for(Integer handle : shaderHandles) {
            glAttachShader(shaderProgram, handle);
        }
        glLinkProgram(shaderProgram);
        checkIfProgramLinkedSuccessfully(shaderProgram);

        for(Integer handle : shaderHandles) {
            glDeleteShader(handle);
        }

        return shaderProgram;
    }




    private static int createShaderSource(String shaderSource, int shaderType) {
        int shaderHandle = glCreateShader(shaderType);
        glShaderSource(shaderHandle, shaderSource);
        glCompileShader(shaderHandle);
        checkIfShaderCompiledSuccessfully(shaderHandle);
        return shaderHandle;
    }

    public static void checkIfProgramLinkedSuccessfully(int shaderProgram) {
        if(glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_TRUE) {
            System.out.println("program compiled successfully");
        } else {
            System.out.printf("glGetProgramInfoLog :: %s%n", glGetProgramInfoLog(shaderProgram));
            throw new RuntimeException("Shader Program failed linking");
        }
    }

    public static void checkIfShaderCompiledSuccessfully(int vertShaderHandle) {
        if(glGetShaderi(vertShaderHandle, GL_COMPILE_STATUS) == GL_TRUE) {
            System.out.println("shader compiled successfully");
        } else {
            System.out.printf("glGetShaderInfoLog :: %s%n", glGetShaderInfoLog(vertShaderHandle));
            throw new RuntimeException("Shader failed compilation");
        }
    }




}
