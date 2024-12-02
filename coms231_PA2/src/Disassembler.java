import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author kennyljh
 */

public class Disassembler {

    /**
     * Maps sorted line code (key) to instructions and possibly branch label (values)
     */
    private Map<Integer, LineCodeData> lineCodeToInstructions = new TreeMap<>();
    private byte[] bytes = null;

    private int currentLine = 0;
    private int labelCount = 0;


    public static void main (String[] arg){

        if (arg.length != 1){

            System.out.println("Usage instruction: java Disassembler <binary file>");
            return;
        }

        String filePath = arg[0];
        Disassembler disassembler = new Disassembler();
        disassembler.fileToBytes(filePath);
        disassembler.bytesToInstructions();
    }


    /**
     * Takes 4 bytes at a time
     * @param filePath
     */
    private void fileToBytes(String filePath){

        try {
            Path path = Paths.get(filePath);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void bytesToInstructions(){

        if (bytes == null){
            System.out.println("No bytes?");
            return;
        }

        for (int i = 0; i < bytes.length; i += 4){

            currentLine = i;

            int instructionBitOf32 = ((bytes[i] << 24) |
                                        (bytes[i + 1] << 16) |
                                        (bytes[i + 2 << 8]) |
                                        (bytes[i + 3]));

            decodeInstruction(instructionBitOf32);
        }


    }

    private void decodeInstruction(int instructionOf32){

        if (decodeRTypeInstruction(instructionOf32) != null) {
            return;
        }

        if (decodeITypeInstruction(instructionOf32) != null) {
            return;
        }

        if (decodeDTypeInstruction(instructionOf32) != null) {
            return;
        }

        if (decodeBTypeInstruction(instructionOf32) != null) {
            return;
        }

        if (decodeCBTypeInstruction(instructionOf32) != null) {
            return;
        }
    }

    /**
     * Decodes R-type instructions:
     *     ADD
     *     AND
     *     BR
     *     EOR
     *     LSL
     *     LSR
     *     ORR
     *     SUB
     *     SUBS
     *     MUL
     *     // extras
     *     PRNT
     *     PRNL
     *     DUMP
     *     HALT
     *
     * @param instructionOf32
     * @return
     */
    private String decodeRTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 21) & 0x7ff;
        int Rm = (instructionOf32 >> 16) & 0x1f;
        int shamt = (instructionOf32 >> 10) & 0x3f;
        int Rn = (instructionOf32 >> 5) & 0x1f;
        int Rd = instructionOf32 & 0x1f;

        String RdRnRm = binaryToRegister(Rd) + ", " + binaryToRegister(Rn) + ", " + binaryToRegister(Rm);
        String RdRnShamt = binaryToRegister(Rd) + ", " + binaryToRegister(Rn) + ", " + binaryToImmediate(shamt);

        // returning complete instruction
        return switch (opcode) {

            case 0b10001011000 -> "ADD " + RdRnRm;
            case 0b10001010000 -> "AND " + RdRnRm;
            case 0b11010110000 -> "BR " + binaryToRegister(Rn);
            case 0b11001010000 -> "EOR " + RdRnRm;
            case 0b11010011011 -> "LSL " + RdRnShamt;
            case 0b11010011010 -> "LSR " + RdRnShamt;
            case 0b10101010000 -> "ORR " + RdRnRm;
            case 0b11001011000 -> "SUB " + RdRnRm;
            case 0b11101011000 -> "SUBS " + RdRnRm;
            case 0b10011011000 -> "MUL " + RdRnRm;
            case 0b11111111101 -> "PRNT";
            case 0b11111111100 -> "PRNL";
            case 0b11111111110 -> "DUMP";
            case 0b11111111111 -> "HALT";
            default -> null;
        };
    }

    /**
     * Decode I-type instructions:
     *     ADDI
     *     ANDI
     *     EORI
     *     ORRI
     *     SUBI
     *     SUBIS
     *
     * @param instructionOf32
     * @return
     */
    private String decodeITypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 22) & 0x3ff;
        int aluImm = (instructionOf32 >> 10) & 0xfff;
        int Rn = (instructionOf32 >> 5) & 0x1f;
        int Rd = instructionOf32 & 0x1f;

        String RdRnImm = binaryToRegister(Rd) + ", " + binaryToRegister(Rn) + ", " + binaryToImmediate(aluImm);

        // returning complete instruction
        return switch (opcode) {

            case 0b1001000100 -> "ADDI " + RdRnImm;
            case 0b1001001000 -> "ANDI " + RdRnImm;
            case 0b1101001000 -> "EORI " + RdRnImm;
            case 0b1011001000 -> "ORRI " + RdRnImm;
            case 0b1101000100 -> "SUBI " + RdRnImm;
            case 0b1111000100 -> "SUBIS " + RdRnImm;
            default -> null;
        };
    }

    /**
     * Decode D-type instructions:
     *     LDUR
     *     STUR
     *
     * @param instructionOf32
     * @return
     */
    private String decodeDTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 21) & 0x7ff;
        int DT_address = (instructionOf32 >> 12) & 0xffff;
        int op = (instructionOf32 >> 10) & 0x3;
        int Rn = (instructionOf32 >> 5) & 0x1f;
        int Rt = instructionOf32 & 0x1f;

        String RtRnDt = binaryToRegister(Rt) + ", [" + binaryToRegister(Rn) + ", " + binaryToImmediate(DT_address) + "]";

        // returning complete instruction
        return switch (opcode) {

            case 0b11111000010 -> "LDUR " + RtRnDt;
            case 0b11111000000 -> "STUR " + RtRnDt;
            default -> null;
        };
    }

    /**
     * Decodes B-type instructions:
     *     B
     *     BL
     *
     * @param instructionOf32
     * @return
     */
    private String decodeBTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 26) & 0x3f;
        int BR_address = instructionOf32 & 0x3FFFFFF;

        String branchLabel = branchLabelDeterminant(binaryToDecimal(BR_address, 26));

        // returning complete instruction
        return switch (opcode) {

            case 0b000101 -> "B " + branchLabel;
            case 0b100101 -> "BL " + branchLabel;
            default -> null;
        };
    }

    /**
     * Decodes CB-type instructions:
     *     B.cond:
     *       0: EQ
     *       1: NE
     *       2: HS
     *       3: LO
     *       4: MI
     *       5: PL
     *       6: VS
     *       7: VC
     *       8: HI
     *       9: LS
     *       a: GE
     *       b: LT
     *       c: GT
     *       d: LE
     *     CBNZ
     *     CBZ
     *
     * @param instructionOf32
     * @return
     */
    private String decodeCBTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 24) & 0xff;
        int COND_BR_address = (instructionOf32 >> 5) & 0x7ffff;
        int Rt = instructionOf32 & 0x1f;

        String[] conditions = {"EQ", "NE", "HS", "LO", "MI", "PL", "VS",
                                "VC", "HI", "LS", "GE", "LT", "GT", "LE"};

        String condition = conditions[Rt];

        String branchLabel = branchLabelDeterminant(binaryToDecimal(COND_BR_address, 19));

        // returning complete instruction
        return switch (opcode) {

            case 0b01010100 -> "B." + condition + " " + branchLabel;
            case 0b10110101 -> "CBNZ " + branchLabel;
            case 0b10110100 -> "CBZ " + branchLabel;
            default -> null;
        };
    }

    /**
     * @param instructionJump
     * @return
     */
    private String branchLabelDeterminant(int instructionJump){

        int lineJump = currentLine + instructionJump;

        // if line code already exists
        if (lineCodeToInstructions.containsKey(lineJump)){

            LineCodeData data = lineCodeToInstructions.get(lineJump);

            String branchLabel = data.getBranchLabel();

            // no pre-existing label
            if (branchLabel == null){

                String newLabel = "Label" + labelCount + ":";
                labelCount++;

                data.setBranchLabel(newLabel);
                lineCodeToInstructions.put(lineJump, data);

                return newLabel;
            }
            return branchLabel;
        }
        else {

            String newLabel = "Label" + labelCount + ":";
            labelCount++;

            LineCodeData newData = new LineCodeData(null, newLabel);
            lineCodeToInstructions.put(lineJump, newData);

            return newLabel;
        }
    }

    /**
     * Converts binary to register
     * @param binary
     * @return
     */
    private String binaryToRegister (int binary){

        int decimal = 0;
        int position = 0;

        while (binary > 0){

            int bit = binary & 0x1;
            decimal += bit * Math.pow(2, position);

            binary = binary >> 1;
            position++;
        }

        return switch (decimal) {
            case 16 -> "IP0";
            case 17 -> "IP1";
            case 28 -> "SP";
            case 29 -> "FP";
            case 30 -> "LR";
            case 31 -> "XZR";
            default -> "X" + decimal;
        };
    }

    /**
     * Converts binary to immediate
     * @param binary
     * @return
     */
    private String binaryToImmediate (int binary){

        int decimal = 0;
        int position = 0;

        while (binary > 0){

            int bit = binary & 0x1;
            decimal += bit * Math.pow(2, position);

            binary = binary >> 1;
            position++;
        }
        return "#" + decimal;
    }

    /**
     * Convert binary to decimal (Two's complement)
     * @param binary
     * @param binarySize
     * @return
     */
    private int binaryToDecimal (int binary, int binarySize){

        int decimal = 0;
        int position = 0;

        // negative
        if (((binary >> (binarySize - 1)) & 0x1) != 0){

            binary = ~binary + 1;

            while (binary > 0){

                int bit = binary & 0x1;
                decimal += bit * Math.pow(2, position);

                binary = binary >> 1;
                position++;
            }
            return -decimal;
        }
        else {

            while (binary > 0){

                int bit = binary & 0x1;
                decimal += bit * Math.pow(2, position);

                binary = binary >> 1;
                position++;
            }
            return decimal;
        }
    }
}

