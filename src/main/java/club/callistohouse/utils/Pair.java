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
 * murmur/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 *******************************************************************************/
package club.callistohouse.utils;

import java.io.Serializable;
import java.util.Comparator;

public class Pair<T1, T2> implements Serializable {
	private static final long serialVersionUID = 2167128816649761768L;

	public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
    private T1 first;
    private T2 second;

    /** Required for GWT serialization. */
    protected Pair() {}

    /** Leverages type inference to make construction of pairs more concise */
    public static <T1, T2> Pair<T1, T2> pair(T1 first, T2 second) {
        return new Pair<T1, T2>(first, second);
    }

    public T1 first() {
        return first;
    }
    public T2 second() {
        return second;
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first() == null) ? 0 : first().hashCode());
        result = prime * result + ((second() == null) ? 0 : second().hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
	@Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (first() == null) {
            if (other.first() != null)
                return false;
        } else if (!first().equals(other.first()))
            return false;
        if (second() == null) {
            if (other.second() != null)
                return false;
        } else if (!second().equals(other.second()))
            return false;
        return true;
    }

    public String toString() {
        return "(" + first + "," + second + ")";
    }

    public static class CompareByFirst<T1 extends Comparable<T1>, T2> implements Comparator<Pair<T1, T2>> {
        public int compare(Pair<T1, T2> o1, Pair<T1, T2> o2) {
            return o1.first().compareTo(o2.first());
        }
    }
}
