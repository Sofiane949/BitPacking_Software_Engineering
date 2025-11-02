public class BitPackerOverlap implements BitPacker {

    private int k = 0;
    private int originalLength = 0;
    private int[] compressed;

    public BitPackerOverlap() {
    }

    @Override
    public void compress(int[] input) {
        originalLength = input.length;
        maxK(input);

        if (k == 32) {
            compressed = input.clone();
            return;
        }
        if (originalLength == 0 || k == 0) {
            compressed = new int[0];
            return;
        }

        long totalBits = (long) originalLength * k;

        int compressedLength = (int) Math.ceil((double) totalBits / 32.0);
        compressed = new int[compressedLength];

        long bitPos = 0;

        for (int value : input) {
            int index = (int) (bitPos / 32);
            int shift = (int) (bitPos % 32);

            // On utilise 'long' pour éviter les problèmes de signe
            long longValue = value & 0xFFFFFFFFL; // Valeur max 32 bits

            compressed[index] |= (int) (longValue << shift);

            // On Vérifie si la valeur déborde sur l'entier suivant
            int bitsWritten = 32 - shift;
            if (bitsWritten < k) {
                int nextIndex = index + 1;
                if (nextIndex < compressedLength) {
                    compressed[nextIndex] |= (int) (longValue >>> bitsWritten);
                }
            }

            bitPos += k;
        }
    }

    @Override
    public int get(int i) {
        if (i < 0 || i >= originalLength) {
            throw new IndexOutOfBoundsException();
        }
        if (k == 32) {
            return compressed[i];
        }
        if (k == 0) {
            return 0;
        }

        long bitPos = (long) i * k;

        int index = (int) (bitPos / 32);
        int shift = (int) (bitPos % 32);

        long mask = (1L << k) - 1;

        // On lit compressed[index] comme un long non-signé
        // On décale vers la droite pour amener les bits voulus en position 0
        long value = (compressed[index] & 0xFFFFFFFFL) >>> shift;

        // On Vérifie si on doit lire la suite sur l'entier suivant
        int bitsRead = 32 - shift;
        if (bitsRead < k) {
            // La valeur est à cheval
            int nextIndex = index + 1;
            if (nextIndex < compressed.length) {
                // Lire l'entier suivant
                long highBits = compressed[nextIndex] & 0xFFFFFFFFL;

                value |= (highBits << bitsRead);
            }
        }

        return (int) (value & mask);
    }

    @Override
    public void decompress(int[] output) {
        if (output.length != originalLength) {
            throw new IllegalArgumentException("Le tableau de sortie n'a pas la bonne taille. Attendu: " + originalLength);
        }

        for (int i = 0; i < originalLength; i++) {
            output[i] = get(i);
        }
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