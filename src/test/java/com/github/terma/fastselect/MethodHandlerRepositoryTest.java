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

package com.github.terma.fastselect;

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
    public void throwExceptionIfCantAccessField() {
        new MethodHandlerRepository(B.class, new HashMap<String, Class>() {{
            put("c", long.class);
        }});
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwExceptionIfIncorrectType() {
        new MethodHandlerRepository(C.class, new HashMap<String, Class>() {{
            put("c", int.class);
        }});
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
