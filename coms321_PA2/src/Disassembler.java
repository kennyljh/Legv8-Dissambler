/**
 * @author kennyljh
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Disassembler {

    /**
     * Maps sorted line code (key) to instructions and possibly branch label (values)
     */
    private Map<Integer, LineCodeData> lineCodeToInstructions = new TreeMap<>();
    private byte[] bytes = null;

    private int currentLine = 0;
    private int labelCount = 0;

    public static void main (String[] arg){

        //String filePath = arg[0];
        String filePath = "C:\\Users\\Kenny\\coms321\\coms321_pa2\\coms321_PA2\\assignment1.legv8asm.machine";
        Disassembler disassembler = new Disassembler();
        disassembler.fileToBytes(filePath);
        disassembler.bytesToInstructions();
        disassembler.printInstructions();
    }

    /**
     * Reads all bytes from a given file
     * @param filePath path of file
     */
    private void fileToBytes(String filePath){

        try {
            Path path = Paths.get(filePath);
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("Usage: ./run.sh <.legv8asm.machine filename>");
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts all bytes to instructions
     */
    private void bytesToInstructions(){

        if (bytes == null){
            System.out.println("No bytes to read?");
            return;
        }

        for (int i = 0; i < bytes.length; i += 4){

            int instructionBitOf32 = ((bytes[i] & 0xff) << 24) |
                                        ((bytes[i + 1] & 0xff) << 16) |
                                        ((bytes[i + 2] & 0xff) << 8) |
                                        (bytes[i + 3] & 0xff);

            String decodedInstruction = decodeInstruction(instructionBitOf32);

            if (lineCodeToInstructions.containsKey(currentLine)){

                LineCodeData existingData = lineCodeToInstructions.get(currentLine);
                existingData.setInstruction(decodedInstruction);
                lineCodeToInstructions.put(currentLine, existingData);
            }
            else {

                LineCodeData newData = new LineCodeData(decodedInstruction, null);
                lineCodeToInstructions.put(currentLine, newData);
            }
            currentLine++;
        }
    }

    /**
     * Decode specific 32-bit instruction
     * @param instructionOf32 32-bit instruction
     * @return decoded instruction
     */
    private String decodeInstruction(int instructionOf32){

        String decodeInstruction = decodeRTypeInstruction(instructionOf32);
        if (decodeInstruction != null) {
            return decodeInstruction;
        }

        decodeInstruction = decodeITypeInstruction(instructionOf32);
        if (decodeInstruction != null) {
            return decodeInstruction;
        }

        decodeInstruction = decodeDTypeInstruction(instructionOf32);
        if (decodeInstruction != null) {
            return decodeInstruction;
        }

        decodeInstruction = decodeBTypeInstruction(instructionOf32);
        if (decodeInstruction != null) {
            return decodeInstruction;
        }

        decodeInstruction = decodeCBTypeInstruction(instructionOf32);
        if (decodeInstruction != null) {
            return decodeInstruction;
        }
        return "INVALID INSTRUCTION";
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
     * @param instructionOf32 32-bit instruction
     * @return decoded R-type instruction
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
     * @param instructionOf32 32-bit instruction
     * @return decoded I-type instruction
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
     * @param instructionOf32 32-bit instruction
     * @return decoded D-type instruction
     */
    private String decodeDTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 21) & 0x7ff;
        int DT_address = (instructionOf32 >> 12) & 0x1ff;
        int op = (instructionOf32 >> 10) & 0x3;
        int Rn = (instructionOf32 >> 5) & 0x1f;
        int Rt = instructionOf32 & 0x1f;

        String RtRnDt = binaryToRegister(Rt) + ", [" + binaryToRegister(Rn) + ", #" + binaryToDecimal(DT_address, 9) + "]";

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
     * @param instructionOf32 32-bit instruction
     * @return decoded B-type instruction
     */
    private String decodeBTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 26) & 0x3f;
        int BR_address = instructionOf32 & 0x3FFFFFF;

        // returning complete instruction
        return switch (opcode) {

            case 0b000101 -> "B " + branchLabelDeterminant(binaryToDecimal(BR_address, 26));
            case 0b100101 -> "BL " + branchLabelDeterminant(binaryToDecimal(BR_address, 26));
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
     * @param instructionOf32 32-bit instruction
     * @return CB-type instruction
     */
    private String decodeCBTypeInstruction(int instructionOf32){

        int opcode = (instructionOf32 >> 24) & 0xff;
        int COND_BR_address = (instructionOf32 >> 5) & 0x7ffff;
        int Rt = instructionOf32 & 0x1f;

        String[] conditions = {"EQ", "NE", "HS", "LO", "MI", "PL", "VS",
                                "VC", "HI", "LS", "GE", "LT", "GT", "LE"};

        String condition = conditions[Rt];

        // returning complete instruction
        return switch (opcode) {

            case 0b01010100 -> "B." + condition + " " + branchLabelDeterminant(binaryToDecimal(COND_BR_address, 19));
            case 0b10110101 -> "CBNZ " + binaryToRegister(Rt) + ", " + branchLabelDeterminant(binaryToDecimal(COND_BR_address, 19));
            case 0b10110100 -> "CBZ " + binaryToRegister(Rt) + ", " + branchLabelDeterminant(binaryToDecimal(COND_BR_address, 19));
            default -> null;
        };
    }

    /**
     * Determines the appropriate branch label for current instruction
     * @param instructionJump instruction jump distance
     * @return branch label
     */
    private String branchLabelDeterminant(int instructionJump){

        int lineJump = currentLine + instructionJump;

        // if line code already exists
        if (lineCodeToInstructions.containsKey(lineJump)){

            LineCodeData data = lineCodeToInstructions.get(lineJump);

            String branchLabel = data.getBranchLabel();

            // no pre-existing label
            if (branchLabel == null){

                String newLabel = "Label" + labelCount;
                labelCount++;

                data.setBranchLabel(newLabel);
                lineCodeToInstructions.put(lineJump, data);

                return newLabel;
            }
            return branchLabel;
        }
        else {

            String newLabel = "Label" + labelCount;
            labelCount++;

            LineCodeData newData = new LineCodeData(null, newLabel);
            lineCodeToInstructions.put(lineJump, newData);

            return newLabel;
        }
    }

    /**
     * Converts binary to register
     * @param binary given binary
     * @return register
     */
    private String binaryToRegister (int binary){

        return switch (binary) {
            case 16 -> "IP0";
            case 17 -> "IP1";
            case 28 -> "SP";
            case 29 -> "FP";
            case 30 -> "LR";
            case 31 -> "XZR";
            default -> "X" + binary;
        };
    }

    /**
     * Converts binary to immediate
     * @param binary given binary
     * @return immediate
     */
    private String binaryToImmediate (int binary){

        return "#" + binary;
    }

    /**
     * Convert binary to decimal (Two's complement)
     * @param binary given binary
     * @param binarySize given binary size
     * @return decimal
     */
    private int binaryToDecimal (int binary, int binarySize){

        // negative
        if (((binary >> (binarySize - 1)) & 0x1) != 0){

            return binary | (-(1 << binarySize));
        }
        return binary;
    }

    /**
     * Outputs all instructions into console
     */
    private void printInstructions(){

        for (Integer line : lineCodeToInstructions.keySet()){

            LineCodeData data = lineCodeToInstructions.get(line);

            if (data.getBranchLabel() != null){
                System.out.println("\n" + data.getBranchLabel() + ":");
                System.out.println("    " + data.getInstruction());
            }
            else {
                System.out.println("    " + data.getInstruction());
            }
        }
    }
}