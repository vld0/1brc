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
public class Utils {

    /**
     * non null double between -99.9 (inclusive) and 99.9 (inclusive), always with one fractional digit
     *
     * @param s
     * @return
     */
    static double parseDouble(String s) {
        char[] cs = s.toCharArray();
        int n = 0;
        int fractionalDigit = -1;
        int negative = 1;
        if (cs[0] == '-') {
            // negative
            negative = -1;
            if (cs[2] == '.') {
                n = cs[1] - '0';
                fractionalDigit = cs[3] - '0';
            }
            else if (cs[3] == '.') {
                n = (cs[1] - '0') * 10 + (cs[2] - '0');
                fractionalDigit = cs[4] - '0';
            }
            else {
                throw new IllegalStateException(s);
            }
        }
        else {
            // positive
            if (cs[1] == '.') {// 2.2
                n = cs[0] - '0';
                fractionalDigit = cs[2] - '0';
            }
            else if (cs[2] == '.') {// 22.2
                n = (cs[0] - '0') * 10 + (cs[1] - '0');
                fractionalDigit = cs[3] - '0';
            }
            else {
                throw new IllegalStateException(s);
            }
        }

        return negative * (n + (double) fractionalDigit / 10);
    }

}
