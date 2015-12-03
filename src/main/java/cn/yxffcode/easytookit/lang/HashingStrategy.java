/*
 * Copyright 2015 The Netty Project
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
package cn.yxffcode.easytookit.lang;

/**
 * Abstraction for hash code generation and equality comparison.
 */
public interface HashingStrategy<T> {

    int hashCode(T obj);

    boolean equals(T a,
                   T b);

    @SuppressWarnings("rawtypes")
    HashingStrategy JAVA_HASHER = new HashingStrategy() {
        @Override
        public int hashCode(Object obj) {
            return obj != null ?
                   obj.hashCode() :
                   0;
        }

        @Override
        public boolean equals(Object a,
                              Object b) {
            return (a == b) || (a != null && a.equals(b));
        }
    };
}
