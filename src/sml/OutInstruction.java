package sml;

/**
 * This class implements the division instruction in SML
 * 
 * @author Alex Wollenschlaeger
 */

public class OutInstruction extends Instruction {

	private int register;
	private int value;

	public OutInstruction(String label, String op) {
		super(label, op);
	}

	public OutInstruction(String label, int register) {
		this(label, "out");
		this.register = register;
	}

	@Override
	public void execute(Machine m) {
		this.value = m.getRegisters().getRegister(register);
		System.out.println(value);
	}

	@Override
	public String toString() {
		return super.toString() + " register " + register + " value is " + value;
	}
}