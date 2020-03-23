package gov.va.bip.framework.rest.provider;

import gov.va.bip.framework.messages.MessageKeys;
import gov.va.bip.framework.messages.MessageSeverity;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.junit.Assert.assertEquals;

public class MessagesAwareResponseEntityTest {

    @Test
    public void testMessagesAwareResponseEntity() {
        ProviderResponse providerResponse = new ProviderResponse();

        MessagesAwareResponseEntity messagesAwareResponseEntity = new MessagesAwareResponseEntity(providerResponse, HttpStatus.CREATED);

        assertEquals(HttpStatus.CREATED, messagesAwareResponseEntity.getStatusCode());

        providerResponse.addMessage(MessageSeverity.ERROR, MessageKeys.NO_KEY.getKey(), "ServiceMessage text", HttpStatus.BAD_REQUEST);

        messagesAwareResponseEntity = new MessagesAwareResponseEntity(providerResponse, HttpStatus.CREATED);

        assertEquals(HttpStatus.BAD_REQUEST, messagesAwareResponseEntity.getStatusCode());
    }

}
