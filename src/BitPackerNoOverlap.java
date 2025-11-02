public class BitPackerNoOverlap implements BitPacker {
    private int k = 0;
    private int nPerInt = 0;
    private int originalLength = 0;
    private int[] compressed;

    public BitPackerNoOverlap() {
    }
    @Override
    public void compress(int[] input) {
        originalLength = input.length;
        maxK(input);

        if (k == 32) { // Cas où les nombres sont trop grands ou négatifs
            compressed = input.clone(); // Pas de compression possible
            nPerInt = 1;
            return;
        }
        if (k == 0) { // Cas d'un tableau vide
            compressed = new int[0];
            return;
        }

        nPerInt = 32 / k;
        int compressedLength = (int) Math.ceil((double) input.length / nPerInt);
        compressed = new int[compressedLength];
        int i = 0;
        int j = 0;
        for (int a : input) {
            if (j + k > 32) {
                i++;
                j = 0;
            }

            compressed[i] = compressed[i] | (a << j);
            j += k;
        }
    }

    @Override
    public void decompress(int[] output) {

    }

    @Override
    public int get(int i) {
        int index = i / nPerInt;
        int posBits = (i % nPerInt) * k;
        int r = compressed[index] >> posBits;
        int mask = (1 << k) - 1;
        r = r & mask;
        return r;
    }

    public void maxK(int[] tab) {
        k = 0;
        for (int a : tab) {
            int len = Integer.toBinaryString(a).length();
            if (len > k) {
                k = len;
            }
        }
    }

    public int[] getCompressed() {
        return compressed;
    }

    public int getK() {
        return k;
    }
}
