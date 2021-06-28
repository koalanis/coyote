package coyote;


public class Shaders {



    public static String simpleVertShader =
            "#version 330 core\n" +
            "layout (location = 0) in vec3 vertex_position;\n" +
            "uniform mat4 view_projection;"+
            "void main()\n" +
            "{\n" +
            "    gl_Position =  view_projection * vec4(vertex_position.x, vertex_position.y, vertex_position.z, 1.0);\n" +
            "}";



    public static String simpleFragShader =
            "#version 330 core\n" +
            "out vec4 fragment_color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fragment_color = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "}";








    public static String tinyVertShader = "" +
            "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);\n" +
            "}";


    public static String tinyFragShader = "" +
            "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    FragColor = vec4(1.0f, 0.5f, 0.2f, 1.0f);\n" +
            "} ";





















    public static String vertexShaderSrc =
            "#version 330 core\n"+
            "in vec3 vertex_position;"+ // A vector (x,y,z) representing the vertex's position
            "uniform vec3 light_position;"+ // Global variable representing the light's position
            "out vec3 vs_light_direction;"+ // Used for shading by the fragment shader
            "void main() {"+
            "gl_Position = vec4(vertex_position, 1.0);"+ // Don't transform the vertices at all
            "vs_light_direction = light_position - vertex_position;"+ // Calculate vector to the light (used for shading in fragment shader)
            "}";

    public static String geoShaderSrc =
            "#version 330 core\n" +
            "layout (triangles) in;" + // Reads in triangles
            "layout (triangle_strip, max_vertices = 3) out;" + // And outputs triangles
            "uniform mat4 view_projection;" +// The matrix encoding the camera position and settings. Don't worry about this for now
            "in vec3 vs_light_direction[];" +// The light direction computed in the vertex shader
            "out vec3 normal;" +// The normal of the triangle. Needs to be computed inside this shader
            "out vec3 light_direction;" +// Light direction again (this is just passed straight through to the fragment shader)
            "void main() {"+
            //Took the cross product of (p2 - p1) and (p3 - p1) which returns a vector
            "vec3 norm = cross( gl_in[1].gl_Position.xyz - gl_in[0].gl_Position.xyz, gl_in[2].gl_Position.xyz - gl_in[0].gl_Position.xyz);"+
            // normalized the resulting vector to obtain the unit vector
            "normal = normalize(norm);"+
            "}";;

    public static String fragShaderSrc =
            "#version 330 core\n"+
            "in vec3 normal;" +// Normal computed in the geometry shader
            "in vec3 light_direction;"+ // Light direction computed in the vertex shader
            "out vec4 fragment_color;" +// This shader will compute the pixel color
            "void main() {"+
            "vec4 color = vec4(1.0, 0.0, 0.0, 1.0);"+ // Red
            "float dot_nl = dot(normalize(light_direction), normalize(normal));"+ // Compute brightness based on angle between normal and light
            "dot_nl = clamp(dot_nl, 0.0, 1.0);" +// Ignore back-facing triangles
            "fragment_color = clamp(dot_nl * color, 0.0, 1.0);"+
            "}";



}
