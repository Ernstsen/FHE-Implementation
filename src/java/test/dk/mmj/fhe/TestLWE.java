package dk.mmj.fhe;

import dk.mmj.fhe.interfaces.Ciphertext;
import dk.mmj.fhe.interfaces.FHE;
import dk.mmj.fhe.interfaces.PublicKey;
import dk.mmj.fhe.interfaces.SecretKey;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class TestLWE {
    private final int securityParameter = 64;
    private FHE lwe;
    private FHE.KeyPair keyPair;

    @Before
    public void setup() {
        lwe = new LWE();
        keyPair = lwe.generateKey(securityParameter);
        assertNotNull(keyPair);
    }

    @Test
    public void testKeyGeneration() {
        FHE.KeyPair keyPair2 = lwe.generateKey(securityParameter);

        assertNotNull(keyPair2);
        assertNotEquals(
                "Keypairs not allowed to be the same, for two calls (only with very small prob.)",
                keyPair, keyPair2
        );
    }

    @Test
    public void testRandomnessInEncrypt() {
        boolean m = true;
        Ciphertext c1 = lwe.encrypt(m, keyPair.getPublicKey());
        Ciphertext c2 = lwe.encrypt(m, keyPair.getPublicKey());

        assertNotEquals("Ciphertext should not match for two different encryptions", c1, c2);
    }

    @Test
    public void testEncryptDecrypt() {
        for (boolean m : new Boolean[]{false, true}) {
            Ciphertext c = lwe.encrypt(m, keyPair.getPublicKey());
            boolean decrypt = lwe.decrypt(c, keyPair.getSecretKey());

            assertEquals("Dec(Enc(m))!=m, for m=" + (m ? "True" : "False"), m, decrypt);
        }
    }

    @Test
    public void testNand() {
        final Boolean[] options = {true, false};

        final PublicKey pk = keyPair.getPublicKey();
        final SecretKey sk = keyPair.getSecretKey();

        for (Boolean m1 : options) {
            for (Boolean m2 : options) {

                final Ciphertext c1 = lwe.encrypt(m1, pk);
                final Ciphertext c2 = lwe.encrypt(m2, pk);
                final Ciphertext nand = lwe.nand(c1, c2, pk);

                final boolean decrypt = lwe.decrypt(nand, sk);

                assertEquals("Homomorphic NAND did not match result of normal NAND", !(m1 & m2), decrypt);
            }
        }
    }

}
