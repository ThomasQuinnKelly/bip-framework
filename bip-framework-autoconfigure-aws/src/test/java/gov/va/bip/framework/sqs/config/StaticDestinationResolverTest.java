package gov.va.bip.framework.sqs.config;

import org.junit.Test;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Session;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaticDestinationResolverTest {

    /**
     * Test of resolveDestinationName method, of class StaticDestinationResolver.
     */
    @Test
    public void testResolveDestinationName() throws Exception {
        String destinationName = "queueName";
        Session session = mock(Session.class);
        Queue queue = mock(Queue.class);
        when(session.createQueue(eq(destinationName))).thenReturn(queue);

        boolean pubSubDomain = false;
        StaticDestinationResolver instance = new StaticDestinationResolver(destinationName);
        Destination result = instance.resolveDestinationName(session, destinationName, pubSubDomain);
        assertNotNull(result);
    }
    
}
