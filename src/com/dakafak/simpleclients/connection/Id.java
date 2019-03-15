package com.dakafak.simpleclients.connection;

import java.io.Serializable;

public class Id<T> implements Serializable {

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

	@Override
	public String toString() {
		return "Id{" +
				"id=" + id +
				'}';
	}

}
