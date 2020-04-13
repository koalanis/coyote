package coyote.tensor;

import java.util.Objects;

public class Mat3 {

    private float[] data;

    public Mat3() {
        this.data = new float[3*3];
    }

    public Mat3(float[] data) {
        if(Objects.isNull(data) || data.length != 9) {
            throw new IllegalStateException("Input matrix data is not of length 9 (3 x 3)");
        }
        this.data = data;
    }


    public float get(int row, int col) {
        if(0 > row || row > 2 || 0 > col || col > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }

        return data[3*col + row];
    }

    public void set(int row, int col, float value) {
        if(0 > row || row > 2 || 0 > col || col > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }
        data[3*row + col] = value;
    }

    public Vec3f getRow(int row) {
        if(0 > row || row > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }

        return new Vec3f(data[3*row], data[3*row + 1], data[3*row + 2]);
    }

    public Vec3f getCol(int col) {
        if(0 > col || col > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }

        return new Vec3f(data[col], data[3 + col], data[3*2 + col]);
    }

    public static Mat3 mult(Mat3 a, Mat3 b) {
        Mat3 mat = new Mat3();
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                mat.set(i, j, Vec3f.dot(a.getRow(i), b.getCol(j)));
            }
        }
        
        return mat;
    }

}
