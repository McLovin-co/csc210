import java.util.ArrayList;

public class DNAProcessor {

    public static ArrayList<String> DNAToCodons(String dna) {
        ArrayList<String> codons = new ArrayList<>();
        for (int i = 0; i < dna.length(); i = i + 3) {
            String codon = dna.substring(i, i + 3);
            codons.add(codon);

        }
        return codons;
    }

    public static String CodonToAminoAcid(String codon) {
        if (codon.equals("TTT") || codon.equals("TTC")) return "F";
        if (codon.equals("TTA") || codon.equals("TTG") || codon.equals("CTT") ||
                codon.equals("CTC") || codon.equals("CTA") || codon.equals("CTG")) return "L";
        if (codon.equals("ATT") || codon.equals("ATC") || codon.equals("ATA")) return "I";
        if (codon.equals("ATG")) return "M";
        if (codon.equals("GTT") || codon.equals("GTC") || codon.equals("GTA") || codon.equals("GTG")) return "V";
        if (codon.equals("TCT") || codon.equals("TCC") || codon.equals("TCA") ||
                codon.equals("TCG") || codon.equals("AGT") || codon.equals("AGC")) return "S";
        if (codon.equals("CCT") || codon.equals("CCC") || codon.equals("CCA") || codon.equals("CCG")) return "P";
        if (codon.equals("ACT") || codon.equals("ACC") || codon.equals("ACA") || codon.equals("ACG")) return "T";
        if (codon.equals("GCT") || codon.equals("GCC") || codon.equals("GCA") || codon.equals("GCG")) return "A";
        if (codon.equals("TAT") || codon.equals("TAC")) return "Y";
        if (codon.equals("TAA") || codon.equals("TAG") || codon.equals("TGA")) return "Stop";
        if (codon.equals("CAT") || codon.equals("CAC")) return "H";
        if (codon.equals("CAA") || codon.equals("CAG")) return "Q";
        if (codon.equals("AAT") || codon.equals("AAC")) return "N";
        if (codon.equals("AAA") || codon.equals("AAG")) return "K";
        if (codon.equals("GAT") || codon.equals("GAC") || codon.equals("GAA") || codon.equals("GAG")) return "D";
        if (codon.equals("TGT") || codon.equals("TGC")) return "C";
        if (codon.equals("TGG")) return "W";
        if (codon.equals("CGT") || codon.equals("CGC") || codon.equals("CGA") ||
                codon.equals("CGG") || codon.equals("AGA") || codon.equals("AGG")) return "R";
        if (codon.equals("GGT") || codon.equals("GGC") || codon.equals("GGA") || codon.equals("GGG")) return "G";
        return "Unknown";
    }

    public static ArrayList<String> dna_to_amino_acid(String dna) {
        ArrayList<String> codons = DNAToCodons(dna);
        ArrayList<String> aminoAcids = new ArrayList<>();
        for (String codon : codons) {
            String aminoAcid = CodonToAminoAcid(codon);  // ← HERE
            aminoAcids.add(aminoAcid);                    // ← HERE
        }
        return aminoAcids;
    }

    public static void main(String[] args) {
        String DNA1 = "CTGATATTGTATCCGGCCGAA";
        String DNA2 = "CTAGCCGGTGGTTATTAATAGTAAACTATTCCA";
        String DNA3 = "TTAATCCTCTACCCCGCAGAG";

        // Convert each to amino acids
        ArrayList<String> amino1 = dna_to_amino_acid(DNA1);
        ArrayList<String> amino2 = dna_to_amino_acid(DNA2);
        ArrayList<String> amino3 = dna_to_amino_acid(DNA3);

        // Compare DNA1 vs DNA2
        if (is_match(amino1, amino2)) {
            System.out.println("DNA1 and DNA2 produce identical amino acid sequences");
        } else {
            System.out.println("DNA1 and DNA2 produce different amino acid sequences");
        }

        // Compare DNA1 vs DNA3
        if (is_match(amino1, amino3)) {
            System.out.println("DNA1 and DNA3 produce identical amino acid sequences");
        } else {
            System.out.println("DNA1 and DNA3 produce different amino acid sequences");
        }

        // Compare DNA2 vs DNA3
        if (is_match(amino2, amino3)) {
            System.out.println("DNA2 and DNA3 produce identical amino acid sequences");
        } else {
            System.out.println("DNA2 and DNA3 produce different amino acid sequences");
        }
    }
    public static boolean is_match(ArrayList<String> amino_seq1, ArrayList<String> amino_seq2) {
        return amino_seq1.equals(amino_seq2);
    }
}
