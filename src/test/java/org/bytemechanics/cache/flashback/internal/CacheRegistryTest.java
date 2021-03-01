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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 *
 * @author afarre
 */
public class CacheRegistryTest {
	
	@BeforeAll
	public static void setup() throws IOException{
		try(InputStream inputStream = CacheRegistryTest.class.getResourceAsStream("/logging.properties")){
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
	 * Test of getKey method, of class CacheRegistry.
	 */
	@Test
	public void testGetKey() {
		CacheRegistry instance = new CacheRegistry("my-key", () -> 2+2);
		Assertions.assertEquals("my-key", instance.getKey());
	}

	/**
	 * Test of getCacheSupplier method, of class CacheRegistry.
	 */
	@Test
	public void testGetCacheSupplier() {
		Supplier<Integer> supplier=() -> 2+2;
		CacheRegistry instance = new CacheRegistry("my-key", supplier);
		Assertions.assertEquals(supplier, instance.getSupplier());
	}

	/**
	 * Test of supplyInstance method, of class CacheRegistry.
	 */
	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testSupplyInstance() {
		AtomicInteger val=new AtomicInteger(2);
		Supplier<Integer> supplier=() -> val.incrementAndGet();
		CacheRegistry instance = new CacheRegistry("my-key", supplier);
		CacheInstanceAdapter cacheInstance=instance.supplyInstance();
		Assertions.assertTrue(cacheInstance instanceof CacheInstance);
		Assertions.assertEquals(3, cacheInstance.getValue());
		Assertions.assertEquals(4, instance.supplyInstance().getValue());
	}

	/**
	 * Test of hashCode method, of class CacheRegistry.
	 */
	@Test
	public void testHashCode() {
		CacheRegistry instance = new CacheRegistry("my-key", () -> 2+2);
		CacheRegistry instance2 = new CacheRegistry("my-key", () -> "a");
		CacheRegistry instance3 = new CacheRegistry("my-key2", () -> "a");
		Assertions.assertEquals(instance.hashCode(), instance2.hashCode());
		Assertions.assertNotEquals(instance.hashCode(), instance3.hashCode());
	}

	/**
	 * Test of equals method, of class CacheRegistry.
	 */
	@Test
	public void testEquals() {
		CacheRegistry instance = new CacheRegistry("my-key", () -> 2+2);
		CacheRegistry instance2 = new CacheRegistry("my-key", () -> "a");
		CacheRegistry instance3 = new CacheRegistry("my-key2", () -> "a");
		Assertions.assertTrue(instance.equals(instance2));
		Assertions.assertFalse(instance.equals(instance3));
	}

	/**
	 * Test of cast method, of class CacheInstance.
	 */
	@Test
	public void testToString() {
		Supplier supplier=() -> 2+2;
		final CacheRegistry instance = new CacheRegistry("my-val", supplier);
		Assertions.assertEquals("CacheRegistry[key=my-val, supplier="+supplier+"]", instance.toString());
	}
}
