import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

// Cette classe "enveloppe" un autre compresseur (Overlap ou NoOverlap)
// pour gérer les grosses valeurs à part.
public class BitPackerOverflow implements BitPacker {
    private int kNormal;
    private int kOverflowIndex;
    private int payloadWidth;   // max(kNormal, kOverflowIndex)
    private int packedK;        // Le 'k' final qu'on va utiliser (1 + payloadWidth)

    private int originalLength;

    // Le compresseur interne qui fera le vrai boulot de packing
    private BitPacker internalPacker;

    // Le tableau qui stocke les "grosses" valeurs
    private int[] compressedOverflow;

    public BitPackerOverflow(BitPacker internalPacker) {
        this.internalPacker = internalPacker;
    }

    @Override
    public void compress(int[] input) {
        this.originalLength = input.length;
        if (input.length == 0) {
            this.internalPacker.compress(new int[0]);
            this.compressedOverflow = new int[0];
            return;
        }

        // On trouve le 'k' de chaque nombre
        int[] k_values = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            k_values[i] = getKForValue(input[i]);
        }

        // On Prend la médiane pour k.
        Arrays.sort(k_values);
        int medianIndex = (int) Math.floor((input.length - 1) * 0.5); // 50e percentile
        this.kNormal = k_values[medianIndex];

        // Si kNormal est 32 on utilise pas l'overflow
        if (this.kNormal == 32) {
            this.packedK = 32;
            this.internalPacker.compress(input);
            this.compressedOverflow = new int[0];
            return;
        }

        // On compte combien de valeurs vont en overflow

        long kNormalLimit = 1L << kNormal;

        HashMap<Integer, Integer> overflowMap = new HashMap<>();
        ArrayList<Integer> overflowList = new ArrayList<>();

        for (int val : input) {
            if (val >= kNormalLimit) {
                if (!overflowMap.containsKey(val)) {
                    overflowMap.put(val, overflowList.size());
                    overflowList.add(val);
                }
            }
        }

        int overflowCount = overflowList.size();
        this.compressedOverflow = overflowList.stream().mapToInt(i -> i).toArray();

        // Nombres de bits pour stocker l'index max
        if (overflowCount == 0) {
            this.kOverflowIndex = 0;
        } else if (overflowCount == 1) {
            this.kOverflowIndex = 1;
        } else {
            this.kOverflowIndex = 32 - Integer.numberOfLeadingZeros(overflowCount - 1);
        }

        this.payloadWidth = Math.max(kNormal, kOverflowIndex);

        this.packedK = 1 + this.payloadWidth;

        int[] mainDataToPack = new int[originalLength];

        for (int i = 0; i < originalLength; i++) {
            int val = input[i];

            if (val >= kNormalLimit) {
                int overflowIndex = overflowMap.get(val);
                mainDataToPack[i] = (1 << payloadWidth) | overflowIndex;
            } else {
                mainDataToPack[i] = val;
            }
        }

        this.internalPacker.compress(mainDataToPack);
    }


    @Override
    public int get(int i) {
        if (i < 0 || i >= originalLength) {
            throw new IndexOutOfBoundsException();
        }

        // Cas spécial où on n'a pas fait d'overflow
        if (this.packedK == 32) {
            return this.internalPacker.get(i);
        }

        int packedValue = this.internalPacker.get(i);

        int flagMask = (1 << payloadWidth);
        boolean isOverflow = (packedValue & flagMask) != 0;

        if (isOverflow) {
            // C'est un index. On efface le drapeau pour ne garder que l'index.
            int payloadMask = flagMask - 1;
            int overflowIndex = packedValue & payloadMask;

            // On retourne la vraie valeur depuis notre tableau d'overflow
            return compressedOverflow[overflowIndex];
        } else {
            return packedValue;
        }
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

    public int[] getCompressedData() {
        return compressedOverflow;
    }

    public int getKForValue(int val) {
        if (val < 0) return 32;
        if (val == 0) return 1;
        return 32 - Integer.numberOfLeadingZeros(val);
    }

    public int[] getCompressedOverflow() {
        return compressedOverflow;
    }
}