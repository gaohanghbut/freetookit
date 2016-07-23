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
package cn.yxffcode.freetookit.collection;

import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

/**
 * 线程安全的Set
 * <p>
 * 此类与ConcurrentHashMap的关系类似于HashSet与HashMap
 *
 * @author gaohang
 */
public final class ConcurrentSet<E> extends AbstractSet<E> implements Serializable {

  private final ConcurrentMap<E, Boolean> map;

  private ConcurrentSet() {
    map = Maps.newConcurrentMap();
  }

  public static <E> ConcurrentSet<E> create() {
    return new ConcurrentSet<>();
  }

  @Override
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  @Override
  public boolean add(E o) {
    return map.putIfAbsent(o, Boolean.TRUE) == null;
  }

  @Override
  public boolean remove(Object o) {
    return map.remove(o) != null;
  }

  @Override
  public void clear() {
    map.clear();
  }

  public boolean addIfAbsent(E e) {
    return map.putIfAbsent(e, Boolean.TRUE) == null;
  }
}
