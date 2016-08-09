package com.github.terma.fastselect.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see com.github.terma.fastselect.StringCompressedIntNoCaseLikeRequest
 * @see StringCompressedIntData
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringCompressedInt {
}
