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

/**
 * emulate user access by different dimension
 */
public class Player extends Thread {

    private final Scenario scenario;

    private long time;
    private int count;
    private long sum;

    public Player(Scenario scenario) {
        this.scenario = scenario;
    }

    public void run() {
        try {
            scenario.prepare();

            while (!Thread.currentThread().isInterrupted()) {
                final long start = System.currentTimeMillis();

                sum += scenario.execute();
                time += System.currentTimeMillis() - start;
                count++;
            }
        } catch (final InterruptedException e) {
            // just stop
        } catch (final Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    public long getTime() {
        return time;
    }

    public int getCount() {
        return count;
    }

    public long getSum() {
        return sum;
    }

    public Scenario getScenario() {
        return scenario;
    }
}
