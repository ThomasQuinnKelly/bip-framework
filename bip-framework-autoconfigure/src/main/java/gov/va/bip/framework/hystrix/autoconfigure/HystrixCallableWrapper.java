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

@FunctionalInterface
public interface HystrixCallableWrapper {

	/**
	 * Wraps the passed callable instance.
	 *
	 * @param callable the callable to wrap
	 * @param <T>      the result type
	 * @return the wrapped callable
	 */
	<T> Callable<T> wrapCallable(Callable<T> callable);

}
