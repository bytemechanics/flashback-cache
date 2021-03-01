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
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
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
public class TTLCacheInstanceTest {
	
	@BeforeAll
	public static void setup() throws IOException{
		try(InputStream inputStream = TTLCacheInstanceTest.class.getResourceAsStream("/logging.properties")){
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
	 * Test of getExpire method, of class TTLCacheInstance.
	 */
	@Test
	public void testGetExpire() {
		Duration duration=Duration.of(1l,ChronoUnit.MINUTES);
		Instant expected=Instant.now().plus(duration);
		TTLCacheInstance instance = new TTLCacheInstance("my-value", duration);
		Assertions.assertEquals(expected.getLong(ChronoField.INSTANT_SECONDS),instance.getExpire().getLong(ChronoField.INSTANT_SECONDS));
	}

	/**
	 * Test of isAlive method, of class TTLCacheInstance.
	 */
	@Test
	public void testIsAlive() throws InterruptedException {
		TTLCacheInstance instance = new TTLCacheInstance("my-value", Duration.of(1l,ChronoUnit.SECONDS));
		Assertions.assertTrue(instance.isAlive());
		Thread.sleep(2000l);
		Assertions.assertFalse(instance.isAlive());
	}

	/**
	 * Test of cast method, of class TTLCacheInstance.
	 */
	@Test
	public void testCast() {
		final CacheInstanceAdapter instance = new TTLCacheInstance("my-value", Duration.of(1l,ChronoUnit.MINUTES));
		TTLCacheInstance castCache=TTLCacheInstance.cast(instance);
		Assertions.assertEquals("my-value", castCache.getValue());
	}

	/**
	 * Test of cast method, of class CacheInstance.
	 */
	@Test
	public void testToString() {
		Duration duration=Duration.of(1l,ChronoUnit.MINUTES);
		Instant expected=Instant.now().plus(duration);
		final CacheInstanceAdapter instance = new TTLCacheInstance("my-val",expected);
		Assertions.assertEquals("TTLCacheInstance[value=my-val, expire="+expected+"]", instance.toString());
	}
}
