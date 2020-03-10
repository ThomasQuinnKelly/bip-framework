package gov.va.bip.framework.test.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.client.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a variant of the {@link RestTemplate} which can allow for certain requests handled by this class to
 * not be automatically closed after the doExecute() function is called.
 *
 * The {@link RestTemplate} by design will always close a response stream once all
 * {@link org.springframework.http.converter.HttpMessageConverter}s have been executed against the response, and the
 * response is being returned. In some cases, multi-part responses will want to leave the input stream associated with
 * them open, for future use by other non-blocking threads (e.g. if you want to spin-off a new thread to complete a
 * download of a large file).
 *
 * To use this class, instantiate it with a list of {@link MediaType} objects to define which {@link MediaType}s you
 * want this {@link RestTemplate} to NOT close for you after processing. If the processed
 * response.getHeaders().getContentType() matches one of the deferredCloseMediaTypes, the response will be processed
 * normally, with the response stream NOT closed by this class. Users of this class will need to manually handle the
 * closing and cleanup of these latent streams on their own. This class will function exactly like a
 * regular {@link RestTemplate} class otherwise.
 */
public class DeferredCloseRestTemplate extends RestTemplate {

    private List<MediaType> deferredCloseMediaTypes = new ArrayList<>();

    /**
     * The constructor to instantiate this object with the list of {@link MediaType}s which it should defer the closing
     * of response streams that match one of these types.
     *
     * @param deferredCloseMediaTypes
     *          A list of {@link MediaType} objects which this class should NOT close the stream of after processing.
     */
    public DeferredCloseRestTemplate(List<MediaType> deferredCloseMediaTypes) {
        if(deferredCloseMediaTypes != null) {
            this.deferredCloseMediaTypes = deferredCloseMediaTypes;
        }
    }

    /**
     * Override this method to bypass closing the response when the response content type is listed in the original
     * deferredCloseMediaTypes list used to instantiate this object.
     * You will be responsible for tracking and closing this stream when you are done with it! This class does not
     * handle tracking or cleaning up any request streams that are left open after this call.
     */
    @Override
    protected <T> T doExecute(URI url, @Nullable HttpMethod method, @Nullable RequestCallback requestCallback,
                              @Nullable ResponseExtractor<T> responseExtractor) throws RestClientException {

        Assert.notNull(url, "URI is required");
        Assert.notNull(method, "HttpMethod is required");
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = createRequest(url, method);
            if (requestCallback != null) {
                requestCallback.doWithRequest(request);
            }
            response = request.execute();
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
            if (response != null && !deferredCloseMediaTypes.contains(response.getHeaders().getContentType())) {
                response.close();
            }
        }
    }
}
