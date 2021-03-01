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
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.TestInfo;

/**
 *
 * @author afarre
 */
public class TTLCacheRegistryTest {
	
	@BeforeAll
	public static void setup() throws IOException{
		try(InputStream inputStream = TTLCacheRegistryTest.class.getResourceAsStream("/logging.properties")){
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
	 * Test of getTimeToLife method, of class TTLCacheRegistry.
	 */
	@Test
	public void testGetTimeToLife() {
		Duration expResult = Duration.of(1l,ChronoUnit.MINUTES);
		TTLCacheRegistry instance = new TTLCacheRegistry("my-key", () -> 2+2, expResult);
		assertEquals(expResult, instance.getTimeToLife());
	}

	/**
	 * Test of supplyInstance method, of class TTLCacheRegistry.
	 */
	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testSupplyInstance() {
		AtomicInteger val=new AtomicInteger(2);
		Supplier<Integer> supplier=() -> val.incrementAndGet();
		Duration expResult = Duration.of(1l,ChronoUnit.MINUTES);
		TTLCacheRegistry instance = new TTLCacheRegistry("my-key", supplier, expResult);
		CacheInstanceAdapter cacheInstance=instance.supplyInstance();
		Assertions.assertTrue(cacheInstance instanceof TTLCacheInstance);
		Assertions.assertEquals(3, cacheInstance.getValue());
		Assertions.assertEquals(4, instance.supplyInstance().getValue());
	}

	/**
	 * Test of isValid method, of class TTLCacheRegistry.
	 */
	@Test
	public void testIsValid() throws InterruptedException {
		Duration duration = Duration.of(1l,ChronoUnit.SECONDS);
		TTLCacheRegistry instance = new TTLCacheRegistry("my-key", () -> 2+2, duration);
		TTLCacheInstance cacheInstance=(TTLCacheInstance)instance.supplyInstance();
		Assertions.assertTrue(instance.isValid(cacheInstance));
		Thread.sleep(2000l);
		Assertions.assertFalse(instance.isValid(cacheInstance));
	}
	
	/**
	 * Test of cast method, of class CacheInstance.
	 */
	@Test
	public void testToString() {
		Duration duration=Duration.of(1l,ChronoUnit.MINUTES);
		Supplier supplier=() -> 2+2;
		final TTLCacheRegistry instance = new TTLCacheRegistry("my-val", supplier,duration);
		Assertions.assertEquals("CacheRegistry[key=my-val, timeToLife="+duration+", supplier="+supplier+"]", instance.toString());
	}	
}
