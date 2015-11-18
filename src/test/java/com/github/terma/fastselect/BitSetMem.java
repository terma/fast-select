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
