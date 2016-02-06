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

package com.github.terma.fastselect.benchmark;

import com.github.terma.fastselect.demo.DemoData;
import com.github.terma.fastselect.utils.MemMeter;

/**
 * Measure heap memory usage for particular DB Engine
 */
public class DemoMem {

    private static final String[] ENGINES = new String[]{"FastSelect"}; // "H2", "FastSelect"
    private static final int VOLUME = 60 * 1000 * 1000;

    public static void main(String[] args) throws Exception {
        for (String engine : ENGINES) {
            final MemMeter memMeter = new MemMeter();

            Player player = createPlayer(VOLUME, engine);

            System.out.println(player + " used heap mem: " + memMeter.getUsedMb() + " mb, volume: " + VOLUME);
            System.out.println(player);
        }
    }

    private static Player createPlayer(int volume, String engine) throws Exception {
        PlayerFactory<DemoData> factory = DemoBenchmark.ENGINE_FACTORIES.get(engine).newInstance();
        DemoBenchmark.fill(volume, factory);
        return factory.createPlayer();
    }

}
