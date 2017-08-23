/*
Copyright 2015-2017 Artem Stasiuk

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

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class StringMultipleRequestTest {

    @Test
    public void provideToString() {
        Assert.assertEquals(
                "StringMultipleRequest {name: 'col', in: [v1, v2]}",
                new StringMultipleRequest("col", "v1", "v2").toString());
        Assert.assertEquals(
                "StringMultipleRequest {name: 'col', in: [v1, v2]}",
                new StringMultipleRequest("col", Arrays.asList("v1", "v2")).toString());
        Assert.assertEquals(
                "StringMultipleRequest {name: 'col', in: [v1, v2]}",
                new StringMultipleRequest("col", new HashSet<String>() {{
                    add("v1");
                    add("v2");
                }}).toString());
        Assert.assertEquals(
                "StringMultipleRequest {name: 'col', in: [val]}",
                new StringMultipleRequest("col", "val").toString());
        Assert.assertEquals(
                "StringMultipleRequest {name: 'col', in: []}",
                new StringMultipleRequest("col").toString());
    }

}
