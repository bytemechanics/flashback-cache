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

import org.bytemechanics.cache.flashback.service.impl.DefaultCacheServiceImpl;


/**
 * Default singleton container for the cache service. Used to store the cache service if no external singleton container exist.
 * @author afarre
 * @since 1.0.0
 */
public class CacheServiceSingleton {
	
	private static CacheServiceSingleton instance;

	private final TTLCacheService metricsService;
	
	private CacheServiceSingleton(){
		this.metricsService=new DefaultCacheServiceImpl();
	}

	/**
	 * Returns always the same instance of this cache service singleton creating new one if it's the first time
	 * @return always the same instance of this cache service singleton
	 */
	public static final CacheServiceSingleton getInstance(){
		
		CacheServiceSingleton reply;
		
		if((reply=CacheServiceSingleton.instance)==null){
			synchronized(CacheServiceSingleton.class){
				if((reply=CacheServiceSingleton.instance)==null){
					reply=new CacheServiceSingleton();
					CacheServiceSingleton.instance=reply;
				}
			}
		}
		
		return reply;
	}

	/**
	 * Returns always the same cache service not null instance
	 * @return always the same cache service not null instance
	 */
	public TTLCacheService getCacheService() {
		return metricsService;
	}
}
