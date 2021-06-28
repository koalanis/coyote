package coyote.tensor;

import java.util.Objects;

public class Mat3 extends Mat{

    public Mat3() {
        super(3, 3);
    }

    public Mat3(float[] data) {
        this();
        if(Objects.isNull(data) || data.length != 9)
            throw new IllegalStateException("Input matrix data is not of length 9 (3 x 3)");
        this.data = data;
    }

    public Mat3(Vec3 i, Vec3 j, Vec3 k) {
        this();
        this.data[0] = i.x();
        this.data[1] = i.y();
        this.data[2] = i.z();

        this.data[3] = j.x();
        this.data[4] = j.y();
        this.data[5] = j.z();

        this.data[6] = k.x();
        this.data[7] = k.y();
        this.data[8] = k.z();
    }

    public Vec3 getRow(int row) {
        if(0 > row || row > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }

        return new Vec3(this.get(row, 0),  this.get(row, 1), this.get(row, 2));
    }

    public Vec3 getCol(int col) {
        if(0 > col || col > 2 ) {
            throw new IndexOutOfBoundsException("There is no such entry in a 3 x 3 matrix");
        }

        return new Vec3(this.get(0, col),  this.get( 1, col), this.get( 2, col));
    }
}
