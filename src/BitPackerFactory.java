public class BitPackerFactory {

    // On définit les types de compression possibles ici
    // C'est plus propre de le mettre dans la Factory.
    public enum CompressionType {
        NO_OVERLAP,
        WITH_OVERLAP,
        OVERFLOW_NO_OVERLAP, // Overflow qui utilise NoOverlap en interne
        OVERFLOW_WITH_OVERLAP  // Overflow qui utilise Overlap en interne
    }

    public static BitPacker create(CompressionType type) {
        switch (type) {
            case NO_OVERLAP:
                return new BitPackerNoOverlap();

            case WITH_OVERLAP:
                return new BitPackerOverlap();

            case OVERFLOW_NO_OVERLAP:
                BitPacker noOverlapPacker = new BitPackerNoOverlap();
                return new BitPackerOverflow(noOverlapPacker);

            case OVERFLOW_WITH_OVERLAP:
                BitPacker overlapPacker = new BitPackerOverlap();
                return new BitPackerOverflow(overlapPacker);

            default:
                throw new IllegalArgumentException("Type de compression inconnu: " + type);
        }
    }
}