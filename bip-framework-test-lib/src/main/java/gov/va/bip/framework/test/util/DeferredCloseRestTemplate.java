package gov.va.bip.framework.test.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

public class DeferredCloseRestTemplate extends RestTemplate {


    /**
     * Override this method to bypass closing the response when the response is a multipart/related message.
     * The closing will take place in a deferred manner in the MultipartHttpMessageConverter
     */
    @Override
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
                              @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

        boolean closeIt = true;

        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();

            if (response != null && response.getHeaders().getContentType() != null) {
                if ("multipart".equals(response.getHeaders().getContentType().getType()) &&
                    "related".equals(response.getHeaders().getContentType().getSubtype())) {
                    closeIt = false;
                }
            }
            handleResponse(url, method, response);
            return (responseExtractor != null ? responseExtractor.extractData(response) : null);
        }
        catch (IOException ex) {
            String resource = url.toString();
            String query = url.getRawQuery();
            resource = (query != null ? resource.substring(0, resource.indexOf('?')) : resource);
            throw new ResourceAccessException("I/O error on " + method.name() +
                " request for \"" + resource + "\": " + ex.getMessage(), ex);
        }
        finally {
            if (response != null && closeIt) {
                response.close();
            }
        }
    }
}
