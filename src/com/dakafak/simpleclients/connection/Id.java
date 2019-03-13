package com.dakafak.simpleclients.connection;

public class Id<T> {

	T id;

	public Id(T id) {
		this.id = id;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}
}
