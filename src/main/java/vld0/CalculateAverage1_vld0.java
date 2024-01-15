package vld0;

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

import static vld0.Utils.parseDouble;

public class CalculateAverage1_vld0 {

    private static final String FILE = "./measurements.txt";

    public static void main(String[] args) throws IOException {

        Map<String, Metric> measurements = Files.lines(Path.of(FILE))
                .parallel()
                .map(line -> {
                    //System.out.println("Reading " + line + " from thread " + Thread.currentThread().threadId());
                    int index = line.indexOf(';', 1);// city has min 1 char
                    // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                    return new Measurement(line.substring(0, index),
                            parseDouble(line.substring(index + 1)));
                })
                .collect(
                        Collectors.toMap(
                                Measurement::city,
                                measurement -> new Metric(measurement.temperature),
                                Metric::combine,
                                TreeMap::new));

        System.out.println(measurements);

    }

    private record Measurement(String city, Double temperature) {
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
