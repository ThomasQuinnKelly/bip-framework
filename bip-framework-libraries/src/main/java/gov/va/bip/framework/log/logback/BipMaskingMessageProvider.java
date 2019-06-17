package gov.va.bip.framework.log.logback;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import ch.qos.logback.classic.spi.ILoggingEvent;
import net.logstash.logback.composite.JsonWritingUtils;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;

/**
 * A logstash message provider to the generator in the JSON object context.
 * <p>
 * Usage of this class is declared in the {@code bip-framework-logback-starter.xml} logback configuration.
 */
public class BipMaskingMessageProvider extends MessageJsonProvider {

	/** The rules to apply to event messages */
	private BipMaskRules rules;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.logstash.logback.composite.loggingevent.MessageJsonProvider#writeTo(
	 * com.fasterxml.jackson.core.JsonGenerator,
	 * ch.qos.logback.classic.spi.ILoggingEvent)
	 */
	@Override
	public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
		JsonWritingUtils.writeStringField(generator, getFieldName(), rules.apply(event.getFormattedMessage()));
	}

	/**
	 * Sets the rules to be used by this provider.
	 * <p>
	 * During initialization, logback uses the {@code bip-framework-logback-starter.xml}
	 * tag names by convention to construct the names of methods and classes it will
	 * expect to be available for its use.
	 * In this case, the {@code <rules>} tag name prescribes the name part of the setter.
	 *
	 * @param rules
	 *            the new rules
	 */
	public void setRules(BipMaskRules rules) {
		this.rules = rules;
	}
}
