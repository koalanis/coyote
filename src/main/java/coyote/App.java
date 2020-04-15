package coyote;


import coyote.tensor.Mat;
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



    public static void main(String[] args) {

//        Simulation sim  = new Simulation();
//        sim.run();

        try {

            Mat mat = new Mat(3, 3);
            mat.set(0, 0, 1.0f);
            mat.set(1, 1, 2.0f);
            mat.set(2, 2, 3.0f);

            Mat vec = new Mat(3, 1);

            System.out.println(mat);

            vec.set(0, 0, 1.0f);
            vec.set(1, 0, 1.0f);
            vec.set(2, 0, 1.0f);

            System.out.println(vec);


            Mat vec_1 = Mat.mult(mat, vec);
            System.out.println(vec_1);

            Mat vec_2= Mat.transpose(vec_1);
            System.out.println(vec_2);


        } catch(Exception e) {
            e.printStackTrace(System.err);
            System.err.println(e.getLocalizedMessage());
        }

    }

}