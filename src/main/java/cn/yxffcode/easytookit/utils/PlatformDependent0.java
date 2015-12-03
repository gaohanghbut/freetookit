/*
 * Copyright 2013 The Netty Project
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

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * The {@link PlatformDependent} operations which requires access to {@code sun.misc.*}.
 */
final class PlatformDependent0 {

    static final         Unsafe UNSAFE;
    private static final long   BYTE_ARRAY_BASE_OFFSET;

    private static final boolean UNALIGNED;

    static {
        ByteBuffer direct = ByteBuffer.allocateDirect(1);
        Field      addressField;
        try {
            addressField = Buffer.class.getDeclaredField("address");
            addressField.setAccessible(true);
            if (addressField.getLong(ByteBuffer.allocate(1)) != 0) {
                // A heap buffer must have 0 address.
                addressField = null;
            } else {
                if (addressField.getLong(direct) == 0) {
                    // A direct buffer must have non-zero address.
                    addressField = null;
                }
            }
        } catch (Throwable t) {
            // Failed to access the address field.
            addressField = null;
        }


        UNSAFE = UnsafeUtils.UNSAFE;

        if (UNSAFE == null) {
            BYTE_ARRAY_BASE_OFFSET = - 1;
            UNALIGNED = false;
        } else {
            BYTE_ARRAY_BASE_OFFSET = UNSAFE.arrayBaseOffset(byte[].class);
            boolean unaligned;
            try {
                Class<?> bitsClass       = Class.forName("java.nio.Bits", false, ClassLoader.getSystemClassLoader());
                Method   unalignedMethod = bitsClass.getDeclaredMethod("unaligned");
                unalignedMethod.setAccessible(true);
                unaligned = Boolean.TRUE.equals(unalignedMethod.invoke(null));
            } catch (Throwable t) {
                // We at least know x86 and x64 support unaligned access.
                String arch = StringUtils.EMPTY;
                //noinspection DynamicRegexReplaceableByCompiledPattern
                unaligned = arch.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            }

            UNALIGNED = unaligned;
        }
    }

    static boolean unalignedAccess() {
        return UNALIGNED;
    }

    static boolean equals(byte[] bytes1,
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
        if (len1 == 0) {
            return true;
        }
        final long baseOffset1    = BYTE_ARRAY_BASE_OFFSET + startPos1;
        final long baseOffset2    = BYTE_ARRAY_BASE_OFFSET + startPos2;
        int        remainingBytes = len1 & 7;
        for (int i = len1 - 8; i >= remainingBytes; i -= 8) {
            if (UNSAFE.getLong(bytes1, baseOffset1 + i) != UNSAFE.getLong(bytes2, baseOffset2 + i)) {
                return false;
            }
        }
        if (remainingBytes >= 4) {
            remainingBytes -= 4;
            if (UNSAFE.getInt(bytes1, baseOffset1 + remainingBytes) !=
                UNSAFE.getInt(bytes2, baseOffset2 + remainingBytes)) {
                return false;
            }
        }
        if (remainingBytes >= 2) {
            return UNSAFE.getChar(bytes1, baseOffset1) == UNSAFE.getChar(bytes2, baseOffset2) &&
                   (remainingBytes == 2 || bytes1[startPos1 + 2] == bytes2[startPos2 + 2]);
        }
        return bytes1[startPos1] == bytes2[startPos2];
    }

    private PlatformDependent0() {
    }

}
