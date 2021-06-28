package coyote;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public class Obj {

    public FloatBuffer vertices;
    public IntBuffer indices;


    @Override
    public String toString() {

        String vertices_len = Objects.nonNull(vertices) ? String.valueOf(vertices.array().length) : "emtpy";
        String indices_len = Objects.nonNull(indices) ? String.valueOf(indices.array().length) : "emtpy";

        return "Obj{" +
                "vertices_len=" + vertices_len +
                ", indices_len=" + indices_len +
                '}';
    }
}
