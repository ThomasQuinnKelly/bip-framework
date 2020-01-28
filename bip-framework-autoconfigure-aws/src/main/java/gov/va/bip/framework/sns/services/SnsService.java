package gov.va.bip.framework.sns.services;

import gov.va.bip.framework.sqs.dto.SendMessageResponse;

import javax.jms.Message;
import javax.jms.TextMessage;

public interface SnsService {

    /**
     * Create a TextMessage
     *
     * @param message
     * @return returns a TextMessage
     */
    public TextMessage createTextMessage(String message);

    /**
     * Send a Message
     *
     * @param message
     * @return returns a JMS ID
     */
    public SendMessageResponse sendMessage(Message message);

//    /**
//     * Creates a Queue
//     *
//     * @param createQueueRequest
//     * @return returns a CreateQueueResult
//     */
//    public CreateQueueResult createQueue(CreateQueueRequest createQueueRequest);
//
//    /**
//	 * Creates a Queue from a string version of the queue name
//	 *
//	 * @param queueName
//	 * @return returns a CreateQueueResult
//	 */
//	public CreateQueueResult createQueue(String queueName);
//
//    /**
//     * Returns the list of all created and available Queues
//     *
//     * @return returns a ListQueuesResult object which contains a list of queueUrls
//     */
//    public ListQueuesResult listQueues();
//
//  //  public Message receiveMessage(ReceiveMessageRequest receiveMessageRequest);
//
//    public Message receiveMessage(String queueUrl);
//
//
//    //print the SQS queues
////		System.out.println("SQS Queue: " +sqsClient.listQueues());
////
////		sqsClient.receiveMessage(queueURL).getMessages().forEach(System.out::println);
////	SendMessageResult sendMessage(SendMessageRequest sendMessageRequest);
////	SendMessageResult sendMessage(String queueUrl, String messageBody);

}


//	String ENDPOINT_PREFIX = "sqs";
//
//	AddPermissionResult addPermission(AddPermissionRequest addPermissionRequest);
//	AddPermissionResult addPermission(String queueUrl, String label, java.util.List<String> aWSAccountIds, java.util.List<String> actions);
//	ChangeMessageVisibilityResult changeMessageVisibility(ChangeMessageVisibilityRequest changeMessageVisibilityRequest);
//	ChangeMessageVisibilityResult changeMessageVisibility(String queueUrl, String receiptHandle, Integer visibilityTimeout);
//	ChangeMessageVisibilityBatchResult changeMessageVisibilityBatch(ChangeMessageVisibilityBatchRequest changeMessageVisibilityBatchRequest);
//	ChangeMessageVisibilityBatchResult changeMessageVisibilityBatch(String queueUrl, java.util.List<ChangeMessageVisibilityBatchRequestEntry> entries);
//	CreateQueueResult createQueue(CreateQueueRequest createQueueRequest);
//	CreateQueueResult createQueue(String queueName);
//	DeleteMessageResult deleteMessage(DeleteMessageRequest deleteMessageRequest);
//	DeleteMessageResult deleteMessage(String queueUrl, String receiptHandle);
//	DeleteMessageBatchResult deleteMessageBatch(DeleteMessageBatchRequest deleteMessageBatchRequest);
//	DeleteMessageBatchResult deleteMessageBatch(String queueUrl, java.util.List<DeleteMessageBatchRequestEntry> entries);
//	DeleteQueueResult deleteQueue(DeleteQueueRequest deleteQueueRequest);
//	DeleteQueueResult deleteQueue(String queueUrl);
//	GetQueueAttributesResult getQueueAttributes(GetQueueAttributesRequest getQueueAttributesRequest);
//	GetQueueAttributesResult getQueueAttributes(String queueUrl, java.util.List<String> attributeNames);
//	GetQueueUrlResult getQueueUrl(GetQueueUrlRequest getQueueUrlRequest);
//	GetQueueUrlResult getQueueUrl(String queueName);
//	ListDeadLetterSourceQueuesResult listDeadLetterSourceQueues(ListDeadLetterSourceQueuesRequest listDeadLetterSourceQueuesRequest);
//	ListQueueTagsResult listQueueTags(ListQueueTagsRequest listQueueTagsRequest);
//	ListQueueTagsResult listQueueTags(String queueUrl);
//	ListQueuesResult listQueues(ListQueuesRequest listQueuesRequest);
//	ListQueuesResult listQueues();
//	ListQueuesResult listQueues(String queueNamePrefix);
//	PurgeQueueResult purgeQueue(PurgeQueueRequest purgeQueueRequest);
//	ReceiveMessageResult receiveMessage(ReceiveMessageRequest receiveMessageRequest);
//	ReceiveMessageResult receiveMessage(String queueUrl);
//	RemovePermissionResult removePermission(RemovePermissionRequest removePermissionRequest);
//	RemovePermissionResult removePermission(String queueUrl, String label);
//	SendMessageResult sendMessage(SendMessageRequest sendMessageRequest);
//	SendMessageResult sendMessage(String queueUrl, String messageBody);
//	SendMessageBatchResult sendMessageBatch(SendMessageBatchRequest sendMessageBatchRequest);
//	SendMessageBatchResult sendMessageBatch(String queueUrl, java.util.List<SendMessageBatchRequestEntry> entries);
//	SetQueueAttributesResult setQueueAttributes(SetQueueAttributesRequest setQueueAttributesRequest);
//	SetQueueAttributesResult setQueueAttributes(String queueUrl, java.util.Map<String, String> attributes);
//	TagQueueResult tagQueue(TagQueueRequest tagQueueRequest);
//	TagQueueResult tagQueue(String queueUrl, java.util.Map<String, String> tags);
//	UntagQueueResult untagQueue(UntagQueueRequest untagQueueRequest);
//	UntagQueueResult untagQueue(String queueUrl, java.util.List<String> tagKeys);
//	void shutdown();
//	ResponseMetadata getCachedResponseMetadata(AmazonWebServiceRequest request);