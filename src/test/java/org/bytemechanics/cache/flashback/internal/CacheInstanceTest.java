/*
 * Copyright 2021 Byte Mechanics.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bytemechanics.cache.flashback.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * @author afarre
 */
public class CacheInstanceTest {
	
	@BeforeAll
	public static void setup() throws IOException{
		try(InputStream inputStream = CacheInstanceTest.class.getResourceAsStream("/logging.properties")){
			LogManager.getLogManager().readConfiguration(inputStream);
		}catch (final IOException e){
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}
	@BeforeEach
    void beforeEachTest(final TestInfo testInfo) {
        System.out.println(">>>>> "+this.getClass().getSimpleName()+" >>>> "+testInfo.getTestMethod().map(Method::getName).orElse("Unkown")+""+testInfo.getTags().toString()+" >>>> "+testInfo.getDisplayName());
    }

	/**
	 * Test of getValue method, of class CacheInstance.
	 */
	@Test
	public void testGetValue() {
		final CacheInstance instance = new CacheInstance("my-val");
		Assertions.assertEquals("my-val", instance.getValue());
	}

	/**
	 * Test of hashCode method, of class CacheRegistry.
	 */
	@Test
	public void testHashCode() {
		CacheInstance instance = new CacheInstance("my-val");
		CacheInstance instance2 = new CacheInstance("my-val");
		CacheInstance instance3 = new CacheInstance("my-val2");
		Assertions.assertEquals(instance.hashCode(), instance2.hashCode());
		Assertions.assertNotEquals(instance.hashCode(), instance3.hashCode());
	}
	/**
	 * Test of equals method, of class CacheRegistry.
	 */
	@Test
	public void testEquals() {
		CacheInstance instance = new CacheInstance("my-val");
		CacheInstance instance2 = new CacheInstance("my-val");
		CacheInstance instance3 = new CacheInstance("my-val2");
		Assertions.assertTrue(instance.equals(instance2));
		Assertions.assertFalse(instance.equals(instance3));
	}
	
	/**
	 * Test of cast method, of class CacheInstance.
	 */
	@Test
	public void testCast() {
		final CacheInstanceAdapter instance = new CacheInstance("my-val");
		CacheInstance castCache=CacheInstance.cast(instance);
		Assertions.assertEquals("my-val", castCache.getValue());
	}
	
	/**
	 * Test of cast method, of class CacheInstance.
	 */
	@Test
	public void testToString() {
		final CacheInstanceAdapter instance = new CacheInstance("my-val");
		Assertions.assertEquals("CacheInstance[value=my-val]", instance.toString());
	}
}
