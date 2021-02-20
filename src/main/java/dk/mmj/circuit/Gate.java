package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.PublicKey;

public interface Gate {
    /**
     * Evaluates the gate
     *
     * @return encryption of gate output
     */
    Ciphertext evaluate(PublicKey pk, Ciphertext... input);
}
