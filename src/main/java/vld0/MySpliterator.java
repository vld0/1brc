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
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MySpliterator implements Spliterator<Stream<String>> {

    public static final long TOTAL_NB_OF_LINES = 1000000000L;// 1000

    public static final long CHUNKS = 4;// 8;// 1000

    private long start;// inclusive

    private long end;// exclusive

    // private long chunk;

    public MySpliterator(long start, long end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Stream<String>> action) {
        if (start < end) {
            try {
                Stream<String> lines = Files.lines(Path.of("./measurements.txt")).skip(start).limit(end - start);
                System.out.println("==================" + start + "==" + end + " lines = "+lines.toString());
                action.accept(lines);
                start = end;
                return true;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    @Override
    public Spliterator<Stream<String>> trySplit() {
        long chunk = (end - start) / 2;// Math.round((double)(start - end) / 2);
        if (chunk < (TOTAL_NB_OF_LINES / CHUNKS)) {
            return null;
        }
        long newStart = start + chunk;
        long newEnd = end;
        // start = start;
        end = newStart;

        System.out.println("TS==================OLD" + start + "==" + end);
        System.out.println("TS==================NEW" + newStart + "==" + newEnd);

        return new MySpliterator(newStart, newEnd);
    }

    @Override
    public long estimateSize() {
        return TOTAL_NB_OF_LINES;
    }

    @Override
    public int characteristics() {
        return ORDERED | SIZED | SUBSIZED | NONNULL;
    }

}
