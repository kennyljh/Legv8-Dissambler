import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kennyljh
 */

public class Disassembler {

    private Map<Integer, String> bytesToInstructions = new LinkedHashMap<>();
    byte[] bytes = null;


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

            int instructionBitOf32 = ((bytes[i] << 24) |
                                        (bytes[i + 1] << 16) |
                                        (bytes[i + 2 << 8]) |
                                        (bytes[i + 3]));

            String instructionType = getInstructionType((instructionBitOf32 >> 21) & 0x7ff);

            switch (instructionType){


            }
        }


    }

    /**
     * Determines instruction type from opcode binary literal
     * @param opcodeOfEleven 11 bit opcode
     * @return instruction type
     */
    private String getInstructionType(int opcodeOfEleven){

        int opcodeOfTen = (opcodeOfEleven >> 1) & 0x3ff;
        int opcodeOfEight = (opcodeOfEleven >> 3) & 0xff;
        int opcodeOfSix = (opcodeOfEleven >> 5) & 0x3f;

        if (opcodeOfEleven >= 0b00011110001 && opcodeOfEleven <= 0b11111100010){

            if (opcodeOfEleven >= 0b00111000000 && opcodeOfEleven <= 0b11111000010){
                return "D";
            }
            else {
                return "R";
            }
        }

        if (opcodeOfTen >= 0b1001000100 && opcodeOfTen <= 0b1111001000){
            return "I";
        }

        if (opcodeOfSix >= 0b000101 && opcodeOfSix <= 0b100101){
            return "B";
        }

        if (opcodeOfEight >= 0b01010100 && opcodeOfEight <= 0b10110101){
            return "CB";
        }

        return null;
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

        String instruction;
        String completeInstruction;

        switch (opcode){

            case 0b10001011000:
                instruction = "ADD";
                completeInstruction = (instruction + " " + RdRnRm);

            case 0b10001010000:
                instruction = "AND";
                completeInstruction = (instruction + " " + RdRnRm);

            case 0b11010110000:
                instruction = "BR";
                completeInstruction = (instruction + " X" + binaryToDecimal())
            case 0b11001010000:
                instruction = "EOR";
            case 0b11010011011:
                instruction = "LSL";
            case 0b11010011010:
                instruction = "LSR";
            case 0b10101010000:
                instruction = "ORR";
            case 0b11001011000:
                instruction = "SUB";
            case 0b11101011000:
                instruction = "SUBS";
            case 0b10011011000:
                instruction = "MUL";
            case 0b11111111101:
                instruction = "PRNT";
            case 0b11111111100:
                instruction = "PRNL";
            case 0b11111111110:
                instruction = "DUMP";
            case 0b11111111111:
                instruction = "HALT";
            default:
                completeInstruction = "INVALID";
        }
        return completeInstruction;
    }

    private String decodeITypeInstruction(){}

    private String decodeDTypeInstruction(){}

    private String decodeBTypeInstruction(){}

    private String decodeCBTypeInstruction(){}

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
            case 28 -> "SP";
            case 29 -> "FP";
            case 30 -> "LR";
            case 31 -> "XZR";
            default -> "X" + decimal;
        };
    }



































}

