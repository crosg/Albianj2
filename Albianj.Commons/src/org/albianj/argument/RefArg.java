package org.albianj.argument;

public class RefArg<T> {
	private T _t = null;

	public T getValue() {
		return this._t;
	}

	public void setValue(T t) {
		this._t = t;
	}
}
