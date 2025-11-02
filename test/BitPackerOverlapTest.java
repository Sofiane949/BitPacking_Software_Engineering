import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitPackerOverlapTest {

    @org.junit.jupiter.api.Test
    void compress() {
        int[] a = {0,1,2};
        BitPackerOverlap bp = new BitPackerOverlap();
        bp.compress(a);
        assertEquals (2, bp.getK());
        assertEquals(1, bp.getCompressed().length);
        int[] b = {15,14,255,21,1};
        bp.compress(b);
        assertEquals (8, bp.getK());
        assertEquals (2, bp.getCompressed().length);
        assertEquals("10101111111110000111000001111", Integer.toBinaryString(bp.getCompressed()[0]));
        int[] c = {15,14,256,21,1};
        bp.compress(c);
        assertEquals (9, bp.getK());
        assertEquals (2, bp.getCompressed().length);
        assertEquals("10101100000000000001110000001111", Integer.toBinaryString(bp.getCompressed()[0]));
        assertEquals("10000", Integer.toBinaryString(bp.getCompressed()[1]));
        int[] d = {15,14,256,21,1,2,3};
        bp.compress(d);
        assertEquals (9, bp.getK());
        assertEquals (2, bp.getCompressed().length);
        assertEquals("10101100000000000001110000001111", Integer.toBinaryString(bp.getCompressed()[0]));
        assertEquals("110000000100000000010000", Integer.toBinaryString(bp.getCompressed()[1]));
    }

    @org.junit.jupiter.api.Test
    void decompress() {
        int[] input = {15,14,255,21,3012};
        BitPackerOverlap bp = new BitPackerOverlap();
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
        BitPackerOverlap bp = new BitPackerOverlap();
        bp.compress(a);
        assert (bp.get(0) == 15 && bp.get(1) == 14 && bp.get(2) == 255 && bp.get(3) == 21 && bp.get(4) == 3012);
    }

    @Test
    void maxK() {
        int[] a = {0,1,2};
        int[] b = {0,1,2,8,1023};
        int[] d = {0,1,2,8,1024};
        int[] c = {0,1,2,8,1023,2047};
        BitPackerOverlap bp = new BitPackerOverlap();
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