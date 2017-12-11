/*
 * Copyright (c) 2015 Abigail Esman. All rights reserved.
 * This file is part of Abigail Esman.
 *
 * @author Abigail Esman (abigail_esman@hotmail.com)
 */

package com.phantasm.phantasm.common.webapi;

public enum PTWebAPIError {
	SKErrorNone (0),
    SKErrorInvalidIDPass (4),
	SKErrorConnectionFailed (9);

	private int value;

	private PTWebAPIError(final int newValue) {
		value = newValue;
	}

	public int getValue() { return value; }
	public boolean compare(int value){return this.value == value;}

	public static PTWebAPIError getValue(int value) {
		PTWebAPIError[] as = PTWebAPIError.values();
		for(int i = 0; i < as.length; i++) {
			if(as[i].compare(value))
				return as[i];
		}

		return PTWebAPIError.SKErrorNone;
	}
}
