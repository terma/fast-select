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

package com.github.terma.fastselect.demo;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.callbacks.GroupCountCallback;

import java.util.Arrays;

/**
 * @author Artem Stasiuk
 * @see DemoData
 * @see DemoUtils
 */
public class Demo {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Demo app for fast-select https://github.com/terma/fast-select");
            System.out.println("Usage java -jar file.jar <TEST_VOLUME> <TIME_IN_SEC>");
            return;
        }

        final int volume = Integer.parseInt(args[0]);
        final int duration = Integer.parseInt(args[1]);

        final FastSelect<DemoData> fastSelect = DemoUtils.createFastSelect(1000, volume);
        System.out.println();

        System.out.println("FastSelect: " + fastSelect);
        System.out.println("Scenario: GroupCountCallback, where: " + Arrays.asList(DemoUtils.createWhere()));

        System.out.println();
        System.out.print("Heating... just: " + duration + " sec, ");
        long avg = play(fastSelect, duration * 1000);
        System.out.println("Avg: " + avg + " msec");

        System.out.println();
        System.out.print("Running... just: " + duration + " sec, ");
        avg = play(fastSelect, duration * 1000);
        System.out.println("Avg: " + avg + " msec");
    }

    private static long play(final FastSelect<DemoData> fastSelect, final long time) {
        final long target = System.currentTimeMillis() + time;
        int calls = 0;
        long entropy = 0;

        while (true) {
            // test call
            final GroupCountCallback callback = new GroupCountCallback(fastSelect.getColumnsByNames().get("r"));
            fastSelect.select(DemoUtils.createWhere(), callback);
            entropy += callback.getCounters().size();
            // end test call

            calls++;

            if (calls % 10 == 0) {
                if (System.currentTimeMillis() > target) break;
            }
        }
        System.out.println("e: " + entropy);
        return time / calls;
    }

}
