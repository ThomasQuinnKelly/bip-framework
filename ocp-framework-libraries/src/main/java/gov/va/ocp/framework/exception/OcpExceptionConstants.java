package gov.va.ocp.framework.exception;

public class OcpExceptionConstants {

	/** Server name the exception occurred on, as stored in System propery "server.name" */
	public static final String SERVER_NAME = System.getProperty("server.name");

	// This class is not to be instantiated since it only exits for constants
	private OcpExceptionConstants() {
	}


}
