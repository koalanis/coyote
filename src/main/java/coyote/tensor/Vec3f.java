package coyote.tensor;

public class Vec3f {



    private float[] data;

    public Vec3f() {
        data = new float[3];
    }

    public Vec3f(float x, float y, float z) {
        this();
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }

    public Vec3f(float s) {
        this(s,s,s);
    }

    public float x(){
        return data[0];
    }

    public float y(){
        return data[1];
    }

    public float z(){
        return data[2];
    }

    public float norm() {
        return Vec3f.norm(this);
    }

    public float[] toData() {
        return data;
    }


    public static Vec3f add(Vec3f a, Vec3f b) {
        return new Vec3f(a.x()+b.x(), a.y()+b.y(), a.z()+b.z());
    }

    public static Vec3f scale(Vec3f a, float scalar) {
        return new Vec3f(a.x()*scalar, a.y()*scalar, a.z()*scalar);
    }

    public static float norm(Vec3f a) {
        return Floats.sqrt(a.x()*a.x() + a.y()*a.y() + a.z()+a.z());
    }

    public static Vec3f cross(Vec3f a, Vec3f b) {
        return new Vec3f(a.y()*b.z() - a.z()*b.y(),
                         -1.0f*(a.x()*b.z() - a.z()*b.x()),
                          a.x()*b.y() - a.y()*b.x());
    }

    public static float dot(Vec3f a, Vec3f b) {
        return a.x()*b.x() + a.y()*b.y() + a.z()*b.z();
    }

    public static Vec3f subtract(Vec3f a, Vec3f b) {
        return new Vec3f(a.x()-b.x(), a.y()-b.y(), a.z()-b.z());
    }






}
