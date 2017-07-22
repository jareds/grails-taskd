package com.jaredstofflett.grailstaskd

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter

import java.security.PrivateKey
import java.security.Security
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class CertsAndKeysUtils {
    public static X509Certificate getCert(byte[] certData) {
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certData));
    }

    public static PrivateKey loadPrivateKey(byte[] keyData) {
        Security.addProvider(new BouncyCastleProvider());
        def pp = new PEMParser(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(keyData))))
        def pemKeyPair = pp.readObject();
        def kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
        def privateKey = kp.getPrivate()
        return privateKey
    }
}
