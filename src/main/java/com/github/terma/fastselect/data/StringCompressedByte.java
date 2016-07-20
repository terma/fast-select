package com.github.terma.fastselect.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see com.github.terma.fastselect.StringCompressedByteNoCaseLikeRequest
 * @see StringCompressedByteData
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringCompressedByte {
}
