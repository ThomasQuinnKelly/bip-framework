package gov.va.bip.framework.hystrix.autoconfigure;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class HystrixContextAwareConcurrencyStrategyTest {

	@Test
	public void testObjectCreation() {
		assertNotNull(new HystrixContextAwareConcurrencyStrategy(new ArrayList<HystrixCallableWrapper>()));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldWrapCallable() {

		@SuppressWarnings("rawtypes")
		final Callable callable = Mockito.mock(Callable.class);

		final HystrixContextAwareConcurrencyStrategy strategy = new HystrixContextAwareConcurrencyStrategy(
				Collections.<HystrixCallableWrapper> singletonList(new SimpleHystrixCallableWrapper()));

		assertNotEquals(callable, strategy.wrapCallable(callable));
	}

	private static class SimpleHystrixCallableWrapper implements HystrixCallableWrapper {

		@Override
		public <T> Callable<T> wrapCallable(final Callable<T> callable) {
			return new Callable<T>() {
				@Override
				public T call() throws Exception {
					return callable.call();
				}
			};
		}
	}

}