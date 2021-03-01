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
package org.bytemechanics.cache.flashback.service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bytemechanics.cache.flashback.CacheRegistryAdapter;
import org.bytemechanics.cache.flashback.CacheKeyUndefinedException;
import org.bytemechanics.cache.flashback.internal.CacheRegistry;

/**
 * Cache Service adapter to manage and store caches
 * In order to use this in Spring, please create your own implementation and annotate it or extend DefaultCacheServiceImpl 
 * @see DefaultCacheServiceimpl
 * @author afarre
 */
public interface CacheService {

	/** Default cache initial size */
	public static final String DEFAULT_CACHE_SIZE = "100";
	/** Default cache initial system property should be modified */
	public static final String EXPECTED_CACHE_SIZE_PROPERTY = "org.bytemechanics.flashback.expected.cache.size";

	/**
	 * Register new cache in the service store
	 * @param _cacheRegistry cache registry to register
	 * @return registered cache key
	 */
	public String registerCache(final CacheRegistryAdapter _cacheRegistry);
	/**
	 * Recover the cached value or supply new value (put in cache and return it)
	 * @param _key cache key
	 * @return Cache value as object
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 */
	public Object get(final String _key);
	/**
	 * Recover the cached value or supply new value (put in cache and return it) casting to the given class.
	 * @param <T> Cache value type
	 * @param _key cache key
	 * @param _class cache object class
	 * @return Cache value casted as the given _class
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 */
	@SuppressWarnings("unchecked")
	public default <T> T get(final String _key,final Class<T> _class){
		return (T)CacheService.this.get(_key);
	}
	/**
	 * Expire cache invalidating its current value if any
	 * @param _key cache key
	 * @return cache key expired or null if not exist
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 */
	public String expire(final String _key);
	/**
	 * Refresh the current cache value by calling expire() and getCacheValue() returning the new refreshed instance or null if the supplier return null
	 * @param _key cache key
	 * @return the new fresh cache value or null if the supplier return null
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 */
	public default Object refresh(final String _key){
		return Optional.ofNullable(_key)
							.map(this::expire)
							.map(this::get)
							.orElseThrow(CacheKeyUndefinedException::new);
	}
	/**
	 * Refresh the current cache value by calling expire() and getCacheValue() returning the new refreshed instance or null if the supplier return null casting to the given class.
	 * @param <T> Cache value type
	 * @param _key cache key
	 * @param _class cache object class
	 * @return Cthe new fresh cache value or null if the supplier return null cast to the given class
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 */
	@SuppressWarnings("unchecked")
	public default <T> T refresh(final String _key,final Class<T> _class){
		return (T)refresh(_key);
	}
	
	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * @param <T> cache value type
	 * @param _key cache key
	 * @param _supplier cache supplier
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	@SuppressWarnings("unchecked")
	public default <T> T from(final String _key,final Supplier<T> _supplier){
		return Optional.ofNullable(_key)
							.map(String::trim)
							.filter(key -> !key.isEmpty())
							.map(key -> registerCache(new CacheRegistry(key,_supplier)))
							.map(this::get)
							.map(value -> (T)value)
							.orElseThrow(CacheKeyUndefinedException::new);
	}	
	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * Example: _keyparts[]{mypart1,part2} will generate key mypart1.part2
	 * @param <T> cache value type
	 * @param _supplier cache supplier
	 * @param _keyParts cache key parts that will conform the key separated by dot
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	public default <T> T from(final Supplier<T> _supplier,final String... _keyParts){
		return Stream.of(_keyParts)
							.map(String::trim)
							.filter(keyPart -> !keyPart.isEmpty())
							.reduce((part1,part2) -> String.join(".",part1,part2))
								.map(key -> CacheService.this.from(key,_supplier))
								.orElseThrow(CacheKeyUndefinedException::new);
	}
}
