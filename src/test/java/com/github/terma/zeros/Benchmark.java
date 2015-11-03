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

package com.github.terma.zeros;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

public class Benchmark {

    public static final int G_MAX = 100;
    public static final int R_MAX = 5;
    public static final int C_MAX = 6;
    public static final int O_MAX = 2;
    public static final int S_MAX = 100;
    public static final int D_MAX = 1000;

    public static void main(String[] args) throws Exception {
        final long warmupDurationSec = 30;
        final long testDurationSec = 30;
        final int volume = 1000000;
//        final int volume = 200000;
        final int readers = 5;
//        final Scenario scenario = new ZerosDirectFilterBy4G1Risk();
//        final Scenario scenario = new ZerosDirectFilter10G5R1C40S100D();
        final Scenario scenario = new BloomFilterAndDirect10G5R1C40S100D();
//        final Scenario scenario = new H2FilterBy4G1Risk();

        // prepare
//        Filler filler = new Filler(volume);
//        CustomFiller filler = new CustomFiller(volume);
        BloomFilterAndDirectFiller filler = new BloomFilterAndDirectFiller(volume);
        filler.prepareDatabase();
        filler.run();

        System.out.println("warmup: " + warmupDurationSec + " sec");
        play(warmupDurationSec, readers, scenario);

        // test
        System.out.println("test: " + testDurationSec + " sec");
        final Player[] players = play(testDurationSec, readers, scenario);

        long time = 0;
        int count = 0;
        long sum = 0;

        for (Player player : players) {
            time += player.getTime();
            count += player.getCount();
            sum += player.getSum();
        }

        PrintWriter log = new PrintWriter(new FileWriter("/Users/terma/Projects/processing-prototype/log.log", true));
        log.println("Scenario: " + players[0].getScenario() + " done ");
        log.println("Volume: " + volume + ", duration: " + testDurationSec + " sec, threads: " + players.length + ", avg time: " + (time / count) + " msec, count: " + count + ", sum: " + sum);
        log.close();

        System.out.println("Scenario: " + players[0].getScenario() + " done ");
        System.out.println("Volume: " + volume + ", threads: " + players.length + ", avg time: " + (time / count) + " msec, count: " + count + ", sum: " + sum);
    }

    private static Player[] play(long testDurationSec, int readers, Scenario scenario) throws InterruptedException {
        final Player[] players = new Player[readers];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(scenario);
            players[i].start();
        }

        Thread.sleep(TimeUnit.SECONDS.toMillis(testDurationSec));

        for (Player player : players) player.interrupt();
        for (Player player : players) player.join();
        return players;
    }

}
