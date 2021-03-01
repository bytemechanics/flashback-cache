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

import java.util.function.Supplier;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.CacheRegistryAdapter;
import org.bytemechanics.cache.flashback.internal.commons.string.SimpleFormat;

/**
 * Cache memory registry where stores the cache configuration
 * @author afarre
 */
public class CacheRegistry implements CacheRegistryAdapter {
	
	/** Cache key */
	protected final String key;
	/** Cache value supplier */
	protected final Supplier supplier;
	
	
	/**
	 * Constructor of cache registry
	 * @param _key cache key to recover its value
	 * @param _supplier cache supplier to generate a new value if not exist or is has been expired
	 */
	public CacheRegistry(final String _key,final Supplier _supplier){
		this.key=_key;
		this.supplier=_supplier;
	}


	/**
	 * Recover cache key
	 * @return cache key
	 */
	@Override
	public String getKey() {
		return key;
	}
	/**
	 * Recover cache value supplier
	 * @return cache value supplier
	 */
	@Override
	public Supplier getSupplier() {
		return supplier;
	}
	
	
	/**
	 * Generate cache instance from this cache registry
	 * @return new cache instance
	 */
	@Override
	public CacheInstanceAdapter supplyInstance() {
		return new CacheInstance(supplyCacheValue());
	}
	
	
	/**
	 * @see Object#hashCode() 
	 */
	@Override
	public int hashCode() {
		return this.key.hashCode();
	}
	/**
	 * @see Object#equals(java.lang.Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final CacheRegistry other = (CacheRegistry) obj;
		return !((this.key == null) ? (other.key != null) : !this.key.equals(other.key));
	}

	@Override
	public String toString() {
		return SimpleFormat.format("CacheRegistry[key={}, supplier={}]",key,supplier);
	}
}
