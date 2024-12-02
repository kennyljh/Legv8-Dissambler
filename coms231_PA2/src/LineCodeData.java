public class LineCodeData {

    private String instruction;
    private String branchLabel;

    public LineCodeData(String instruction, String branchLabel) {

        this.instruction = instruction;
        this.branchLabel = branchLabel;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getBranchLabel() {
        return branchLabel;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public void setBranchLabel(String branchLabel) {
        this.branchLabel = branchLabel;
    }
}
