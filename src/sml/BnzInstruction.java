package sml;

/**
 * This class implements the branch-if-not-zero instruction in SML
 * 
 * @author someone
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
	public void execute(Machine m) {
		if (m.getRegisters().getRegister(op1) != 0) {
			m.setPc(m.getLabels().indexOf(label2));
		}
	}

	@Override
	public String toString() {
		// TODO
		return super.toString();
	}
}
