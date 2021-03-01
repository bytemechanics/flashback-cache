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

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bytemechanics.cache.flashback.CacheKeyUndefinedException;
import org.bytemechanics.cache.flashback.internal.TTLCacheRegistry;

/**
 * Extends Cache Service adapter in order to provide some utility methods to create time-to-life caches
 * In order to use this in Spring, please create your own implementation and annotate it or extend DefaultCacheServiceImpl 
 * @see DefaultCacheServiceimpl
 * @author afarre
 */
public interface TTLCacheService extends CacheService {

	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * @param <T> cache value type
	 * @param _key cache key
	 * @param _timeToLife cache lifespan as Duration object
	 * @param _supplier cache supplier
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	@SuppressWarnings("unchecked")
	public default <T> T from(final String _key,final Duration _timeToLife,final Supplier<T> _supplier){
		return Optional.ofNullable(_key)
							.map(String::trim)
							.filter(key -> !key.isEmpty())
							.map(key -> registerCache(new TTLCacheRegistry(key,_supplier,_timeToLife)))
							.map(this::get)
							.map(value -> (T)value)
							.orElseThrow(CacheKeyUndefinedException::new);
	}	
	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * @param <T> cache value type
	 * @param _key cache key
	 * @param _timeToLifeAmount cache lifespan amount
	 * @param _timeToLifeUnits cache lifespan time units
	 * @param _supplier cache supplier
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	public default <T> T from(final String _key,final long _timeToLifeAmount,final TemporalUnit _timeToLifeUnits,final Supplier<T> _supplier){
		return TTLCacheService.this.from(_key, Duration.of(_timeToLifeAmount, _timeToLifeUnits), _supplier);
	}
	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * @param <T> cache value type
	 * @param _timeToLife cache lifespan as Duration object
	 * @param _supplier cache supplier
	 * @param _keyParts cache key parts that will conform the key separated by dot
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	public default <T> T from(final Duration _timeToLife,final Supplier<T> _supplier,final String... _keyParts){
		return Stream.of(_keyParts)
							.map(String::trim)
							.filter(keyPart -> !keyPart.isEmpty())
							.reduce((part1,part2) -> String.join(".",part1,part2))
								.map(key -> TTLCacheService.this.from(key,_timeToLife,_supplier))
								.orElseThrow(CacheKeyUndefinedException::new);
	}
	/**
	 * Utility method to define register new cache if not exist, supply if not exist or expired and return a cached value
	 * Example: _keyparts[]{mypart1,part2} will generate key mypart1.part2
	 * @param <T> cache value type
	 * @param _timeToLifeAmount cache lifespan amount
	 * @param _timeToLifeUnits cache lifespan time units
	 * @param _supplier cache supplier
	 * @param _keyParts cache key parts that will conform the key separated by dot
	 * @return the cached value
	 * @throws CacheKeyUndefinedException key has not been provided or is empty
	 */
	public default <T> T from(final long _timeToLifeAmount,final TemporalUnit _timeToLifeUnits,final Supplier<T> _supplier,final String... _keyParts){
		return TTLCacheService.this.from( Duration.of(_timeToLifeAmount, _timeToLifeUnits),_supplier,_keyParts);
	}
}
