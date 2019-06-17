package gov.va.bip.framework.log.logback;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * A set of logback rules to mask sensitive data.
 */
public class BipMaskRules {

	/** A NO-OP combiner for stream reduction (See {@link #apply(String, String)}. Do not use parallel streams. */
	private static final BinaryOperator<String> NO_OP = (in, out) -> {
		throw new UnsupportedOperationException("No parallel streams or mismatched arg types for this reduction.");
	};

	/** The list of rules */
	private final Set<BipMaskRule> rules = new LinkedHashSet<>();

	/**
	 * Adds the rule definition to the set of rules.
	 *
	 * @param definition
	 *            the definition
	 */
	public void addRule(BipMaskRule.Definition definition) {
		rules.add(definition.rule());
	}

	/**
	 * Apply rules to a string value.
	 * <p>
	 * Used by
	 * {@link BipMaskingMessageProvider#writeTo(com.fasterxml.jackson.core.JsonGenerator, ch.qos.logback.classic.spi.ILoggingEvent)}
	 *
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String apply(String input) {
		return rules.stream().reduce(input, (out, rule) -> rule.apply(out), NO_OP);
	}
}
