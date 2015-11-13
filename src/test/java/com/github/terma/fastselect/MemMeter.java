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

import org.openjdk.jmh.runner.RunnerException;

public class MemMeter {

    private final long usedBefore = getUsedNow();

    public static void main(String[] args) throws RunnerException, InterruptedException {
        int volume = 100000000;

        final MemMeter memMeter = new MemMeter();
        new FastSelectFiller(1000, volume).run();

        System.out.println(FastSelectFiller.database.size());
        System.out.println("Used mem: " + memMeter.getUsedMb() + " mb, volume: " + volume);
    }

    private static long getUsedNow() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public long getUsedMb() {
        return getUsed() / 1024 / 1024;
    }

    public long getUsed() {
        System.gc();
        System.gc();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        final long usedAfter = getUsedNow();
        return Math.max(0, usedAfter - usedBefore);
    }

}
