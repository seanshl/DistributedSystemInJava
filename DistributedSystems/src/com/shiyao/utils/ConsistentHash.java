package com.shiyao.utils;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.hash.HashFunction;

public class ConsistentHash<T> {

	private final HashFunction hashFunction;
	
	private final int numOfReplicas;
	
	private final SortedMap<Integer, T> circle;
	
	public ConsistentHash(HashFunction hashFunction, int numOfReplicas, Collection<T> nodes) {
		this.hashFunction = hashFunction;
		this.numOfReplicas = numOfReplicas;
		this.circle = new TreeMap<Integer, T>();
		
		for (T node : nodes) {
			this.add(node);
		}
	}
	
	public void add(T node) {
		for (int i = 0; i < numOfReplicas; i++) {
			circle.put(hashFunction.hashObject(node.toString() + i, null).asInt(), node);
		}
	}
	
	public void remove(T node) {
		for (int i = 0; i < numOfReplicas; i++) {
			circle.remove(hashFunction.hashObject(node.toString() + i, null));
		}
	}
	
	public T get(Object key) {
		if (circle.isEmpty()) return null;
		
		int hash = hashFunction.hashObject(key, null).asInt();
		
		if (!circle.containsKey(hash)) {
			SortedMap<Integer, T> tailMap = circle.tailMap(hash);
			
			circle.tailMap(hash);
			hash = tailMap.isEmpty()?
					circle.firstKey() : tailMap.firstKey();
		}
		
		return circle.get(hash);
	}
	
}
