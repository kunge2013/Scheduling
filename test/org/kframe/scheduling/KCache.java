package org.kframe.scheduling;

public class KCache<K, V> extends Cache<K, V>{

	public KCache(Kind keyKind, Kind valueKind) {
		super(keyKind, valueKind);
	}

	@Override
	public V create(K key) {
		return null;
	}

}
