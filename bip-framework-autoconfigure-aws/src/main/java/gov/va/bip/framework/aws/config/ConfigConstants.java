package gov.va.bip.framework.aws.config;

public class ConfigConstants {

    private ConfigConstants(){

    }

        private static final String AWS_LOCALSTACK_ID = "test-key";
        private static final String AWS_LOCALSTACK_KEY = "test-secret";

    public static String getAwsLocalstackId() {
        return AWS_LOCALSTACK_ID;
        }

    public static String getAwsLocalstackKey() {
        return AWS_LOCALSTACK_KEY;
        }
    }
