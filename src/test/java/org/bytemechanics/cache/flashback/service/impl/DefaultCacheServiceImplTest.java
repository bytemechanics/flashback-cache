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
package org.bytemechanics.cache.flashback.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.CacheKeyUndefinedException;
import org.bytemechanics.cache.flashback.CacheRegistryAdapter;
import org.bytemechanics.cache.flashback.internal.CacheRegistry;
import org.bytemechanics.cache.flashback.internal.TTLCacheRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 *
 * @author afarre
 */
public class DefaultCacheServiceImplTest {

	@BeforeAll
	public static void setup() throws IOException {
		try ( InputStream inputStream = DefaultCacheServiceImplTest.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}
	@BeforeEach
	void beforeEachTest(final TestInfo testInfo) {
		System.out.println(">>>>> " + this.getClass().getSimpleName() + " >>>> " + testInfo.getTestMethod().map(Method::getName).orElse("Unkown") + "" + testInfo.getTags().toString() + " >>>> " + testInfo.getDisplayName());
	}

	
	/**
	 * Test of registerCache method, of class DefaultCacheServiceImpl.
	 */
	@Test
	public void testRegisterCache() {
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
			
		CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",() -> 2+2);
		String result = instance.registerCache(registryInstance);
		Assertions.assertAll(() -> Assertions.assertEquals("my-cache", result)
									,() -> Assertions.assertTrue(registry.containsKey("my-cache"))
									,() -> Assertions.assertEquals(registryInstance,registry.get("my-cache")));
	}

	/**
	 * Test of get method, of class DefaultCacheServiceImpl.
	 */
	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testGet() {
		AtomicInteger seed=new AtomicInteger(2);
		final CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",seed::incrementAndGet);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		registry.put(registryInstance.getKey(), registryInstance);
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
		
		Assertions.assertAll(() ->Assertions.assertEquals(3, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(3, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(3, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(3, instance.get("my-cache",Integer.class)));
	}
	/**
	 * Test of get method, of class DefaultCacheServiceImpl.
	 */
	@Test
	@SuppressWarnings("ThrowableResultIgnored")
	public void testGet_unkown() {
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl();
		Assertions.assertThrows(CacheKeyUndefinedException.class
										, () -> instance.get("my-cache",Integer.class));
	}

	/**
	 * Test of expire method, of class DefaultCacheServiceImpl.
	 */
	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testExpire() {
		AtomicInteger seed=new AtomicInteger(2);
		final CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",seed::incrementAndGet);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		registry.put(registryInstance.getKey(), registryInstance);
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);

		Assertions.assertEquals(3, instance.get("my-cache",Integer.class));
		String result=instance.expire("my-cache");
		Assertions.assertAll(() -> Assertions.assertEquals("my-cache", result)
									,() -> Assertions.assertEquals(4, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(4, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(4, instance.get("my-cache",Integer.class))
									,() -> Assertions.assertEquals(4, instance.get("my-cache",Integer.class))
									);
	}
	/**
	 * Test of expire method, of class DefaultCacheServiceImpl.
	 */
	@Test
	@SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes", "ThrowableResultIgnored"})
	public void testExpire_unkwown() {
		AtomicInteger seed=new AtomicInteger(2);
		final CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",seed::incrementAndGet);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		registry.put(registryInstance.getKey(), registryInstance);
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
		Assertions.assertThrows(CacheKeyUndefinedException.class
										, () -> instance.get("my-cache2",Integer.class));
	}

	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testRefresh() {
		AtomicInteger seed=new AtomicInteger(2);
		final CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",seed::incrementAndGet);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		registry.put(registryInstance.getKey(), registryInstance);
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
		
		Assertions.assertAll(() -> Assertions.assertEquals(3, instance.refresh("my-cache",Integer.class))
									,() -> Assertions.assertEquals(4, instance.refresh("my-cache",Integer.class))
									,() -> Assertions.assertEquals(5, instance.refresh("my-cache",Integer.class))
									,() -> Assertions.assertEquals(6, instance.refresh("my-cache",Integer.class))
									);
	}
	@Test
	@SuppressWarnings({"AssertEqualsBetweenInconvertibleTypes", "ThrowableResultIgnored"})
	public void testRefresh_unknown() {
		AtomicInteger seed=new AtomicInteger(2);
		final CacheRegistryAdapter registryInstance = new CacheRegistry("my-cache",seed::incrementAndGet);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		registry.put(registryInstance.getKey(), registryInstance);
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
		Assertions.assertThrows(CacheKeyUndefinedException.class
										, () -> instance.refresh("my-cache2",Integer.class));
	}

	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testFrom_parts() {
		AtomicInteger seed=new AtomicInteger(2);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
			
		Assertions.assertAll(() -> Assertions.assertEquals(3, instance.from(seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(3, instance.get("this.is.my.cache",Integer.class))
									,() -> Assertions.assertEquals(3, instance.from(seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(1, instance.registry.size())
									,() -> Assertions.assertEquals(3, instance.from("this.is.my.cache",seed::incrementAndGet))
									);
	}
	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testFrom_parts_unknown() {
		AtomicInteger seed=new AtomicInteger(2);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
			
		Assertions.assertAll(() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(seed::incrementAndGet))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(seed::incrementAndGet," "," "," ",""))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(seed::incrementAndGet," "))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from("     ",seed::incrementAndGet))
									);
	}

	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testFrom_parts_ttl() throws InterruptedException {
		AtomicInteger seed=new AtomicInteger(2);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
			
		Assertions.assertAll(() -> Assertions.assertEquals(3, instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(3, instance.get("this.is.my.cache",Integer.class))
									,() -> Assertions.assertEquals(3, instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(1, instance.registry.size())
									,() -> Assertions.assertEquals(3, instance.from("this.is.my.cache",1l,ChronoUnit.SECONDS,seed::incrementAndGet)));
		Thread.sleep(2000l);
		Assertions.assertAll(() -> Assertions.assertEquals(4, instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(4, instance.get("this.is.my.cache",Integer.class))
									,() -> Assertions.assertEquals(4, instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet,"this","is","my","cache"))
									,() -> Assertions.assertEquals(1, instance.registry.size())
									,() -> Assertions.assertEquals(4, instance.from("this.is.my.cache",1l,ChronoUnit.SECONDS,seed::incrementAndGet)));
	}

	@Test
	@SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
	public void testFrom_parts_ttl_unknown() throws InterruptedException {
		AtomicInteger seed=new AtomicInteger(2);
		final ConcurrentMap<String,CacheRegistryAdapter> registry=new ConcurrentHashMap<>();
		final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> store=new ConcurrentHashMap<>();
		final DefaultCacheServiceImpl instance=new DefaultCacheServiceImpl(registry, store);
			
		Assertions.assertAll(() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet," "," "," ",""))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from(1l,ChronoUnit.SECONDS,seed::incrementAndGet," "))
									,() -> Assertions.assertThrows(CacheKeyUndefinedException.class, () -> instance.from("     ",1l,ChronoUnit.SECONDS,seed::incrementAndGet))
									);
	}	
	/**
	 * Test of validOrNew method, of class DefaultCacheServiceImpl.
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testValidOrNew() throws InterruptedException {
		AtomicInteger seed=new AtomicInteger(2);
		CacheRegistryAdapter cacheRegistry = new TTLCacheRegistry("my-cache",seed::incrementAndGet,Duration.of(1l,ChronoUnit.SECONDS));
		CacheInstanceAdapter cacheInstance = cacheRegistry.supplyInstance();
		
		Assertions.assertEquals(cacheInstance, DefaultCacheServiceImpl.validOrNew(cacheRegistry, cacheInstance));
		Thread.sleep(2000l);
		Assertions.assertNotEquals(cacheInstance, DefaultCacheServiceImpl.validOrNew(cacheRegistry, cacheInstance));
	}
}
