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

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.CacheRegistryAdapter;
import org.bytemechanics.cache.flashback.internal.commons.string.SimpleFormat;

/**
 * Cache memory registry
 * @author afarre
 */
public class TTLCacheRegistry extends CacheRegistry {
	
	/** Cache lifespan */
	private final Duration timeToLife;
	
	
	/**
	 * Constructor of time to life cache
	 * @param _key cache key
	 * @param _supplier cache value supplier
	 * @param _timeToLife cache lifespan
	 */
	public TTLCacheRegistry(final String _key,final Supplier _supplier,final Duration _timeToLife){
		super(_key,_supplier);
		this.timeToLife=_timeToLife;
	}

	
	/**
	 * Recover the lifespan for this cache
	 * @return lifespan as Duration object
	 * @see Duration
	 */
	public Duration getTimeToLife() {
		return timeToLife;
	}
	
	/**
	 * Recover a CacheInstanceAdapter instance with the value supplied by the internal supplier
	 * @return CacheInstanceAdapter instance value
	 * @see CacheRegistryAdapter#supplyInstance() 
	 */
	@Override
	public CacheInstanceAdapter supplyInstance() {
		return new TTLCacheInstance(supplyCacheValue(),this.timeToLife);
	}

	/**
	 * Validate if the provided cache instance is still valid
	 * @param _instance cache instance to validate
	 * @return true if the given instance is still valid, false otherwise
	 * @see CacheRegistryAdapter#isValid(org.bytemechanics.cache.flashback.CacheInstanceAdapter)
	 */
	@Override
	public boolean isValid(final CacheInstanceAdapter _instance) {
		return Optional.ofNullable(_instance)
							.map(TTLCacheInstance::cast)
							.map(TTLCacheInstance::isAlive)
							.orElse(false);
	}


	@Override
	public String toString() {
		return SimpleFormat.format("CacheRegistry[key={}, timeToLife={}, supplier={}]",key,timeToLife,supplier);
	}
}
