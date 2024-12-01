import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kennyljh
 */

public class Disassembler {

    private Map<Integer, String> bytesToInstructions = new HashMap<>();
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


    }

    /**
     * Determines instruciton type from opcode binary literal
     * @param opcodeOfEleven 11 bit opcode
     * @return instruction type
     */
    private String getInstructionType(int opcodeOfEleven){

        int opcodeOfTen = opcodeOfEleven >> 1;
        int opcodeOfEight = opcodeOfEleven >> 3;
        int opcodeOfSix = opcodeOfEleven >> 5;

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


































}

