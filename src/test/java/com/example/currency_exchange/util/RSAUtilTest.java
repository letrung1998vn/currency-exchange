package com.example.currency_exchange.util;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.*;

class RSAUtilTest {

    @Test
    void generateKeyPair_shouldReturnNonNullKeys() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        assertNotNull(kp);
        assertNotNull(kp.getPrivate());
        assertNotNull(kp.getPublic());
    }

    @Test
    void publicPrivateBase64_roundtrip_shouldRestoreKeys() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        PublicKey pub = kp.getPublic();
        PrivateKey priv = kp.getPrivate();

        String pubB64 = RSAUtil.publicKeyToBase64(pub);
        String privB64 = RSAUtil.privateKeyToBase64(priv);

        assertNotNull(pubB64);
        assertNotNull(privB64);

        PublicKey pub2 = RSAUtil.publicKeyFromBase64(pubB64);
        PrivateKey priv2 = RSAUtil.privateKeyFromBase64(privB64);

        assertNotNull(pub2);
        assertNotNull(priv2);
        // basic sanity: encoded forms should be equal
        assertArrayEquals(pub.getEncoded(), pub2.getEncoded());
        assertArrayEquals(priv.getEncoded(), priv2.getEncoded());
    }

    @Test
    void encryptDecrypt_roundtrip_shouldReturnOriginalPlaintext() {
        KeyPair kp = RSAUtil.generateKeyPair(2048);
        String plaintext = "Hello-RSA-测试-€";

        String cipher = RSAUtil.encrypt(plaintext, kp.getPublic());
        assertNotNull(cipher);
        assertFalse(cipher.isEmpty());

        String decrypted = RSAUtil.decrypt(cipher, kp.getPrivate());
        assertEquals(plaintext, decrypted);
    }

    @Test
    void decrypt_withWrongPrivateKey_shouldThrowRuntimeException() {
        KeyPair kp1 = RSAUtil.generateKeyPair(2048);
        KeyPair kp2 = RSAUtil.generateKeyPair(2048);

        String plaintext = "secret-value";
        String cipher = RSAUtil.encrypt(plaintext, kp1.getPublic());

        assertThrows(RuntimeException.class, () -> RSAUtil.decrypt(cipher, kp2.getPrivate()));
    }

}