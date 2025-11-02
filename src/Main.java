import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("Erreur: arguments incorrects.");
            System.out.println("Usage: java Main <TYPE> <nom_du_fichier>");
            System.out.println("Types valides: NO_OVERLAP, WITH_OVERLAP, OVERFLOW_WITH_OVERLAP, OVERFLOW_NO_OVERLAP");
            return;
        }

        BitPackerFactory.CompressionType type;
        try {
            type = BitPackerFactory.CompressionType.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: Le type '" + args[0] + "' n'existe pas.");
            return;
        }

        String filename = args[1];
        int[] originalData;

        ArrayList<Integer> numbersList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                numbersList.add(Integer.parseInt(line.trim()));
            }
        } catch (IOException e) {
            System.out.println("Erreur: impossible de lire le fichier '" + filename + "'.");
            return;
        } catch (NumberFormatException e) {
            System.out.println("Erreur: le fichier contient une ligne qui n'est pas un entier.");
            return;
        }

        originalData = numbersList.stream().mapToInt(i -> i).toArray();


        System.out.println("--- Lancement de la compression ---");
        System.out.println("Données chargées depuis '" + filename + "'.");
        System.out.println("Taille d'origine: " + originalData.length + " entiers");
        System.out.println("Mode de compression: " + type);

        BitPacker packer = BitPackerFactory.create(type);

        System.out.println("\n... Compression en cours ...");
        packer.compress(originalData);

        int compressedSize = packer.getCompressedData().length;

        if (packer instanceof BitPackerOverflow) {
            compressedSize += ((BitPackerOverflow) packer).getCompressedOverflow().length;
        }
        System.out.println("Taille compressée: " + compressedSize + " entiers");

        // Test get
        System.out.println("\n--- Test: Accès Direct get(i) ---");
        int index = Math.min(3, originalData.length - 1);
        int val = packer.get(index);
        System.out.println("Récupération de get(" + index + "): " + val);
        assert(val == originalData[index]);

        // Décompression
        System.out.println("\n--- Test: Décompression complète ---");
        int[] decompressedData = new int[originalData.length];
        packer.decompress(decompressedData);

        // Vérification données décompressées
        boolean success = Arrays.equals(originalData, decompressedData);
        if (success) {
            System.out.println("\nRésultat: SUCCÈS ! Données identiques.");
        } else {
            System.out.println("\nRésultat: ÉCHEC ! Données différentes.");
            System.out.println("Origine:  " + Arrays.toString(originalData));
            System.out.println("Décomp: " + Arrays.toString(decompressedData));
        }
    }
}