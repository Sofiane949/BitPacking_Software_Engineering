import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class BitPackerNoOverlapTest {

    @org.junit.jupiter.api.Test
    void compress() {
        int[] a = {0,1,2};
        BitPackerNoOverlap bp = new BitPackerNoOverlap();
        bp.compress(a);
        assertEquals (2, bp.getK());
        assertEquals(1, bp.getCompressed().length);
        int[] b = {15,14,255,21,1};
        bp.compress(b);
        assertEquals (8, bp.getK());
        assertEquals (2, bp.getCompressed().length);
        assertEquals("10101111111110000111000001111", Integer.toBinaryString(bp.getCompressed()[0]));
    }

    @org.junit.jupiter.api.Test
    void decompress() {
        int[] input = {15,14,255,21,3012};
        BitPackerNoOverlap bp = new BitPackerNoOverlap();
        bp.compress(input);
        int[] output = new int[5];
        bp.decompress(output);
        for (int i = 0; i < 5; i++) {
            assertEquals(output[0], input[0]);
        }
    }

    @org.junit.jupiter.api.Test
    void get() {
        int[] a = {15,14,255,21,3012};
        BitPackerNoOverlap bp = new BitPackerNoOverlap();
        bp.compress(a);
        assert (bp.get(0) == 15 && bp.get(1) == 14 && bp.get(2) == 255 && bp.get(3) == 21 && bp.get(4) == 3012);
    }

    @Test
    void maxK() {
        int[] a = {0,1,2};
        int[] b = {0,1,2,8,1023};
        int[] d = {0,1,2,8,1024};
        int[] c = {0,1,2,8,1023,2047};
        BitPackerNoOverlap bp = new BitPackerNoOverlap();
        bp.maxK(a);
        assert(bp.getK() == 2);
        bp.maxK(b);
        assert(bp.getK() == 10);
        bp.maxK(c);
        assert(bp.getK() == 11);
        bp.maxK(d);
        assert(bp.getK() == 11);
    }
}