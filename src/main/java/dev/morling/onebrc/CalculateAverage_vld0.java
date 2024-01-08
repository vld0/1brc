package dev.morling.onebrc;

/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CalculateAverage_vld0 {

    private static final String FILE = "./measurements.txt";

    public static void main(String[] args) throws IOException {

        Map<String, Metric> measurements = Files.lines(Path.of(FILE))
                .map(line -> {
                    // String[] ss = line.split(";");// fastpath if the regex is a one char String
                    // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                    int index = line.indexOf(';');
                    return new Measurement(line.substring(0, index),
                            Double.parseDouble(line.substring(index + 1)));
                })
                .parallel()
                .collect(
                        Collectors.toMap(
                                Measurement::city,
                                measurement -> new Metric(measurement.temperature),
                                Metric::combine,
                                TreeMap::new));

        System.out.println(measurements);

    }

    record Measurement(String city, Double temperature) {
    }

    public static class Metric {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double total = 0;
        int count = 0;

        public Metric(double temperature) {
            this.total = total + temperature;
            this.count = count + 1;
            min = temperature;
            max = temperature;
        }

        // @Override
        /*
         * public void accept(double temperature) {
         * total += temperature;
         * count++;
         * }
         */

        public Metric combine(Metric other) {
            total += other.total;
            count += other.count;
            min = Math.min(min, other.min);
            max = Math.max(max, other.max);
            return this;
        }

        @Override
        public String toString() {
            // credits https://stackoverflow.com/a/48028886/4797156
            return min + "/" + Math.round((total / count) * 10.0) / 10.0 + "/" + max;
        }

    }

    /*
     * System.out.println("r="+(Math.round(((double) 187.2 /35) * 10.0) / 10.0));
     *
     * List<Measurement> ms = Arrays.asList(
     * new Measurement("Abha", -23),
     * new Measurement("Abha", 3.332),
     * new Measurement("Abha", 10),
     * new Measurement("Abha", 15),
     * new Measurement("Abidjan", 3),
     * new Measurement("Abidjan", 16.3)
     *
     * );
     */
    /*
     *
     * final Map<String, Metric> measurements = ms
     * .stream()
     */

}
