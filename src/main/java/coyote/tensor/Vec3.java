package coyote.tensor;

public class Vec3 extends Mat{


    public Vec3() {
        super(3, 1);
    }

    public Vec3(float x, float y, float z) {
        this();
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }

    public Vec3(float s) {
        this(s,s,s);
    }


    public void set(Vec3 a) {
        data[0] = a.data[0];
        data[1] = a.data[1];
        data[2] = a.data[2];
    }

    public void set(float x, float y, float z) {
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }

    public float x(){
        return this.data[0];
    }

    public float y(){
        return this.data[1];
    }

    public float z(){
        return this.data[2];
    }

    public void x(float x){
        this.data[0] = x;
    }

    public void y(float y){
        this.data[1] = y;
    }

    public void z(float z){
        this.data[2] = z;
    }

    public void normalize() {
        this.set(Vec3.normalize(this));
    }

    public float norm() {
        return Vec3.norm(this);
    }

    public static Vec3 add(Vec3 a, Vec3 b) {
        return new Vec3(a.x()+b.x(), a.y()+b.y(), a.z()+b.z());
    }

    public static Vec3 scale(Vec3 a, float scalar) {
        return new Vec3(a.x()*scalar, a.y()*scalar, a.z()*scalar);
    }

    public static float norm(Vec3 a) {

        float norm_sq = (a.x()*a.x()) + (a.y()*a.y()) + (a.z()*a.z());
        return (float) Math.sqrt(norm_sq);
    }

    public static Vec3 normalize(Vec3 a) {

        float norm = Vec3.norm(a);
        float inv_norm = norm == 0.0f ? 0.0f : 1.0f/norm;
        return Vec3.scale(a, inv_norm);
    }

    public static Vec3 cross(Vec3 a, Vec3 b) {
        return new Vec3(a.y()*b.z() - a.z()*b.y(),
                -1.0f*(a.x()*b.z() - a.z()*b.x()),
                a.x()*b.y() - a.y()*b.x());
    }

    public static float dot(Vec3 a, Vec3 b) {

        Mat singleton = Mat.mult(a, Mat.transpose(b));;
        return singleton.data[0];
    }

    public static Vec3 subtract(Vec3 a, Vec3 b) {
        return new Vec3(a.x()-b.x(), a.y()-b.y(), a.z()-b.z());
    }


}
