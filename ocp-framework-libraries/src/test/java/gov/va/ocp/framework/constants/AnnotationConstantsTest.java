/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.va.ocp.framework.constants;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;

import org.junit.Test;

import gov.va.ocp.framework.constants.AnnotationConstants;

/**
 *
 * @author rthota
 */
public class AnnotationConstantsTest {
	public static final String UNCHECKED = "unchecked";

	@Test
	public void annotationConstantsTest() throws Exception {
		assertEquals(UNCHECKED, AnnotationConstants.UNCHECKED);
	}

	@Test
	public void annotationConstantsConstructor() throws Exception {
		Constructor<AnnotationConstants> constructor = AnnotationConstants.class.getDeclaredConstructor();
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
