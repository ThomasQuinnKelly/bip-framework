package gov.va.bip.framework.aws.config;

public class ConfigConstants {

    public enum aws_credentials {
        AWS_LOCALSTACK_REGION("us-east-1"),
        AWS_LOCALSTACK_ID("test-key"),
        AWS_LOCALSTACK_KEY("test-secret");

        private final String text;

        /**
         * @param text
         */
        aws_credentials(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

}
