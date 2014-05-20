
package teal.math;

import javax.vecmath.Tuple2d;

public class Complex2d extends Tuple2d {

    private static final long serialVersionUID = 3257002155280905269L;

    public Complex2d() {
        super();
    }

    public Complex2d(Complex2d z) {
        super(z);
    }

    public Complex2d(double real, double imag) {
        super(real, imag);
    }

    public double real() {
        return x;
    }

    public double imag() {
        return y;
    }

    public void mul(Complex2d z) {
        double x = this.x;
        this.x = x * z.x - y * z.y;
        this.y = x * z.y + y * z.x;
    }

    public double getAbs() {
        return Math.sqrt(x * x + y * y);
    }

    public double getAngle() {
        return Math.atan2(y, x);
    }

    public void setAbs(double abs) {
        double angle = getAngle();
        x = abs * Math.cos(angle);
        y = abs * Math.sin(angle);
    }

    public void setAngle(double angle) {
        double abs = getAbs();
        x = abs * Math.cos(angle);
        y = abs * Math.sin(angle);
    }

    public static Complex2d exp(Complex2d z) {
        Complex2d Z = new Complex2d();
        Z.setAbs(Math.exp(z.x));
        Z.setAngle(z.y);
        return Z;
    }

}
