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

import java.util.Objects;
import org.bytemechanics.cache.flashback.CacheInstanceAdapter;
import org.bytemechanics.cache.flashback.internal.commons.string.SimpleFormat;

/**
 * Cache object instance store
 * @author afarre
 */
public class CacheInstance implements CacheInstanceAdapter {

	/** Cache instance value */
	protected final Object value;	
	
	
	/**
	 * CacheInstance constructor
	 * @param _value cache value
	 */
	public CacheInstance(final Object _value){
		this.value=_value;
	}
	
	
	/**
	 * Cache value getter
	 * @return cache value
	 * @see CacheInstanceAdapter#getValue() 
	 */
	@Override
	public Object getValue() {
		return value;
	}

	
	/**
	 * @see Object#hashCode() 
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode(this.value);
	}
	/**
	 * @see Object#equals(java.lang.Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null)||(getClass() != obj.getClass())) {
			return false;
		}
		return Objects.equals(this.value, ((CacheInstance) obj).value);
	}

	@Override
	public String toString() {
		return SimpleFormat.format("CacheInstance[value={}]",value);
	}
	
	/**
	 * Utility method to cast to this class into optionals and streams
	 * @param _instance cacheInstance object as CacheInstanceAdapter
	 * @return cacheInstance object cast as CacheInstance
	 */
	public static CacheInstance cast(final CacheInstanceAdapter _instance){
		return (CacheInstance)_instance;
	}
}
