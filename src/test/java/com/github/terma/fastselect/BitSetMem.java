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

import com.github.terma.fastselect.utils.MemMeter;
import com.googlecode.javaewah.EWAHCompressedBitmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BitSetMem {

    public static void main(String[] args) {
        MemMeter memMeter = new MemMeter();

//        ArrayByteList arrayIntList=new ArrayByteList(300 * 1000 * 1000); 268 mb
        List<EWAHCompressedBitmap> indexes = new ArrayList<>();

        for (int j = 0; j < 100; j++) {
            EWAHCompressedBitmap bitSet = new EWAHCompressedBitmap(); // 45 mb
//        BitSet bitSet = new BitSet(300 * 1000 * 1000);


            indexes.add(bitSet);
        }


        Random random = new Random();
        for (int i = 0; i < 300 * 1000 * 1000; i++) {
            indexes.get(random.nextInt(indexes.size())).set(i);
        }

        System.out.println(memMeter.getUsedMb() + " mb");
        System.out.println(indexes.hashCode());
//        System.out.println(bitSet.hashCode());
//        System.out.println(arrayIntList.hashCode());
    }

}
