package com.github.terma.fastselect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

class FastBitSet {

//    private final static int ADDRESS_BITS_PER_WORD = 6;
//    private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
//    private final static int BIT_INDEX_MASK = BITS_PER_WORD - 1;
//
//    /* Used to shift left or right for a partial word mask */
//    private static final long WORD_MASK = 0xffffffffffffffffL;
//
//    private long[] words;
//
//    private transient int wordsInUse = 0;
//
//    private transient boolean sizeIsSticky = false;
//
//    private static int wordIndex(int bitIndex) {
//        return bitIndex >> ADDRESS_BITS_PER_WORD;
//    }
//
//    private void checkInvariants() {
//        assert(wordsInUse == 0 || words[wordsInUse - 1] != 0);
//        assert(wordsInUse >= 0 && wordsInUse <= words.length);
//        assert(wordsInUse == words.length || words[wordsInUse] == 0);
//    }
//
//    private void recalculateWordsInUse() {
//        // Traverse the bitset until a used word is found
//        int i;
//        for (i = wordsInUse-1; i >= 0; i--)
//            if (words[i] != 0)
//                break;
//
//        wordsInUse = i+1; // The new logical size
//    }
//
//    public BitSet() {
//        initWords(BITS_PER_WORD);
//        sizeIsSticky = false;
//    }
//
//    private void initWords(int nbits) {
//        words = new long[wordIndex(nbits-1) + 1];
//    }
//
//    private void ensureCapacity(int wordsRequired) {
//        if (words.length < wordsRequired) {
//            // Allocate larger of doubled size or required size
//            int request = Math.max(2 * words.length, wordsRequired);
//            words = Arrays.copyOf(words, request);
//            sizeIsSticky = false;
//        }
//    }
//
//    private void expandTo(int wordIndex) {
//        int wordsRequired = wordIndex+1;
//        if (wordsInUse < wordsRequired) {
//            ensureCapacity(wordsRequired);
//            wordsInUse = wordsRequired;
//        }
//    }
//
//    private static void checkRange(int fromIndex, int toIndex) {
//        if (fromIndex < 0)
//            throw new IndexOutOfBoundsException("fromIndex < 0: " + fromIndex);
//        if (toIndex < 0)
//            throw new IndexOutOfBoundsException("toIndex < 0: " + toIndex);
//        if (fromIndex > toIndex)
//            throw new IndexOutOfBoundsException("fromIndex: " + fromIndex +
//                    " > toIndex: " + toIndex);
//    }
//
//    public void set(int bitIndex) {
//        if (bitIndex < 0)
//            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
//
//        int wordIndex = wordIndex(bitIndex);
//        expandTo(wordIndex);
//
//        words[wordIndex] |= (1L << bitIndex); // Restores invariants
//
//        checkInvariants();
//    }
//
//    public boolean get(int bitIndex) {
//        if (bitIndex < 0)
//            throw new IndexOutOfBoundsException("bitIndex < 0: " + bitIndex);
//
//        checkInvariants();
//
//        int wordIndex = wordIndex(bitIndex);
//        return (wordIndex < wordsInUse)
//                && ((words[wordIndex] & (1L << bitIndex)) != 0);
//    }
//
//    /**
//     * Returns a new {@code BitSet} composed of bits from this {@code BitSet}
//     * from {@code fromIndex} (inclusive) to {@code toIndex} (exclusive).
//     *
//     * @param  fromIndex index of the first bit to include
//     * @param  toIndex index after the last bit to include
//     * @return a new {@code BitSet} from a range of this {@code BitSet}
//     * @throws IndexOutOfBoundsException if {@code fromIndex} is negative,
//     *         or {@code toIndex} is negative, or {@code fromIndex} is
//     *         larger than {@code toIndex}
//     * @since  1.4
//     */
//    public BitSet get(int fromIndex, int toIndex) {
//        checkRange(fromIndex, toIndex);
//
//        checkInvariants();
//
//        int len = length();
//
//        // If no set bits in range return empty bitset
//        if (len <= fromIndex || fromIndex == toIndex)
//            return new BitSet(0);
//
//        // An optimization
//        if (toIndex > len)
//            toIndex = len;
//
//        BitSet result = new BitSet(toIndex - fromIndex);
//        int targetWords = wordIndex(toIndex - fromIndex - 1) + 1;
//        int sourceIndex = wordIndex(fromIndex);
//        boolean wordAligned = ((fromIndex & BIT_INDEX_MASK) == 0);
//
//        // Process all words but the last word
//        for (int i = 0; i < targetWords - 1; i++, sourceIndex++)
//            result.words[i] = wordAligned ? words[sourceIndex] :
//                    (words[sourceIndex] >>> fromIndex) |
//                            (words[sourceIndex+1] << -fromIndex);
//
//        // Process the last word
//        long lastWordMask = WORD_MASK >>> -toIndex;
//        result.words[targetWords - 1] =
//                ((toIndex-1) & BIT_INDEX_MASK) < (fromIndex & BIT_INDEX_MASK)
//                        ? /* straddles source words */
//                        ((words[sourceIndex] >>> fromIndex) |
//                                (words[sourceIndex+1] & lastWordMask) << -fromIndex)
//                        :
//                        ((words[sourceIndex] & lastWordMask) >>> fromIndex);
//
//        // Set wordsInUse correctly
//        result.wordsInUse = targetWords;
//        result.recalculateWordsInUse();
//        result.checkInvariants();
//
//        return result;
//    }
//
//    public int length() {
//        if (wordsInUse == 0)
//            return 0;
//
//        return BITS_PER_WORD * (wordsInUse - 1) +
//                (BITS_PER_WORD - Long.numberOfLeadingZeros(words[wordsInUse - 1]));
//    }
//
//    /**
//     * Returns true if this {@code BitSet} contains no bits that are set
//     * to {@code true}.
//     *
//     * @return boolean indicating whether this {@code BitSet} is empty
//     * @since  1.4
//     */
//    public boolean isEmpty() {
//        return wordsInUse == 0;
//    }
//
//    /**
//     * Returns true if the specified {@code BitSet} has any bits set to
//     * {@code true} that are also set to {@code true} in this {@code BitSet}.
//     *
//     * @param  set {@code BitSet} to intersect with
//     * @return boolean indicating whether this {@code BitSet} intersects
//     *         the specified {@code BitSet}
//     * @since  1.4
//     */
//    public boolean intersects(BitSet set) {
//        for (int i = Math.min(wordsInUse, set.wordsInUse) - 1; i >= 0; i--)
//            if ((words[i] & set.words[i]) != 0)
//                return true;
//        return false;
//    }
//
//    /**
//     * Returns the number of bits set to {@code true} in this {@code BitSet}.
//     *
//     * @return the number of bits set to {@code true} in this {@code BitSet}
//     * @since  1.4
//     */
//    public int cardinality() {
//        int sum = 0;
//        for (int i = 0; i < wordsInUse; i++)
//            sum += Long.bitCount(words[i]);
//        return sum;
//    }
//
//    /**
//     * Performs a logical <b>AND</b> of this target bit set with the
//     * argument bit set. This bit set is modified so that each bit in it
//     * has the value {@code true} if and only if it both initially
//     * had the value {@code true} and the corresponding bit in the
//     * bit set argument also had the value {@code true}.
//     *
//     * @param set a bit set
//     */
//    public void and(BitSet set) {
//        if (this == set)
//            return;
//
//        while (wordsInUse > set.wordsInUse)
//            words[--wordsInUse] = 0;
//
//        // Perform logical AND on words in common
//        for (int i = 0; i < wordsInUse; i++)
//            words[i] &= set.words[i];
//
//        recalculateWordsInUse();
//        checkInvariants();
//    }
//
//    /**
//     * Performs a logical <b>OR</b> of this bit set with the bit set
//     * argument. This bit set is modified so that a bit in it has the
//     * value {@code true} if and only if it either already had the
//     * value {@code true} or the corresponding bit in the bit set
//     * argument has the value {@code true}.
//     *
//     * @param set a bit set
//     */
//    public void or(BitSet set) {
//        if (this == set)
//            return;
//
//        int wordsInCommon = Math.min(wordsInUse, set.wordsInUse);
//
//        if (wordsInUse < set.wordsInUse) {
//            ensureCapacity(set.wordsInUse);
//            wordsInUse = set.wordsInUse;
//        }
//
//        // Perform logical OR on words in common
//        for (int i = 0; i < wordsInCommon; i++)
//            words[i] |= set.words[i];
//
//        // Copy any remaining words
//        if (wordsInCommon < set.wordsInUse)
//            System.arraycopy(set.words, wordsInCommon,
//                    words, wordsInCommon,
//                    wordsInUse - wordsInCommon);
//
//        // recalculateWordsInUse() is unnecessary
//        checkInvariants();
//    }
//
//    /**
//     * Performs a logical <b>XOR</b> of this bit set with the bit set
//     * argument. This bit set is modified so that a bit in it has the
//     * value {@code true} if and only if one of the following
//     * statements holds:
//     * <ul>
//     * <li>The bit initially has the value {@code true}, and the
//     *     corresponding bit in the argument has the value {@code false}.
//     * <li>The bit initially has the value {@code false}, and the
//     *     corresponding bit in the argument has the value {@code true}.
//     * </ul>
//     *
//     * @param  set a bit set
//     */
//    public void xor(BitSet set) {
//        int wordsInCommon = Math.min(wordsInUse, set.wordsInUse);
//
//        if (wordsInUse < set.wordsInUse) {
//            ensureCapacity(set.wordsInUse);
//            wordsInUse = set.wordsInUse;
//        }
//
//        // Perform logical XOR on words in common
//        for (int i = 0; i < wordsInCommon; i++)
//            words[i] ^= set.words[i];
//
//        // Copy any remaining words
//        if (wordsInCommon < set.wordsInUse)
//            System.arraycopy(set.words, wordsInCommon,
//                    words, wordsInCommon,
//                    set.wordsInUse - wordsInCommon);
//
//        recalculateWordsInUse();
//        checkInvariants();
//    }
//
//    /**
//     * Clears all of the bits in this {@code BitSet} whose corresponding
//     * bit is set in the specified {@code BitSet}.
//     *
//     * @param  set the {@code BitSet} with which to mask this
//     *         {@code BitSet}
//     * @since  1.2
//     */
//    public void andNot(BitSet set) {
//        // Perform logical (a & !b) on words in common
//        for (int i = Math.min(wordsInUse, set.wordsInUse) - 1; i >= 0; i--)
//            words[i] &= ~set.words[i];
//
//        recalculateWordsInUse();
//        checkInvariants();
//    }
//
//    /**
//     * Returns the hash code value for this bit set. The hash code depends
//     * only on which bits are set within this {@code BitSet}.
//     *
//     * <p>The hash code is defined to be the result of the following
//     * calculation:
//     *  <pre> {@code
//     * public int hashCode() {
//     *     long h = 1234;
//     *     long[] words = toLongArray();
//     *     for (int i = words.length; --i >= 0; )
//     *         h ^= words[i] * (i + 1);
//     *     return (int)((h >> 32) ^ h);
//     * }}</pre>
//     * Note that the hash code changes if the set of bits is altered.
//     *
//     * @return the hash code value for this bit set
//     */
//    public int hashCode() {
//        long h = 1234;
//        for (int i = wordsInUse; --i >= 0; )
//            h ^= words[i] * (i + 1);
//
//        return (int)((h >> 32) ^ h);
//    }
//
//    /**
//     * Returns the number of bits of space actually in use by this
//     * {@code BitSet} to represent bit values.
//     * The maximum element in the set is the size - 1st element.
//     *
//     * @return the number of bits currently in this bit set
//     */
//    public int size() {
//        return words.length * BITS_PER_WORD;
//    }
//
//    /**
//     * Compares this object against the specified object.
//     * The result is {@code true} if and only if the argument is
//     * not {@code null} and is a {@code Bitset} object that has
//     * exactly the same set of bits set to {@code true} as this bit
//     * set. That is, for every nonnegative {@code int} index {@code k},
//     * <pre>((BitSet)obj).get(k) == this.get(k)</pre>
//     * must be true. The current sizes of the two bit sets are not compared.
//     *
//     * @param  obj the object to compare with
//     * @return {@code true} if the objects are the same;
//     *         {@code false} otherwise
//     * @see    #size()
//     */
//    public boolean equals(Object obj) {
//        if (!(obj instanceof BitSet))
//            return false;
//        if (this == obj)
//            return true;
//
//        BitSet set = (BitSet) obj;
//
//        checkInvariants();
//        set.checkInvariants();
//
//        if (wordsInUse != set.wordsInUse)
//            return false;
//
//        // Check words in use by both BitSets
//        for (int i = 0; i < wordsInUse; i++)
//            if (words[i] != set.words[i])
//                return false;
//
//        return true;
//    }
//
//    /**
//     * Cloning this {@code BitSet} produces a new {@code BitSet}
//     * that is equal to it.
//     * The clone of the bit set is another bit set that has exactly the
//     * same bits set to {@code true} as this bit set.
//     *
//     * @return a clone of this bit set
//     * @see    #size()
//     */
//    public Object clone() {
//        if (! sizeIsSticky)
//            trimToSize();
//
//        try {
//            BitSet result = (BitSet) super.clone();
//            result.words = words.clone();
//            result.checkInvariants();
//            return result;
//        } catch (CloneNotSupportedException e) {
//            throw new InternalError(e);
//        }
//    }
//
//    /**
//     * Attempts to reduce internal storage used for the bits in this bit set.
//     * Calling this method may, but is not required to, affect the value
//     * returned by a subsequent call to the {@link #size()} method.
//     */
//    private void trimToSize() {
//        if (wordsInUse != words.length) {
//            words = Arrays.copyOf(words, wordsInUse);
//            checkInvariants();
//        }
//    }
//
//    /**
//     * Save the state of the {@code BitSet} instance to a stream (i.e.,
//     * serialize it).
//     */
//    private void writeObject(ObjectOutputStream s)
//            throws IOException {
//
//        checkInvariants();
//
//        if (! sizeIsSticky)
//            trimToSize();
//
//        ObjectOutputStream.PutField fields = s.putFields();
//        fields.put("bits", words);
//        s.writeFields();
//    }
//
//    /**
//     * Reconstitute the {@code BitSet} instance from a stream (i.e.,
//     * deserialize it).
//     */
//    private void readObject(ObjectInputStream s)
//            throws IOException, ClassNotFoundException {
//
//        ObjectInputStream.GetField fields = s.readFields();
//        words = (long[]) fields.get("bits", null);
//
//        // Assume maximum length then find real length
//        // because recalculateWordsInUse assumes maintenance
//        // or reduction in logical size
//        wordsInUse = words.length;
//        recalculateWordsInUse();
//        sizeIsSticky = (words.length > 0 && words[words.length-1] == 0L); // heuristic
//        checkInvariants();
//    }
//
//    /**
//     * Returns a string representation of this bit set. For every index
//     * for which this {@code BitSet} contains a bit in the set
//     * state, the decimal representation of that index is included in
//     * the result. Such indices are listed in order from lowest to
//     * highest, separated by ",&nbsp;" (a comma and a space) and
//     * surrounded by braces, resulting in the usual mathematical
//     * notation for a set of integers.
//     *
//     * <p>Example:
//     * <pre>
//     * BitSet drPepper = new BitSet();</pre>
//     * Now {@code drPepper.toString()} returns "{@code {}}".
//     * <pre>
//     * drPepper.set(2);</pre>
//     * Now {@code drPepper.toString()} returns "{@code {2}}".
//     * <pre>
//     * drPepper.set(4);
//     * drPepper.set(10);</pre>
//     * Now {@code drPepper.toString()} returns "{@code {2, 4, 10}}".
//     *
//     * @return a string representation of this bit set
//     */
//    public String toString() {
//        checkInvariants();
//
//        int numBits = (wordsInUse > 128) ?
//                cardinality() : wordsInUse * BITS_PER_WORD;
//        StringBuilder b = new StringBuilder(6*numBits + 2);
//        b.append('{');
//
//        int i = nextSetBit(0);
//        if (i != -1) {
//            b.append(i);
//            for (i = nextSetBit(i+1); i >= 0; i = nextSetBit(i+1)) {
//                int endOfRun = nextClearBit(i);
//                do { b.append(", ").append(i); }
//                while (++i < endOfRun);
//            }
//        }
//
//        b.append('}');
//        return b.toString();
//    }

}