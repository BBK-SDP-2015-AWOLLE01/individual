package sml;

/**
 * This class implements the branch-if-not-zero instruction in SML
 * 
 * @author Alex Wollenschlaeger
 */

public class BnzInstruction extends Instruction {

	private int op1;
	private String label2;

	public BnzInstruction(String label, String op) {
		super(label, op);
	}

	public BnzInstruction(String label, int op1, String label2) {
		this(label, "bnz");
		this.op1 = op1;
		this.label2 = label2;
	}

	@Override
	public void execute(Machine m) throws IllegalArgumentException {
		if (m.getLabels().indexOf(label2) == -1) {
			throw new IllegalArgumentException("Instruction not found.");
		}
		
		if (m.getRegisters().getRegister(op1) != 0) {
			m.setPc(m.getLabels().indexOf(label2));
		}
	}

	@Override
	public String toString() {
		return super.toString() + " to " + label2;
	}
}
