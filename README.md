# Flashback cache
[![Latest version](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/flashback-cache/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.bytemechanics/flashback-cache/badge.svg)
[![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aflashback-cache&metric=alert_status)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aflashback-cache)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.bytemechanics%3Aflashback-cache&metric=coverage)](https://sonarcloud.io/dashboard/index/org.bytemechanics%3Aflashback-cache)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A simple zero-dependencies non-distributed Time to Life in-memory cache.

## Motivation
There is a lot of cache solution in market full of capabilities, therefore why another cache system? Basically to have some low-weight short-time cache for medium time consumption operations.

## Quick start
(Please read our [Javadoc](https://flashback-cache.bytemechanics.org/javadoc/index.html) for further information)
1. First of all include the Jar file in your compile and execution classpath.
**Maven**
```Maven
	<dependency>
		<groupId>org.bytemechanics</groupId>
		<artifactId>flashback-cache</artifactId>
		<version>X.X.X</version>
	</dependency>
```
**Graddle**
```Gradle
dependencies {
    compile 'org.bytemechanics:flashback-cache:X.X.X'
}
```
1. Allocate the cache service who will store all the cache values, or use the provided singleton
```Java
package mypackage;
import org.bytemechanics.cache.flashback.service.CacheServiceSingleton;
public class MyClass{
	private static final CacheServiceSingleton cacheService=CacheServiceSingleton.getInstance();
}
```
1. Create a new cache
   * Infinite cache
```Java
MyObject myResolvedValue=cacheService.from("my.cache.key",() -> methodRecoverCacheValue());
```
   * TTL cache (will be keep the same value until the next recover after 2 minutes)
```Java
MyObject myResolvedValue=cacheService.from("my.cache.key",2l,ChronoUnit.MINUTES,() -> methodRecoverCacheValue());
```

