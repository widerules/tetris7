package com.supersetter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Elements {
	Class<?>[] base();
	Class<?>[] derived();
}
