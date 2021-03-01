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

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bytemechanics.cache.flashback.service.TTLCacheService;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.CacheKeyUndefinedException;
import org.bytemechanics.cache.flashback.CacheRegistryAdapter;
import org.bytemechanics.cache.flashback.service.CacheService;

/**
 * In memory cache repository
 * @author afarre
 */
public class DefaultCacheServiceImpl implements TTLCacheService{
	
	/** Cache registry storage */
	protected final ConcurrentMap<String,CacheRegistryAdapter> registry;
	/** Cache storage */
	protected final ConcurrentMap<CacheRegistryAdapter,CacheInstanceAdapter> storage;

	
	/**
	 * Constructor with the configured cache size from cache property or the cache default size
	 */
	public DefaultCacheServiceImpl() {
		this(Integer.valueOf(System.getProperty(EXPECTED_CACHE_SIZE_PROPERTY,DEFAULT_CACHE_SIZE)));
	}
	/**
	 * Constructor with using the initial cache size as provided
	 * @param _initialSize initial cache size
	 */
	public DefaultCacheServiceImpl(final int _initialSize) {
		this(new ConcurrentHashMap<String,CacheRegistryAdapter>(_initialSize),new ConcurrentHashMap<CacheRegistryAdapter, CacheInstanceAdapter>(_initialSize));
	}
	/**
	 * Constructor with the configured cache size from cache property or the cache default size
	 * @param _registry cache registry storage
	 * @param _storage cache storage
	 */
	public DefaultCacheServiceImpl(final ConcurrentMap<String,CacheRegistryAdapter> _registry,final ConcurrentMap<CacheRegistryAdapter, CacheInstanceAdapter> _storage) {
		this.registry = _registry;
		this.storage = _storage;
	}
	
	
	/**
	 * Register new cache in the service store
	 * @param _cacheRegistry cache registry to register
	 * @return registered cache key
	 * @see CacheService#registerCache(org.bytemechanics.cache.flashback.CacheRegistryAdapter) 
	 */
	@Override
	public String registerCache(final CacheRegistryAdapter _cacheRegistry) {
		return this.registry.computeIfAbsent(_cacheRegistry.getKey(),key -> _cacheRegistry)
									.getKey();
	}
	/**
	 * Recover the cached value or supply new value (put in cache and return it)
	 * @param _key cache key
	 * @return Cache value as object
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 * @see CacheService#get(java.lang.String) 
	 */
	@Override
	public Object get(final String _key){
		return Optional.ofNullable(_key)
							.map(this.registry::get)
							.map(currentRegistry -> this.storage.compute(currentRegistry,DefaultCacheServiceImpl::validOrNew))
							.map(CacheInstanceAdapter::getValue)
							.orElseThrow(CacheKeyUndefinedException::new);
	}

	/**
	 * Expire cache invalidating its current value if any
	 * @param _key cache key
	 * @return cache key expired or null if not exist
	 * @throws CacheKeyUndefinedException whenever key does has not been registered
	 * @see CacheService#expire(java.lang.String) 
	 */
	@Override
	public String expire(final String _key) {
		this.storage.remove(Optional.ofNullable(_key)
												.map(this.registry::get)
												.orElseThrow(CacheKeyUndefinedException::new));
		return _key;
	}

	/**
	 * Validate if the given cache instance is still valid by calling the isValid method from _cacheRegistry and generate a new one if not, returning the new instance or the old one
	 * @param _cacheRegistry cache registry to use as validation
	 * @param _cacheInstance cache instance to validate
	 * @return cache instance valid for this cache registry
	 */
	protected static final CacheInstanceAdapter validOrNew(final CacheRegistryAdapter _cacheRegistry,final CacheInstanceAdapter _cacheInstance){
		return Optional.ofNullable(_cacheInstance)
							.filter(_cacheRegistry::isValid)
							.orElseGet(_cacheRegistry::supplyInstance);
	}
}
