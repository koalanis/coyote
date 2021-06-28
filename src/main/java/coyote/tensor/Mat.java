package coyote.tensor;

import java.util.Arrays;

public class Mat {

    public int rowDim;
    public int colDim;
    protected float[] data;



    public Mat(int row, int col) {
        this.colDim = col;
        this.rowDim = row;
        this.data = new float[col*row];
    }

    private int getLoc(int row, int col) {
        if(row >= rowDim || row < 0 || col >= colDim || col < 0)
            throw new IndexOutOfBoundsException(
                    String.format("(%d x %d) is out of bounds of matrix dimensions (%d x %d)", row, col, rowDim, colDim));

        return colDim*row + col;
    }

    public float get(int row, int col) {
        return this.data[this.getLoc(row, col)];
    }

    public void set(int row, int col, float val) {
        this.data[this.getLoc(row, col)] = val;
    }

    public float[] toData() {
        return data;
    }

    public static Mat mult(Mat a, Mat b) {
        if(a.colDim != b.rowDim) {
            throw new IllegalArgumentException(
                    String.format("Cannot multiply a (%d x %d) by a (%d x %d)", a.rowDim, a.colDim, b.rowDim, b.colDim)
            );
        }

        int K = a.colDim;
        Mat mat = new Mat(a.rowDim, b.colDim);

        for(int c = 0; c<mat.colDim; c++) {
            for(int r = 0; r < mat.rowDim; r++) {
                float dot = 0;
                for(int k = 0; k < K; k++) {
                    dot += a.get(r,k)*b.get(k, c);
                }
                mat.set(r, c, dot);
            }
        }

        return mat;
    }

    public static Mat transpose(Mat a) {

        Mat mat = new Mat(a.colDim, a.rowDim);
        for(int r = 0; r < a.rowDim; r++) {
            for(int c = 0; c < a.colDim; c++) {
                mat.set(c, r, a.get(r, c));
            }
        }
        return mat;
    }



    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();

        build.append(String.format("Matrix (%d x %d)", this.rowDim, this.colDim)).append(System.lineSeparator());
        for(int c = 0; c < colDim; c++) {
            for(int r = 0; r < rowDim; r++) {
                build.append("| ");
                build.append(String.format(" %.5f ", this.get(r, c)));
            }
            build.append(" |").append(System.lineSeparator());
        }
        build.append("Raw Data Layout :: ").append(Arrays.toString(this.data)).append(System.lineSeparator());

        return build.toString();
    }
}
