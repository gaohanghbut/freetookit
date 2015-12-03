/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cn.yxffcode.easytookit.utils;

/**
 * 从netty中copy
 */
public final class ConcurrentUtils {
    /**
     * Compare two {@code byte} arrays for equality. For performance reasons no bounds checking on the
     * parameters is performed.
     *
     * @param bytes1    the first byte array.
     * @param startPos1 the position (inclusive) to start comparing in {@code bytes1}.
     * @param endPos1   the position (exclusive) to stop comparing in {@code bytes1}.
     * @param bytes2    the second byte array.
     * @param startPos2 the position (inclusive) to start comparing in {@code bytes2}.
     * @param endPos2   the position (exclusive) to stop comparing in {@code bytes2}.
     */
    public static boolean equals(byte[] bytes1,
                                 int startPos1,
                                 int endPos1,
                                 byte[] bytes2,
                                 int startPos2,
                                 int endPos2) {
        if (! ConcurrentUtils0.unalignedAccess()) {
            return safeEquals(bytes1, startPos1, endPos1, bytes2, startPos2, endPos2);
        }
        return ConcurrentUtils0.equals(bytes1, startPos1, endPos1, bytes2, startPos2, endPos2);
    }

    private static boolean safeEquals(byte[] bytes1,
                                      int startPos1,
                                      int endPos1,
                                      byte[] bytes2,
                                      int startPos2,
                                      int endPos2) {
        final int len1 = endPos1 - startPos1;
        final int len2 = endPos2 - startPos2;
        if (len1 != len2) {
            return false;
        }
        final int end = startPos1 + len1;
        for (int i = startPos1, j = startPos2; i < end; ++ i, ++ j) {
            if (bytes1[i] != bytes2[j]) {
                return false;
            }
        }
        return true;
    }

    private ConcurrentUtils() {
        // only static method supported
    }
}
