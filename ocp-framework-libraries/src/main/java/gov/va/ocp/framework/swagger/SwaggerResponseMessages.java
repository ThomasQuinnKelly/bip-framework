package gov.va.ocp.framework.swagger;

/**
 * The Interface SwaggerResponseMessages contains response messages for documenting into
 * the swagger documentation. This is to ensure consistent messaging (and ideally behavior)
 * for services.
 *
 * @author akulkarn
 */
public interface SwaggerResponseMessages { // NOSONAR constants must be in this class
	/** The Constant MESSAGE_200 SUCCESS */
	static final String MESSAGE_200 =
			"A Response which indicates a successful Request.  The Response may contain \"messages\" that could describe warnings or further information.";

	/** The Constant MESSAGE_403 FORBIDDEN (not authorized) */
	static final String MESSAGE_403 = "The request is not authorized.  Please verify credentials used in the request.";

	/** The Constant MESSAGE_400 BAD REQUEST (some issue with the request) */
	static final String MESSAGE_400 =
			"There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  This request shouldn't be retried until corrected.";

	/** The Constant MESSAGE_500 INTERNAL SERVER ERROR (some issue in the server-side code) */
	static final String MESSAGE_500 =
			"There was an error encountered processing the Request.  Response will contain a  \"messages\" element that will provide further information on the error.  Please retry.  If problem persists, please contact support with a copy of the Response.";

}