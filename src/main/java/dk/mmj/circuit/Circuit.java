package dk.mmj.circuit;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.PublicKey;

public interface Circuit {

    /**
     * Evaluates the circuit given inputs and a public-Key,
     * which might be needed in homomorphic operations
     * <br/>
     * <b>NOTE:</b> for the output to bear meaning, all inputs must be encrypted under the same key
     *
     * @param pk the public key that all input ciphertexts are encrypted under
     * @param input array of ciphertexts, as input
     * @return Results of the circuit, encrypted under the same public-key as the inputs
     */
    Ciphertext evaluate(PublicKey pk, Ciphertext ...input);

}
