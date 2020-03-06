package gov.va.bip.framework.sqs.config;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SqsPropertiesTest {


    //Test SQS Queue Properties
    @Test
    public void testGetSqsQueueProperties() {
        SqsProperties instance = new SqsProperties();
        SqsQueueProperties queueProp = instance.getSqsQueueProperties();
        instance.setSqsQueueProperties(queueProp);

        assertNotEquals(instance.getSqsQueueProperties(), Optional.ofNullable(queueProp));
    }

    /**
     * Test of getNumberOfMessagesToPrefetch method, of class SqsProperties.
     */
    @Test
    public void testGetNumberOfMessagesToPrefetch() {
    	Integer preFetch = 8;
        SqsProperties instance = new SqsProperties();
        instance.setNumberofmessagestoprefetch(preFetch);

        assertEquals(instance.getPrefetch(), Optional.ofNullable(preFetch));
    }

    /**
     * Test of getQueueName method, of class SqsProperties.
     */
    @Test
    public void testGetQueueName() {
        SqsProperties instance = new SqsProperties();
        instance.setEndpoint("http://localhost:8080/queuename");
        assertEquals("queuename", instance.getQueueName());
    }

    /**
     * Test of getDLQQueueName method, of class SqsProperties.
     */
    @Test
    public void testGetDLQQueueName() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqendpoint("http://localhost:8080/queuename");
        assertEquals("queuename", instance.getDLQQueueName());
    }

    /**
     * Test of setSecretKey method, of class SqsProperties.
     */
    @Test
    public void testSetSecretKey() {
        SqsProperties instance = new SqsProperties();
        instance.setSecretKey("secretKey");
        assertEquals("secretKey", instance.getSecretKey());
    }

    /**
     * Test of setAccessKey method, of class SqsProperties.
     */
    @Test
    public void testSetAccessKey() {
        SqsProperties instance = new SqsProperties();
        instance.setAccessKey("accessKey");
        assertEquals("accessKey", instance.getAccessKey());

    }

    /**
     * Test of setRegion method, of class SqsProperties.
     */
    @Test
    public void testSetRegion() {
        SqsProperties instance = new SqsProperties();
        instance.setRegion("region");
        assertEquals("region", instance.getRegion());
    }

    /**
     * Test of setEndpoint method, of class SqsProperties.
     */
    @Test
    public void testSetEndpoint() {
        SqsProperties instance = new SqsProperties();
        instance.setEndpoint("http://localhost:8080/queuename");
        assertEquals("http://localhost:8080/queuename", instance.getEndpoint());
    }


    /**
     * Test of setDlqendpoint method, of class SqsProperties.
     */
    @Test
    public void testSetDlqendpoint() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqendpoint("http://localhost:8080/queuename");
        assertEquals("http://localhost:8080/queuename", instance.getDlqendpoint());
    }
    
    /**
     * Test of queueType method, of class SqsProperties.
     */
    @Test
    public void testSetQueueType() {
        boolean queueType = false;
        SqsProperties instance = new SqsProperties();
        instance.setQueuetype(queueType);
        assertEquals(queueType, instance.getQueuetype());
    }
    
    /**
     * Test of ContentBasedDuplication method, of class SqsProperties.
     */
    @Test
    public void testSetContentBasedDuplication() {
        boolean contentBased = false;
        SqsProperties instance = new SqsProperties();
        instance.setContentbaseddeduplication(contentBased);
        assertEquals(contentBased, instance.getContentbaseddeduplication());
    }
    
    /**
     * Test of Delay method, of class SqsProperties.
     */
    @Test
    public void testSetDelay() {
        Integer delay = 5;
        SqsProperties instance = new SqsProperties();
        instance.setDelay(delay);
        assertEquals(delay, instance.getDelay());
    }
    
    /**
     * Test of Max method, of class SqsProperties.
     */
    @Test
    public void testSetMaxmessagesize() {
        String max = "5";
        SqsProperties instance = new SqsProperties();
        instance.setMaxmessagesize(max);
        assertEquals(max, instance.getMaxmessagesize());
    }
    
    /**
     * Test of DLQ Max method, of class SqsProperties.
     */
    @Test
    public void testSetDLQMaxmessagesize() {
        String max = "5";
        SqsProperties instance = new SqsProperties();
        instance.setDlqmaxmessagesize(max);
        assertEquals(max, instance.getDlqmaxmessagesize());
    }

    /**
     * Test of Max Receive Coiunt, of class SqsProperties.
     */
    @Test
    public void testSetMaxReceiveCount() {
        String max = "5";
        SqsProperties instance = new SqsProperties();
        instance.setMaxreceivecount(max);
        assertEquals(max, instance.getMaxreceivecount());
    }


    /**
     * Test of MessageRet method, of class SqsProperties.
     */
    @Test
    public void testSetMessageretentionperiod() {
        String ret = "5";
        SqsProperties instance = new SqsProperties();
        instance.setMessageretentionperiod(ret);
        assertEquals(ret, instance.getMessageretentionperiod());
    }
    
    /**
     * Test of DlqMessageRet method, of class SqsProperties.
     */
    @Test
    public void testSetDlqMessageretentionperiod() {
        String ret = "5";
        SqsProperties instance = new SqsProperties();
        instance.setDlqmessageretentionperiod(ret);
        assertEquals(ret, instance.getDlqmessageretentionperiod());
    }

    /**
     * Test of Waittime method, of class SqsProperties.
     */
    @Test
    public void testSetWaittime() {
        Integer waittime = 5;
        SqsProperties instance = new SqsProperties();
        instance.setWaittime(waittime);
        assertEquals(waittime, instance.getWaittime());
    }
    
    /**
     * Test of DlqWaittime method, of class SqsProperties.
     */
    @Test
    public void testDlqSetWaittime() {
        Integer waittime = 5;
        SqsProperties instance = new SqsProperties();
        instance.setDlqwaittime(waittime);
        assertNotEquals(waittime, instance.getDlqwaittime());
    }

    /**
     * Test of Visibility method, of class SqsProperties.
     */
    @Test
    public void testSetVisibility() {
        Integer visibility = 5;
        SqsProperties instance = new SqsProperties();
        instance.setVisibilitytimeout(visibility);
        assertEquals(visibility, instance.getVisibilitytimeout());
    }
    
    /**
     * Test of Visibility method, of class SqsProperties.
     */
    @Test
    public void testSetDlqVisibility() {
        Integer visibility = 5;
        SqsProperties instance = new SqsProperties();
        instance.setDlqvisibilitytimeout(visibility);
        assertNotEquals(visibility, instance.getDlqvisibilitytimeout());
    }
    /**
     * Test of logger method, of class SqsProperties.
     */
    @Test
    public void testSetLogger() {
        Logger logger = LoggerFactory.getLogger(SqsProperties.class);
        SqsProperties instance = new SqsProperties();
        instance.setLogger(logger);
        assertEquals(logger, instance.getLogger());
    }

    //Test SQS Property enabled
    @Test
    public void testBoolean() {
        boolean enabled = true;
        SqsProperties instance = new SqsProperties();
        instance.setEnabled(enabled);

        TestCase.assertEquals(Optional.of(instance.getEnabled()), Optional.ofNullable(enabled));
    }

    //Test SQS Property DLQ enabled
    @Test
    public void testDLQBoolean() {
        boolean enabled = true;
        SqsProperties instance = new SqsProperties();
        instance.setDlqenabled(enabled);

        TestCase.assertEquals(Optional.of(instance.getDlqenabled()), Optional.ofNullable(enabled));
    }

    //Test SQS Property DLQ content
    @Test
    public void testDlqcontentbaseddeduplication() {
        boolean enabled = true;
        SqsProperties instance = new SqsProperties();
        instance.setDlqcontentbaseddeduplication(enabled);

        TestCase.assertEquals(Optional.of(instance.getDlqcontentbaseddeduplication()), Optional.ofNullable(enabled));
    }

    //Test SQS Property DLQ delay
    @Test
    public void testDLQDelay() {
        int delayCount= 30;
        SqsProperties instance = new SqsProperties();
        instance.setDlqdelay(delayCount);

        TestCase.assertEquals(Optional.of(instance.getDlqdelay()), Optional.ofNullable(delayCount));
    }

    //Test SQS Property DLQ delay is null
    @Test
    public void testDLQDelayIsNull() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqdelay(null);

        if (instance.getDlqdelay() == null) {
            instance.setDlqdelay(instance.getDelay());
        }

        TestCase.assertNull(instance.getDlqdelay());
    }

    //Test SQS Property dlqmaxmessagesize is null
    @Test
    public void testDLQMaxMessageSizeIsNull() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqmaxmessagesize(null);

        if (instance.getDlqmaxmessagesize() == null) {
            instance.setDlqmaxmessagesize(instance.getMaxmessagesize());
        }

        TestCase.assertNull(instance.getDlqmaxmessagesize());
    }

    //Test SQS Property dlqMessageRententionPeriod is null
    @Test
    public void testDLQMessageRententionPeriodIsNull() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqmessageretentionperiod(null);

        if (instance.getDlqmessageretentionperiod() == null) {
            instance.setDlqmessageretentionperiod(instance.getMessageretentionperiod());
        }

        TestCase.assertNull(instance.getDlqmessageretentionperiod());
    }

    //Test SQS Property dlqWaittime is null
    @Test
    public void testDLQWaitTimeIsNull() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqwaittime(null);

        if (instance.getDlqwaittime() == null) {
            instance.setDlqwaittime(instance.getWaittime());
        }

        TestCase.assertNull(instance.getDlqwaittime());
    }


    //Test SQS Property dlqVisibilityTimeout is null
    @Test
    public void testDLQVisibilityTimeoutIsNull() {
        SqsProperties instance = new SqsProperties();
        instance.setDlqvisibilitytimeout(null);

        if (instance.getDlqvisibilitytimeout() == null) {
            instance.setDlqvisibilitytimeout(instance.getVisibilitytimeout());
        }

        TestCase.assertNull(instance.getDlqvisibilitytimeout());
    }


    //Test SQS Property retries
    @Test
    public void testRetries() {
        int retryCount= 30;
        SqsProperties instance = new SqsProperties();
        instance.setRetries(retryCount);

        TestCase.assertEquals(Optional.of(instance.getRetries()), Optional.ofNullable(retryCount));
    }

    //Test SQS Queue Property Queue
    @Test
    public void testQueue() {
        String myqueue = "My_Queue";
        SqsQueueProperties instance = new SqsQueueProperties() {
            @Override
            public String getQueue() {
                return super.getQueue();
            }

            @Override
            public void setQueue(String queue) {
                super.setQueue(queue);
            }
        };
        instance.setQueue(myqueue);

        TestCase.assertEquals(Optional.of(instance.getQueue()), Optional.ofNullable(myqueue));
    }


    //Test SQS Queue Property DLQueue
    @Test
    public void testDLQueue() {
        String mydlqueue = "My_DLQueue";
        SqsQueueProperties instance = new SqsQueueProperties() {
            @Override
            public String getDeadletterqueue() {
                return super.getDeadletterqueue();
            }

            @Override
            public void setDeadletterqueue(String deadletterqueue) {
                super.setDeadletterqueue(deadletterqueue);
            }
        };
        instance.setDeadletterqueue(mydlqueue);

        TestCase.assertEquals(Optional.ofNullable(instance.getDeadletterqueue()), Optional.ofNullable(mydlqueue));
    }


    /**
     * Test of ContentBasedDuplication method, of class SqsQueueProperties.
     */
    @Test
    public void testQueueSetContentBasedDuplication() {
        boolean contentBased = false;
        SqsQueueProperties instance = new SqsQueueProperties() {
            @Override
            public Boolean getContentbasedduplication() {
                return super.getContentbasedduplication();
            }

            @Override
            public void setContentbasedduplication(Boolean contentbasedduplication) {
                super.setContentbasedduplication(contentBased);
            }
        };
        instance.setContentbasedduplication(contentBased);
        TestCase.assertEquals(Optional.of(instance.getContentbasedduplication()), Optional.ofNullable(contentBased));
    }
}
