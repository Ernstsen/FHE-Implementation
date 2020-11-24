package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.FHE;

public class LWEParameters implements FHE.Parameters {

    private int n = 3;
    private int m = n;
    private int q = 1000003;
    private double alpha = 0.0000024;

    public LWEParameters() {
    }

    int getN() {
        return n;
    }

    public LWEParameters setN(int n) {
        this.n = n;
        return this;
    }

    int getM() {
        return m;
    }

    public LWEParameters setM(int m) {
        this.m = m;
        return this;
    }

    int getQ() {
        return q;
    }

    public LWEParameters setQ(int q) {
        this.q = q;
        return this;
    }

    double getAlpha() {
        return alpha;
    }

    public LWEParameters setAlpha(double alpha) {
        this.alpha = alpha;
        return this;
    }
}
