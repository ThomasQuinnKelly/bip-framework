package gov.va.bip.framework.security.jwt;

import gov.va.bip.framework.log.BipLogger;
import gov.va.bip.framework.log.BipLoggerFactory;
import gov.va.bip.framework.security.PersonTraits;
import gov.va.bip.framework.security.jwt.JwtAuthenticationProperties.JwtKeyPairs;
import gov.va.bip.framework.security.jwt.correlation.CorrelationIdsParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

/**
 * Parse the encrypted JWT
 */
public class JwtParser {
	static final BipLogger LOGGER = BipLoggerFactory.getLogger(JwtParser.class);

	/** number of milliseconds in a second */
	private static final double NUMBER_OF_MILLIS_N_A_SECOND = 1000.0;

	/** The spring configurable properties used for authentication */
	private JwtAuthenticationProperties jwtAuthenticationProperties;

	/**
	 * Parse the JWT JSON into its component values
	 *
	 * @param properties
	 */
	public JwtParser(final JwtAuthenticationProperties properties) {
		this.jwtAuthenticationProperties = properties;
	}

	/**
	 * Decrypts the JWT and attempts to construct a PersonTraits object from it.
	 * If correlation id parsing fails, {@code null} is returned.
	 *
	 * @param token
	 *            the encrypted JWT
	 * @return PersonTraits, or {@code null} if some issue with the correlation
	 *         ids
	 */
	public PersonTraits parseJwt(final String token) {

		long startTime = System.currentTimeMillis();

		// New feature to support multiple consumers
		Claims claims = parseJwtKeyPairs(token);

		// Old feature to support single consumer
		if (claims == null && StringUtils.isNotBlank(jwtAuthenticationProperties.getIssuer())
				&& StringUtils.isNotBlank(jwtAuthenticationProperties.getSecret())) {
			// We will sign our JWT with our ApiKey secret
			final Key signingKey = createSigningKey(jwtAuthenticationProperties.getSecret());

			claims = Jwts.parser().setSigningKey(signingKey).requireIssuer(jwtAuthenticationProperties.getIssuer())
					.parseClaimsJws(token).getBody();
		}

		final long elapsedTime = System.currentTimeMillis() - startTime;

		LOGGER.debug("Time elapsed to parse JWT token {}{}{}", "[", elapsedTime / NUMBER_OF_MILLIS_N_A_SECOND,
				" secs]");

		if (claims != null) {
			return getPersonFrom(claims);
		} else {
			return null;
		}
	}

	/**
	 * Parses the JWT key pairs.
	 *
	 * @param token
	 *            the token
	 * @param claims
	 *            the claims
	 * @return the claims
	 */
	private Claims parseJwtKeyPairs(final String token) {
		
		Claims claims = null;
		final List<JwtKeyPairs> jwtKeyPairs = jwtAuthenticationProperties.getKeyPairs();
		if (jwtKeyPairs != null && !jwtKeyPairs.isEmpty()) {
			for (int i = 0; i < jwtKeyPairs.size(); i++) {
				final JwtKeyPairs jwtKeyPair = jwtKeyPairs.get(i);
				// We will sign our JWT with our ApiKey secret
				Key signingKey = createSigningKey(jwtKeyPair.getSecret());
				try {
					claims = Jwts.parser().setSigningKey(signingKey).requireIssuer(jwtKeyPair.getIssuer())
							.parseClaimsJws(token).getBody();
					LOGGER.debug("claims {}", claims);
				} catch (final Exception e) {
					LOGGER.error("Exception parsing JWT token {}", e);
					if (i == jwtKeyPairs.size() - 1) {
						LOGGER.error("Rethrowing Error");
						throw e;
					} else {
						LOGGER.warn("Trying next JWT Key Pair");
					}
				} // end catch
				if (claims != null) {
					break;
				}
			}
		}
		return claims;
	}

	/**
	 * Creates the signing key.
	 *
	 * @param secret
	 *            the secret
	 * @return the key
	 */
	private Key createSigningKey(final String secret) {
		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), signatureAlgorithm.getJcaName());
	}

	/**
	 * Attempts to produce a PersonTraits object from the correlation ids. If
	 * correlation id parsing fails, {@code null} is returned.
	 *
	 * @param claims
	 *            - the JWT contents
	 * @return PersonTraits, or {@code null} if some issue with the correlation
	 *         ids
	 */
	@SuppressWarnings("unchecked")
	private PersonTraits getPersonFrom(final Claims claims) {
		PersonTraits personTraits = new PersonTraits();

		personTraits.setFirstName(claims.get("firstName", String.class));
		personTraits.setLastName(claims.get("lastName", String.class));
		personTraits.setPrefix(claims.get("prefix", String.class));
		personTraits.setMiddleName(claims.get("middleName", String.class));
		personTraits.setSuffix(claims.get("suffix", String.class));
		personTraits.setBirthDate(claims.get("birthDate", String.class));
		personTraits.setGender(claims.get("gender", String.class));
		personTraits.setAssuranceLevel(claims.get("assuranceLevel", Integer.class));
		personTraits.setEmail(claims.get("email", String.class));
		personTraits.setTokenId(claims.get("jti", String.class));
		personTraits.setAppToken(claims.get("appToken", String.class));
		personTraits.setApplicationID(claims.get("applicationID", String.class));
		personTraits.setUserID(claims.get("userID", String.class));
		personTraits.setStationID(claims.get("stationID", String.class));

		try {
			List<String> list = (List<String>) claims.get("correlationIds");
			CorrelationIdsParser.parseCorrelationIds(list, personTraits);

		} catch (final Exception e) { // NOSONAR intentionally wide, errors are
			// already logged
			// if there is any detected issue with the correlation ids
			personTraits = null;
		}

		return personTraits;
	}

}
