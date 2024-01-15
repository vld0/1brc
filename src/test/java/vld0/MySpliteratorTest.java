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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class MySpliteratorTest {

    @Test // ok dar incet
    public void givenAStreamOdede() {

        MySpliterator mySpliterator = new MySpliterator(0, MySpliterator.TOTAL_NB_OF_LINES);

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        // ExecutorService execService = Executors.newFixedThreadPool(2);

        StreamSupport.stream(mySpliterator, true).forEach(

                splittedStream -> {

                    // System.out.println(splittedStream + "-A-"+ Thread.currentThread().threadId());

                    forkJoinPool.submit(() -> {
                        // System.out.println(splittedStream + "-B-" + Thread.currentThread().threadId());
                        Map<String, Metric> measurements = splittedStream.map(line -> {
                            // System.out.println(splittedStream + "--"+ Thread.currentThread().threadId());
                            // String[] ss = line.split(";");// NOT WORKING fastpath if the regex is a one char String
                            // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                            int index = line.indexOf(';', 1);// city has min 1 char
                            // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                            return new Measurement(line.substring(0, index),
                                    Utils.parseDouble(line.substring(index + 1)));
                        })
                                .collect(
                                        Collectors.toMap(
                                                Measurement::city,
                                                measurement -> new Metric(measurement.temperature),
                                                Metric::combine,
                                                TreeMap::new));

                        System.out.println(measurements);

                        return measurements;

                    }).join();

                }

        );

    }

    /**
     * nu merge ? e ceva in neregula aici
     */
    @Test
    public void givenAStreamOfIntegers_whenProcessedInParallelWithCustomSpliterator_countProducesRightOutput() {

        MySpliterator mySpliterator = new MySpliterator(0, MySpliterator.TOTAL_NB_OF_LINES);

        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        // ExecutorService execService = Executors.newFixedThreadPool(2);
        forkJoinPool
                .submit(new Runnable() {
                    @Override
                    public void run() {
                        // System.out.println( "before --" + Thread.currentThread().threadId());
                        StreamSupport.stream(mySpliterator, true).forEach(
                                splittedStream -> {
                                    System.out.println(splittedStream + "--" + Thread.currentThread().threadId());
                                    Map<String, Metric> measurements = splittedStream.map(line -> {
                                        // System.out.println(splittedStream + "--"+ Thread.currentThread().threadId());
                                        // String[] ss = line.split(";");// NOT WORKING fastpath if the regex is a one char String
                                        // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                                        int index = line.indexOf(';', 1);// city has min 1 char
                                        // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                                        return new Measurement(line.substring(0, index),
                                                Utils.parseDouble(line.substring(index + 1)));
                                    })
                                            .collect(
                                                    Collectors.toMap(
                                                            Measurement::city,
                                                            measurement -> new Metric(measurement.temperature),
                                                            Metric::combine,
                                                            TreeMap::new));

                                    System.out.println(measurements);

                                });
                    }
                }

                )
                .join();

    }

    @Test
    public void givenAStreamOf2323() {

        MySpliterator mySpliterator = new MySpliterator(0, MySpliterator.TOTAL_NB_OF_LINES);

        // System.out.println( "before --" + Thread.currentThread().threadId());
        StreamSupport.stream(mySpliterator, true).forEach(
                splittedStream -> {
                    // System.out.println(splittedStream + "--" + Thread.currentThread().threadId());
                    Map<String, Metric> measurements = splittedStream.map(line -> {
                        // System.out.println(splittedStream + "--"+ Thread.currentThread().threadId());
                        // String[] ss = line.split(";");// NOT WORKING fastpath if the regex is a one char String
                        // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                        int index = line.indexOf(';', 1);// city has min 1 char
                        // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                        return new Measurement(line.substring(0, index),
                                Utils.parseDouble(line.substring(index + 1)));
                    })
                            .collect(
                                    Collectors.toMap(
                                            Measurement::city,
                                            measurement -> new Metric(measurement.temperature),
                                            Metric::combine));

                    System.out.println(measurements);

                });

    }

    @Test
    public void blablabla() throws InterruptedException, ExecutionException {

        MySpliterator mySpliterator = new MySpliterator(0, MySpliterator.TOTAL_NB_OF_LINES);

        // ExecutorService executorService = Executors.newFixedThreadPool(4);
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);

        List<MyCallable> callables = StreamSupport.stream(mySpliterator, true).map(MyCallable::new).collect(Collectors.toList());

        List<Future<Map<String, Metric>>> futures = forkJoinPool.invokeAll(callables);

        for (Future<Map<String, Metric>> future : futures) {
            System.out.println(future.get());
        }

    }

    public class MyCallable implements Callable<Map<String, Metric>> {

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
                return new Measurement(line.substring(0, index),
                        Utils.parseDouble(line.substring(index + 1)));
            })
                    .collect(
                            Collectors.toMap(
                                    Measurement::city,
                                    measurement -> new Metric(measurement.temperature),
                                    Metric::combine,
                                    TreeMap::new));

            return measurements;
        }

    }

    public class MyRecursiveTask extends RecursiveTask<Map<String, Metric>> {

        Stream<String> splittedStream;

        public MyRecursiveTask(Stream<String> splittedStream) {
            this.splittedStream = splittedStream;
        }

        @Override
        protected Map<String, Metric> compute() {

            Map<String, Metric> measurements = splittedStream.map(line -> {
                // System.out.println(splittedStream + "--"+ Thread.currentThread().threadId());
                // String[] ss = line.split(";");// NOT WORKING fastpath if the regex is a one char String
                // return new Measurement(ss[0], Double.parseDouble(ss[1]));
                int index = line.indexOf(';', 1);// city has min 1 char
                // Station name: non null UTF-8 string of min length 1 character and max length 100 bytes (i.e. this could be 100 one-byte characters, or 50 two-byte characters, etc.)
                return new Measurement(line.substring(0, index),
                        Utils.parseDouble(line.substring(index + 1)));
            })
                    .collect(
                            Collectors.toMap(
                                    Measurement::city,
                                    measurement -> new Metric(measurement.temperature),
                                    Metric::combine,
                                    TreeMap::new));

            return measurements;
        }

    }

    record Measurement(String city, Double temperature) {
    }

}