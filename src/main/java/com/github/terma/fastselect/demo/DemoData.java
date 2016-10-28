/*
Copyright 2015-2016 Artem Stasiuk

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

package com.github.terma.fastselect.demo;

public class DemoData {

    public static final int[] SCALAR_IN_1 = new int[]{1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89};
    public static final String SCALAR_IN_1_AS_STRING = "1, 2, 3, 22, 5, 6, 33, 8, 9, 10, 89";

    public static final int[] SCALAR_IN_2 = new int[]{1, 2, 3, 4};
    public static final String SCALAR_IN_2_AS_STRING = "1, 2, 3, 4";

    public static final int G_ID_MAX = 100;
    public static final int G_ID_PORTION_DEVIATION = 5;
    public static final int G_ID_PORTION = 200000;

    public static final int R_MAX = 6;

    public static final int BS_ID_MAX = 150000;
    public static final int BS_ID_PORTION_DEVIATION = 500;
    public static final int BS_ID_PORTION = 200000;

    public static final long RANGE_LEFT = 100;
    public static final long RANGE_RIGHT = Long.MAX_VALUE / 2;

    public static final String STRING_LIKE = "like value 99";

    public byte prg;
    public byte csg;
    public byte tlg;
    public byte tsg;

    public short mid;
    public short cid;

    public int age;

    public byte crn;
    public long vlc;
    public long vsd;
    public long vch;

    public byte prr;
    public byte csr;
    public byte pror;
    public byte csor;
    public byte proc;
    public byte csoc;

    public int bsid;
    public int cpid;

    public String tr;
    public String ui;

    public String prn;
    public String csn;

    public static String joinByComma(int[] a) {
        int iMax = a.length - 1;

        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax) return b.toString();
            b.append(", ");
        }
    }
}
