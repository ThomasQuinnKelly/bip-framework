package gov.va.bip.framework.security.jks;

import static org.junit.Assert.*;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.cert.Certificate;
import java.util.Base64;

import org.junit.Test;

public class KeystoreUtilsTest {
	
	
	/** Private Key in PEM format for testing */
	final String PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" + 
			"MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDvhdcxme9MYxs1\n" + 
			"zQgA8KTt/7ol/uFL7OwNREYVhsR80TGgi0+D2RGFFHSYYRPxHSnY0n4k92ylN3SW\n" + 
			"jNAfnULOj6VcfPGs6iK+8tcjevPZ+e8BshMBiMOpYxmYKjOluYAUuoDFkRhOiZF4\n" + 
			"03xJvUN/9QuiC420/E/wV1agsfqfvlaIqOyWmLcJuaKjLkJloLppP7y74rNfZSyb\n" + 
			"JsIFgjFOpwDbzdJXN0HxyTiKMkYPYCeRtGhCVsh1/NXo1AYQXVHcLUXPexKlQfGl\n" + 
			"JwobIxlSbzYwEotaZT2u6ZdoXCDwB7kDpMMNxqnlRDnxNzjWkaXJX0e21debZqso\n" + 
			"c8CFQSVbAgMBAAECggEAS2IiMhgyeiYO4EXhKPqTzdvYKDEpkw3D1QER1aCOooHs\n" + 
			"TozJv8NHgkzHzVVwQ0hcbUMCNHwyk2T6qo8yyWBjMEOBVbR1E2i0+sfO8fm2WXoR\n" + 
			"wMTcunTir6Li4UMW8ieJqfxwnr1lmwIs6IwW59ZLMjBbMurT9IwcVJYIS5b4O+G9\n" + 
			"ynuWyIzIiR3pBrvs1bppPPf7HysvGBDFvNU+lxbvssJcmWLcWwdQHw81BVx61R67\n" + 
			"FJWLwKZwzGfSF/E40or37yyGNwZphkloE2fdKR1xme9idG5CHQA4EJ+2Xzq1urr4\n" + 
			"JIq6AnzKCQYQu7EkTf6ysTCMj3ZW8w2oR9WwZgllQQKBgQD+JWUNpjgMDtF7Ds7r\n" + 
			"USyct/O46KGxjO4XtC/bqu5Pvp2zR8N9aa0fBdYE/IxsyaeSYF006hwv+sU7OaV/\n" + 
			"HyQ9aHNfkUqqQWLCL23EoBeroDf41AL0gzHYo34b1Vy72wL5KqBZALHEWiTp2Y8E\n" + 
			"as2jdObHZegB05Siia3JHSWwKwKBgQDxRSM818IvI6LMGUNetHKKRrdsUOEpf6g7\n" + 
			"1GJ3F8KJ5DXkp4h8UswAou3QVB0+wP9mglSXCBKphjTEqbqrIx6v5GRqmt7bdgfd\n" + 
			"qGnBlWyNWm3lKmfte3NgEc1Y++lD//wZo4NHKN+es2iq/1T/e/AWFyrO593kwaHJ\n" + 
			"NCJHyX2XkQKBgQCyP24Bxbv3kQkRYxBzcOp2PHp/DQIjMjHnhCQw0FD2sz2N+V49\n" + 
			"/rlCUeiKdMrUnhaejmGw9CWy7RIZ+NN/NktHmpOYyqFwig2HHpbBWntfjy7SoXg1\n" + 
			"IDL8u6Z+RQYWZ4RNV71Az0De806CiWlKctIpu5sE7Q9tAIV1/lRaAsxlIwKBgQCU\n" + 
			"Wced/KiY5ZePWaVUES4wmeTkxCJ+qtDd4Oyef8yNjvYYX28LyrlHVudVZrMIyBEC\n" + 
			"jfbHWx5mgG/n0NlHq3OOAahA6HNJjBywDYLhlihGDiludJGss3CwzKFwKo0206rG\n" + 
			"dQUedyJw6a3dHty7rxBj9ODMkkOdCfaZyeWeMGV6EQKBgQDBqGF7n+oeAxSKvjAy\n" + 
			"gmqVgVdo2AV4+IEWeYNzf9dLEoEfcemLX/UlbxjsFCUi86aj9+sDk9rCOwEAs31s\n" + 
			"ZazRYHVvscDw463Np/GQ8bC9asqCzwJIBpHR7SG3xFj6vOF0ZGK164wdz261CkwZ\n" + 
			"ojLD/v843JZjySG9rXweW9aS0A==\n" + 
			"-----END PRIVATE KEY-----\n" + 
			"";
	
	/** Public Certificate in PEM format for testing */
	final String PUBLIC_CERT = "-----BEGIN CERTIFICATE-----\n" + 
			"MIIC1DCCAbwCCQDIf0EZWofP0jANBgkqhkiG9w0BAQsFADAsMQswCQYDVQQGEwJV\n" + 
			"UzEdMBsGA1UEAwwUYmlwLXJlZmVyZW5jZS1wZXJzb24wHhcNMTkwODA2MTczNzU3\n" + 
			"WhcNMjAwODA1MTczNzU3WjAsMQswCQYDVQQGEwJVUzEdMBsGA1UEAwwUYmlwLXJl\n" + 
			"ZmVyZW5jZS1wZXJzb24wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDv\n" + 
			"hdcxme9MYxs1zQgA8KTt/7ol/uFL7OwNREYVhsR80TGgi0+D2RGFFHSYYRPxHSnY\n" + 
			"0n4k92ylN3SWjNAfnULOj6VcfPGs6iK+8tcjevPZ+e8BshMBiMOpYxmYKjOluYAU\n" + 
			"uoDFkRhOiZF403xJvUN/9QuiC420/E/wV1agsfqfvlaIqOyWmLcJuaKjLkJloLpp\n" + 
			"P7y74rNfZSybJsIFgjFOpwDbzdJXN0HxyTiKMkYPYCeRtGhCVsh1/NXo1AYQXVHc\n" + 
			"LUXPexKlQfGlJwobIxlSbzYwEotaZT2u6ZdoXCDwB7kDpMMNxqnlRDnxNzjWkaXJ\n" + 
			"X0e21debZqsoc8CFQSVbAgMBAAEwDQYJKoZIhvcNAQELBQADggEBANonpizsN6zD\n" + 
			"cvCb/SDngj8GpXr+1sjjMPEd4Nn2OqxPI9VJV/GggtwXv/nyI6J2EEJk5QpdETwl\n" + 
			"4yH48rhVQ/8t3IpnR7huquwKs1Jfx+SUYhwkI3hKuu5UQjBSboSiHIPmEXzm86Z/\n" + 
			"1XG4KliX3jaysdrE1j8FAg+nAwsUjgEU2Jx0d3xHTmoo4G5gwTF8XgvbCXIzaz0S\n" + 
			"OJ+n6B3lxRgCaT6oSxrLD2pD4rHw+M9fVysI7T+rBrHMbSxNwOqhW99UhiIdOMuj\n" + 
			"Nu2xvJCrQuQ79yRNFd2pcAhk0+zLcsAn4yDgSJ0QzD/3k0jRwcvJkg5Q13ADmWiU\n" + 
			"f5oSXDO+WaQ=\n" + 
			"-----END CERTIFICATE-----\n" + 
			"";
	
	/**
	 * Test creation of client keystore. Expect that a keystore containing the given private key and public certificate.
	 * @throws Exception
	 */
	@Test
	public void testCreateKeyStore() throws Exception {
		
		final String privateKeyPassword = "password";
		final String alias = "test";
		
		//Execute the method we want to test
		KeyStore result = KeystoreUtils.createKeyStore(PUBLIC_CERT, PRIVATE_KEY, privateKeyPassword, alias);
		
		//Verify our alias is contained in the keystore
		assertTrue(result.containsAlias(alias));
		
		
		//Verify the Private key matches what we provided
		Key resultPK = result.getKey(alias, privateKeyPassword.toCharArray());
		String encodedPK = "-----BEGIN PRIVATE KEY-----" + Base64.getEncoder().encodeToString(resultPK.getEncoded()) + "-----END PRIVATE KEY-----";
		assertEquals(PRIVATE_KEY.replace("\n", ""), encodedPK);
		
		//Verify our public certificate
		Certificate resultCert = result.getCertificate(alias);
		String encodedCert = "-----BEGIN CERTIFICATE-----" + Base64.getEncoder().encodeToString(resultCert.getEncoded()) + "-----END CERTIFICATE-----";
		assertEquals(PUBLIC_CERT.replace("\n", ""), encodedCert);
		
	}
	
	/**
	 * Test creation of client keystore without a private key password. Expect that a keystore containing the given private key and public certificate.
	 * A Blank string is expected for the private key password.
	 * @throws Exception
	 */
	@Test
	public void testCreateKeyStore_NoPassword() throws Exception {
		final String privateKeyPassword = null;
		final String alias = "test";
		
		//Execute the method we want to test
		KeyStore result = KeystoreUtils.createKeyStore(PUBLIC_CERT, PRIVATE_KEY, privateKeyPassword, alias);
		
		//Verify our alias is contained in the keystore
		assertTrue(result.containsAlias(alias));
		
		
		//Verify the Private key matches what we provided
		Key resultPK = result.getKey(alias, "".toCharArray());
		String encodedPK = "-----BEGIN PRIVATE KEY-----" + Base64.getEncoder().encodeToString(resultPK.getEncoded()) + "-----END PRIVATE KEY-----";
		assertEquals(PRIVATE_KEY.replace("\n", ""), encodedPK);
		
		//Verify our public certificate
		Certificate resultCert = result.getCertificate(alias);
		String encodedCert = "-----BEGIN CERTIFICATE-----" + Base64.getEncoder().encodeToString(resultCert.getEncoded()) + "-----END CERTIFICATE-----";
		assertEquals(PUBLIC_CERT.replace("\n", ""), encodedCert);
	
	}
	
	/**
	 * Test creation of trustd keystore. Expect that a keystore containing the given certificate is returned and that the certificate entry is marked as trusted.
	 * @throws Exception
	 */
	@Test
	public void testCreateTrustStore() throws Exception {
		final String alias = "test";
		
		//Execute the method we want to test
		KeyStore result = KeystoreUtils.createTrustStore(alias, PUBLIC_CERT);
		
		//Verify our alias is contained in the keystore
		assertTrue(result.containsAlias(alias));
		
		//Verify our trusted certificate
		Certificate resultCert = result.getCertificate(alias);
		String encodedCert = "-----BEGIN CERTIFICATE-----" + Base64.getEncoder().encodeToString(resultCert.getEncoded()) + "-----END CERTIFICATE-----";
		assertEquals(PUBLIC_CERT.replace("\n", ""), encodedCert);
		
		//Check to make sure this is a trusted certificate
		Entry entry = result.getEntry(alias, null);
		assertTrue(entry instanceof KeyStore.TrustedCertificateEntry);
		
	}
}