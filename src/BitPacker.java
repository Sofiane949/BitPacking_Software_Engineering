public interface BitPacker {
    void compress(int[] input);
    void decompress(int[] output);
    int get(int i);
}
