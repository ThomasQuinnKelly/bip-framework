/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.bip.framework.constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;

import org.junit.Test;

import gov.va.bip.framework.constants.BipConstants;

/**
 *
 * @author rthota
 */
public class BipConstantsTest {
	public static final String UNCHECKED = "unchecked";

	public static String getUnchecked() {
		return UNCHECKED;
	}

	@Test
	public void annotationConstantsTest() throws Exception {
		assertEquals(BipConstants.UNCHECKED, getUnchecked());
	}

	@Test
	public void annotationConstantsConstructor() throws Exception {
		Constructor<BipConstants> constructor = BipConstants.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		try {
			constructor.newInstance();
			fail("Should have thrown exception");
		} catch (Exception e) {
			assertTrue(java.lang.reflect.InvocationTargetException.class.isAssignableFrom(e.getClass()));
			assertTrue(java.lang.IllegalStateException.class.isAssignableFrom(e.getCause().getClass()));
		}
	}
}
