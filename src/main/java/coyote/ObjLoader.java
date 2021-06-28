package coyote;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ObjLoader {

    public static Obj queryObjFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter an obj: ");
        String input = scanner.next();
        System.out.println("You entered :: " + input);
        String fileName = input + ".obj";


        try {
            File file = new File(
                    Objects.requireNonNull(ObjLoader.class.getClassLoader().getResource(fileName)).getFile()
            );
            return ObjLoader.loadObj(file);
        } catch (NullPointerException | FileNotFoundException e) {
            System.err.println("Could not find file with name: " + fileName);
        }

        return new Obj();
    }

    public static Obj loadObj(File objFile) throws FileNotFoundException {

        Obj obj = new Obj();

        List<Float> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Scanner scanner = new Scanner(objFile);
        while(scanner.hasNext()) {
            String line = scanner.nextLine();
            Scanner lineScanner = new Scanner(line);
            String flag = lineScanner.next();

            if(flag.equals("v")) {
                float v1, v2, v3;
                v1 = lineScanner.nextFloat();
                v2 = lineScanner.nextFloat();
                v3 = lineScanner.nextFloat();
                vertices.add(v1);
                vertices.add(v2);
                vertices.add(v3);

            }
            else if (flag.equals("f")) {
                int v1, v2, v3;
                v1 = lineScanner.nextInt();
                v2 = lineScanner.nextInt();
                v3 = lineScanner.nextInt();
                indices.add(v1);
                indices.add(v2);
                indices.add(v3);
            }
        }
        scanner.close();

        obj.vertices = vertices.stream().collect(
                ()-> FloatBuffer.allocate( vertices.size()),
                FloatBuffer::put,
                (left, right) -> {
                    throw new UnsupportedOperationException("only be called in parallel stream");
                }
        );

        obj.indices = indices.stream().collect(
                ()-> IntBuffer.allocate( indices.size()),
                IntBuffer::put,
                (left, right) -> {
                    throw new UnsupportedOperationException("only be called in parallel stream");
                }
        );

        return obj;
    }


}
