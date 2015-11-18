/*
Copyright 2015 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.fastselect.utils;

import org.junit.Test;

import java.util.HashMap;

public class MethodHandlerRepositoryTest {

    @Test
    public void empty() {
        new MethodHandlerRepository(A.class, new HashMap<String, Class>() {{
        }});
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfCantFindField() {
        new MethodHandlerRepository(A.class, new HashMap<String, Class>() {{
            put("x", int.class);
        }});
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfIncorrectType() {
        new MethodHandlerRepository(C.class, new HashMap<String, Class>() {{
            put("c", int.class);
        }});
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfAskAboutUnknownGetter() {
        new MethodHandlerRepository(C.class, new HashMap<String, Class>() {{
            put("c", int.class);
        }}).get("?");
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfAskAboutUnknownSetter() {
        new MethodHandlerRepository(C.class, new HashMap<String, Class>() {{
            put("c", int.class);
        }}).set("?");
    }

    @Test
    public void supportPrivateFields() {
        new MethodHandlerRepository(Private.class, new HashMap<String, Class>() {{
            put("field", int.class);
        }});
    }

    @Test
    public void supportPublicFields() {
        new MethodHandlerRepository(Public.class, new HashMap<String, Class>() {{
            put("field", int.class);
        }});
    }

    public static class Private {
        @SuppressWarnings("unused")
        private int field;
    }

    public static class Public {
        @SuppressWarnings("unused")
        public int field;
    }

    public static class A {

    }

    public static class B {
        private long c;
    }

    public static class C {
        public long c;
    }

}
