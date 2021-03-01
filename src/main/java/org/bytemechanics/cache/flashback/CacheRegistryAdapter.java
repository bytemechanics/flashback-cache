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
package org.bytemechanics.cache.flashback;

import java.util.function.Supplier;

/**
 * Cache registry interface to which defines the format of the cache
 * @author afarre
 */
public interface CacheRegistryAdapter {

	/**
	 * Recover cache key
	 * @return cache key
	 */
	public String getKey();
	/**
	 * Recover cache value supplier
	 * @return cache value supplier
	 */
	public Supplier getSupplier();

	/**
	 * Recover a CacheInstanceAdapter instance with the value supplied by the internal supplier
	 * @return CacheInstanceAdapter instance value
	 */
	public CacheInstanceAdapter supplyInstance();

	/**
	 * Return a new instance of the cache. By default execute the supplier to get the new instance
	 * @return new cache instance
	 */
	public default Object supplyCacheValue(){ return getSupplier().get(); };
	
	/**
	 * Validate if the provided cache instance is still valid
	 * @param _instance cache instance to validate
	 * @return true if the given instance is still valid, false otherwise
	 */
	public default boolean isValid(final CacheInstanceAdapter _instance){ return true; };
}
