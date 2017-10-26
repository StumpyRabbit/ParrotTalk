/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2003, 2016 Robert Withers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ******************************************************************************
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 * ******************************************************************************
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the erights.org community, which is also this software's 
 * foundation.  In particular, I would like to thank the following individuals:
 *         Mark Miller
 *         Marc Stiegler
 *         Bill Franz
 *         Tyler Close 
 *         Kevin Reid
 *******************************************************************************/
package club.callistohouse.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListUtil {

    public static <T> List<List<T>> fromArray(T[][] a) {
        ArrayList<List<T>> outer = new ArrayList<List<T>>(a.length);
        for (T[] inner : a) {
            outer.add(Arrays.asList(inner));
        }
        return outer;
    }

    public static <T> Iterable<T> reverseIterable(final List<T> list) {
        return new Iterable<T>() {
            public Iterator<T> iterator() {
                return reverseIterator(list);
            }
        };
    }
    public static <T> Iterator<T> reverseIterator(List<T> list) {
        final ListIterator<T> i = list.listIterator(list.size());
        return new Iterator<T>() {
            public boolean hasNext() {
                return i.hasPrevious();
            }
            public T next() {
                return i.previous();
            }

            public void remove() {
                i.remove();
            }
        };
    }
    public static <T> List<T> reverseList(List<T> list) {
        final List<T> reverse = new ArrayList<T>();
        for(int i = list.size() - 1; i >= 0; i--) {
        	reverse.add(list.get(i));
        }
        return reverse;
    }

    /**
     * Returns a copy of a two-level list. The objects within the list are not cloned.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<List<T>> newArrayList2D(List<List<T>> list) {
        ArrayList<List<T>> outer = new ArrayList<List<T>>(list.size());
        for (List<T> l : list) {
            outer.add(new ArrayList(l));
        }
        return outer;
    }

    public static class ZipList<T1, T2> extends AbstractList<Pair<T1, T2>> {
        List<T1> l1;
        List<T2> l2;
        public ZipList(List<T1> l1, List<T2> l2) {
            if (l1.size() != l2.size())
                throw new IllegalArgumentException("Lists are not the same size");
            this.l1 = l1;
            this.l2 = l2;
        }

        @Override public Pair<T1, T2> get(int index) {
            return new Pair<T1, T2>(l1.get(index), l2.get(index));
        }

        @Override public int size() {
            return l1.size();
        }
    }

    public static <T1, T2> List<Pair<T1, T2>> zip(List<T1> l1, List<T2> l2) {
        return new ZipList<T1, T2>(l1, l2);
    }

    public static <T1, T2> Pair<List<T1>, List<T2>> unzip(Collection<Pair<T1, T2>> l) {
        List<T1> l1 = new ArrayList<T1>(l.size());
        List<T2> l2 = new ArrayList<T2>(l.size());
        for (Pair<T1, T2> p : l) {
            l1.add(p.first());
            l2.add(p.second());
        }
        return new Pair<List<T1>, List<T2>>(l1, l2);
    }

    public static <T> List<T> immutableCopy(List<T> argTypes) {
        if (argTypes == null)
            return Collections.emptyList();
        return Collections.unmodifiableList(new ArrayList<T>(argTypes));
    }

}
