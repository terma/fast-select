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

import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.demo.DemoUtils;
import com.github.terma.fastselect.utils.MemMeter;
import org.openjdk.jmh.runner.RunnerException;

public class FastSelectMem {

    public static void main(String[] args) throws RunnerException, InterruptedException {
        int volume = 10 * 10 * 1000 * 1000;

        final MemMeter memMeter = new MemMeter();
        FastSelect<DemoData> fastSelect = DemoUtils.createFastSelect(10000, volume);

        System.out.println("Used mem: " + memMeter.getUsedMb() + " mb, volume: " + volume);
        System.out.println(fastSelect.size());
    }

}
