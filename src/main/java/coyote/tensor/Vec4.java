package coyote.tensor;

public class Vec4 extends Mat {

    public Vec4() {
        super(4, 1);
    }

    public Vec4(float x, float y, float z, float w) {
        this();
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = w;
    }

    public Vec4(float s) {
        this(s,s,s,s);
    }


    public void set(Vec3 a) {
        data[0] = a.data[0];
        data[1] = a.data[1];
        data[2] = a.data[2];
        data[3] = a.data[3];

    }

    public void set(float x, float y, float z, float w) {
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = w;
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

    public float w(){
        return this.data[3];
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

    public void w(float w){
        this.data[3] = w;
    }

    public Vec3 xyz() {
        return new Vec3(this.x(), this.y(), this.z());
    }
}
