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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CalculateAverage2_vld0 {

    private static final String FILE = "./measurements.txt";

    public static void main(String[] args) throws Exception, RuntimeException, InterruptedException {

        MySpliterator mySpliterator = new MySpliterator(0, MySpliterator.TOTAL_NB_OF_LINES);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        List<MyCallable> callables = StreamSupport.stream(mySpliterator, true)
                .map(MyCallable::new).collect(Collectors.toList());

        List<Future<Map<String, Metric>>> futures = executorService.invokeAll(callables);

        /*
         * for (Future<Map<String, Metric>> future : futures) {
         * System.out.println(future.get());
         * }
         */

        Map<String, Metric> measurements = futures.parallelStream().map(mapFuture -> {
            try {
                return mapFuture.get();
            }
            catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        })
                .flatMap(map -> map.entrySet().stream())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Metric::combine,
                                TreeMap::new));

        executorService.close();

        System.out.println(measurements);

    }

    private record Measurement(String city, Double temperature) {
    }

    public static class MyCallable implements Callable<Map<String, Metric>> {

        Stream<String> splittedStream;

        public MyCallable(Stream<String> splittedStream) {
            this.splittedStream = splittedStream;
        }

        @Override
        public Map<String, Metric> call() {
            Map<String, Metric> measurements = splittedStream.map(line -> {
                // System.out.println(splittedStream + "--"+ Thread.currentThread().threadId());
                // String[] ss = line.split(";");// NOT WORKING fastpath if the regex is a one char String
                // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                int index = line.indexOf(';', 1);// city has min 1 char
                // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                //System.out.println("Reading " + line + " from thread " + Thread.currentThread().threadId());
                return new Measurement(line.substring(0, index),
                        Utils.parseDouble(line.substring(index + 1)));
            })
                    .collect(
                            Collectors.toMap(
                                    Measurement::city,
                                    measurement -> new Metric(measurement.temperature),
                                    Metric::combine,
                                    TreeMap::new));

            splittedStream.close();

            return measurements;
        }

    }

}
