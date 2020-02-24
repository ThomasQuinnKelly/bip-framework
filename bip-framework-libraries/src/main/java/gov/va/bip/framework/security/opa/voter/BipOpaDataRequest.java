package gov.va.bip.framework.security.opa.voter;

import java.util.Map;

/**
 * The Class BipOpaDataRequest.
 */
public class BipOpaDataRequest {

    /** The input. */
    Map<String, Object> input;

    /**
     * Instantiates a new bip opa data request.
     *
     * @param input the input
     */
    public BipOpaDataRequest(Map<String, Object> input) {
        this.input = input;
    }

    /**
     * Gets the input.
     *
     * @return the input
     */
    public Map<String, Object> getInput() {
        return this.input;
    }

}
