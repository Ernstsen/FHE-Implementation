package dk.mmj.circuit;

public enum GateType {
    NOT(1),
    AND(2),
    NAND(2),
    OR(2),
    XOR(2),
    INPUT(0);
    int inDegree;

    GateType(int inDegree) {
        this.inDegree = inDegree;
    }
}

