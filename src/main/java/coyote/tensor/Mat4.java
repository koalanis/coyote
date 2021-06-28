package coyote.tensor;

import java.util.Objects;

public class Mat4 extends Mat{

    public Mat4() {
        super(4, 4);
    }

    public Mat4(float[] data) {
        this();
        if(Objects.isNull(data) || data.length != 16)
            throw new IllegalStateException("Input matrix data is not of length 9 (3 x 3)");
        this.data = data;
    }

    public Mat4(Vec4 i, Vec4 j, Vec4 k, Vec4 l) {
        this();
        this.data[0]  = i.x();
        this.data[1]  = i.y();
        this.data[2]  = i.z();
        this.data[3]  = i.w();

        this.data[4]  = j.x();
        this.data[5]  = j.y();
        this.data[6]  = j.z();
        this.data[7]  = j.w();

        this.data[8]  = k.x();
        this.data[9]  = k.y();
        this.data[10] = k.z();
        this.data[11] = k.w();

        this.data[12] = l.x();
        this.data[13] = l.y();
        this.data[14] = l.z();
        this.data[15] = l.w();
    }

    public static Mat4 mult(Mat4 a, Mat4 b) {
        return (Mat4) Mat.mult(a, b);
    }

    public static Mat4 of(Mat a) {
        if(a.rowDim != 4 && a.colDim != 4) {
            throw new IllegalStateException(String.format("Cannot create matrix from (%d x %d)", a.rowDim, a.colDim));
        }

        Mat4 mat = new Mat4(a.toData());

        return mat;
    }


}
