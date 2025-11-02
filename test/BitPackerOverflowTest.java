import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitPackerOverflowTest {

    @Test
    void compress() {
        int[] a = {1, 2, 3, 7, 4};
        BitPackerOverflow bp = new BitPackerOverflow(new BitPackerOverlap());
        bp.compress(a);

        assertEquals(2, bp.getCompressedOverflow().length);
        assert(bp.getCompressedOverflow()[0] == 7);
        assert(bp.getCompressedOverflow()[1] == 4);

        int[] b = {1, 2, 3, 1024, 4, 5, 2048};
        BitPackerOverflow bp2 = new BitPackerOverflow(new BitPackerOverlap());
        bp2.compress(b);

        assertEquals(2, bp2.getCompressedOverflow().length);
        assert(bp2.getCompressedOverflow()[0] == 1024);
        assert(bp2.getCompressedOverflow()[1] == 2048);
    }

    @Test
    void get() {
        int[] input = {1, 2, 3, 1024, 4, 5, 2048, 1024};
        BitPackerOverflow bp = new BitPackerOverflow(new BitPackerOverlap());
        bp.compress(input);

        assert(bp.get(0) == 1);
        assert(bp.get(1) == 2);
        assert(bp.get(2) == 3);
        assert(bp.get(3) == 1024);
        assert(bp.get(4) == 4);
        assert(bp.get(5) == 5);
        assert(bp.get(6) == 2048);
        assert(bp.get(7) == 1024);
    }

    @Test
    void decompress() {
        int[] input = {1, 2, 3, 1024, 4, 5, 2048, 1024};
        BitPackerOverflow bp = new BitPackerOverflow(new BitPackerOverlap());
        bp.compress(input);

        int[] output = new int[input.length];
        bp.decompress(output);

        assertArrayEquals(input, output);
    }

    @Test
    void getKForValue() {
        BitPackerOverflow bp = new BitPackerOverflow(new BitPackerOverlap());
        assert(bp.getKForValue(0) == 1);
        assert(bp.getKForValue(1) == 1);
        assert(bp.getKForValue(2) == 2);
        assert(bp.getKForValue(3) == 2);
        assert(bp.getKForValue(1023) == 10);
        assert(bp.getKForValue(1024) == 11);
        assert(bp.getKForValue(-1) == 32);
    }
}