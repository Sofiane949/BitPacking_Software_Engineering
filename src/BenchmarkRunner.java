import java.util.Random;
import java.util.concurrent.TimeUnit; // Pour convertir nano en milli

public class BenchmarkRunner {

    // Paramètres des tests
    private static final int ARRAY_SIZE = 1_000_000;
    private static final int WARMUP_RUNS = 20;
    private static final int TIMING_RUNS = 50;

    public static void main(String[] args) {
        System.out.println("--- Lancement du Benchmark (Taille Array: " + ARRAY_SIZE + ") ---");
        // CAS 1: Données "petites"
        System.out.println("\nCAS 1: Données petites (max 255)");
        int[] smallData = generateRandomData(ARRAY_SIZE, 255);
        runAllTests(smallData);

        // CAS 2: Données "moyennes"
        System.out.println("\nCAS 2: Données moyennes (max 1 000 000)");
        int[] mediumData = generateRandomData(ARRAY_SIZE, 1_000_000);
        runAllTests(mediumData);

        // CAS 3: Données "Overflow" (99% petites, 1% très grandes)
        System.out.println("\nCAS 3: Données type 'Overflow' (99% petites, 1% grandes)");
        int[] overflowData = generateOverflowData(ARRAY_SIZE, 100, 500_000_000, 0.01);
        runAllTests(overflowData);
    }

    private static void runAllTests(int[] data) {
        // On teste tous nos compresseurs
        runTest(BitPackerFactory.CompressionType.NO_OVERLAP, data);
        runTest(BitPackerFactory.CompressionType.WITH_OVERLAP, data);
        runTest(BitPackerFactory.CompressionType.OVERFLOW_WITH_OVERLAP, data);
    }

    /**
     * Mesure le temps pour un type de compression et un jeu de données.
     */
    private static void runTest(BitPackerFactory.CompressionType type, int[] data) {
        System.out.println("  Testing: " + type);
        BitPacker packer = BitPackerFactory.create(type);

        // WARM-UP
        for (int i = 0; i < WARMUP_RUNS; i++) {
            packer.compress(data);
        }

        // MESURE COMPRESSION
        long totalCompressTime = 0;
        for (int i = 0; i < TIMING_RUNS; i++) {
            long startTime = System.nanoTime();
            packer.compress(data);
            totalCompressTime += (System.nanoTime() - startTime);
        }
        long avgCompressTime = totalCompressTime / TIMING_RUNS;

        // MESURE GET(i) (accès aléatoire)
        long startGet = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            packer.get(ARRAY_SIZE / 2 + i);
        }
        long avgGetTime = (System.nanoTime() - startGet) / 1000;

        // MESURE DECOMPRESSION
        int[] output = new int[data.length];
        long totalDecompressTime = 0;
        for (int i = 0; i < TIMING_RUNS; i++) {
            long startTime = System.nanoTime();
            packer.decompress(output);
            totalDecompressTime += (System.nanoTime() - startTime);
        }
        long avgDecompressTime = totalDecompressTime / TIMING_RUNS;

        System.out.printf("    Compress   : %5d ms\n", TimeUnit.NANOSECONDS.toMillis(avgCompressTime));
        System.out.printf("    Decompress : %5d ms\n", TimeUnit.NANOSECONDS.toMillis(avgDecompressTime));
        System.out.printf("    Get(i)     : %5d ns\n", avgGetTime);
    }


    private static int[] generateRandomData(int size, int maxValue) {
        Random rand = new Random();
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = rand.nextInt(maxValue + 1); // +1 car nextInt est exclusif
        }
        return data;
    }

    /** Crée un tableau avec une majorité de petites valeurs et qqs "outliers". */
    private static int[] generateOverflowData(int size, int normalMax, int outlierMax, double outlierChance) {
        Random rand = new Random();
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            if (rand.nextDouble() < outlierChance) { // ex: 1% de chance
                data[i] = rand.nextInt(outlierMax + 1);
            } else {
                data[i] = rand.nextInt(normalMax + 1);
            }
        }
        return data;
    }
}