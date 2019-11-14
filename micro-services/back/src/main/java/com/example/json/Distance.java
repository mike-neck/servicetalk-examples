/*
 * Copyright 2019 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.json;

public enum Distance {
    FIVE {
        @Override
        boolean detectProblemOnMin(int min) {
            return min < 11 || 20 < min;
        }
    },
    TEN {
        @Override
        boolean detectProblemOnMin(int min) {
            return min < 22 || 40 < min;
        }
    },
    FIFTEEN {
        @Override
        boolean detectProblemOnMin(int min) {
            return min < 35 || 60 < min;
        }
    },
    TWENTY {
        @Override
        boolean detectProblemOnMin(int min) {
            if (0 <= min && min <= 20) {
                return false;
            }
            return min < 47 || 60 < min;
        }
    },
    HALF_MARATHON {
        @Override
        boolean detectProblemOnMin(int min) {
            if (0 <= min && min <= 20) {
                return false;
            }
            return min < 47 || 60 < min;
        }
    },
    ;

    boolean detectProblemOnHour(int hour) {
        return hour != 0;
    }

    abstract boolean detectProblemOnMin(int min);

    boolean detectProblemOnSec(int sec) {
        return sec < 0 || 60 <= sec;
    }
}
