/**
 * Copyright (c) 2015-2016 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Code has been borrowed from https://github.com/jmnarloch/hystrix-context-spring-boot-starter
 * 
 * Updates were made to HystrixContextAutoConfiguration program file
 *
 */
package gov.va.bip.framework.hystrix.autoconfigure;

import java.util.concurrent.Callable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * The Class RequestAttributeAwareCallableWrapper.
 */
@Component
public class RequestAttributeAwareCallableWrapper implements HystrixCallableWrapper {
 
    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        return new RequestAttributeAwareCallable<>(callable, RequestContextHolder.currentRequestAttributes());
    }
 
    private static class RequestAttributeAwareCallable<T> implements Callable<T> {
 
        private final Callable<T> callable;
        private final RequestAttributes requestAttributes;
 
        public RequestAttributeAwareCallable(Callable<T> callable, RequestAttributes requestAttributes) {
            this.callable = callable;
            this.requestAttributes = requestAttributes;
        }
 
        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return callable.call();
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}