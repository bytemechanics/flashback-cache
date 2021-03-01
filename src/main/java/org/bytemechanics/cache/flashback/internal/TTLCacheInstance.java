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
import java.time.Instant;
import java.util.Objects;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.internal.commons.string.SimpleFormat;

/**
 * Cache object instance store with time to life
 * @author afarre
 */
public class TTLCacheInstance extends CacheInstance {

	/** Cache expiration instant */
	protected final Instant expire;	
	
	
	/**
	 * Time to life cache instance constructor, uses the timeToLife parameter to calculate the expiration instance from the current instant
	 * @param _value cache value
	 * @param _timeToLife time that consider this cache as valid
	 */
	public TTLCacheInstance(final Object _value,final Duration _timeToLife){
		this(_value,Instant.now().plus(_timeToLife));
	}
	/**
	 * Time to life cache instance constructor
	 * @param _value cache value
	 * @param _expire cache expiration instant
	 */
	public TTLCacheInstance(final Object _value,final Instant _expire){
		super(_value);
		this.expire=_expire;
	}

	/**
	 * Recover the expiration instant
	 * @return expiration instant
	 */
	public Instant getExpire() {
		return expire;
	}
	/**
	 * Check if this cache is still valid comparing the current instant against the expire attribute
	 * @return true if the current instant is before the expiration instant
	 */
	public boolean isAlive(){
		return Instant.now()
							.isBefore(this.expire);
	}

	
	/**
	 * @see Object#hashCode() 
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.expire);
	}
	/**
	 * @see Object#equals(java.lang.Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		return ((this == obj)||((obj != null)&&(getClass() != obj.getClass())));
	}

	@Override
	public String toString() {
		return SimpleFormat.format("TTLCacheInstance[value={}, expire={}]",value,expire);
	}

	
	/**
	 * Utility method to cast to this class into optionals and streams
	 * @param _instance cacheInstance object as CacheInstanceAdapter
	 * @return cacheInstance object cast as TTLCacheInstance
	 */
	public static TTLCacheInstance cast(final CacheInstanceAdapter _instance){
		return (TTLCacheInstance)_instance;
	}
}
