package sml;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DivInstructionTest {

	private Machine m;
	
	@Before
	public void setUp() throws Exception {
		m = new Machine();
		m.setRegisters(new Registers());
	}

	@Test
	public void DivInstructionCorrectlyDividesTwoOperands() {
		int s1 = 20;
		int s2 = 21;
		int r = 22;
		int op1 = 3;
		int op2 = 4;
		String l1 = "f1";
		m.getRegisters().setRegister(s1, op1);
		m.getRegisters().setRegister(s2, op2);
		Instruction ins = new DivInstruction(l1, r, s1, s2);
		ins.execute(m);
		int actual = m.getRegisters().getRegister(r);
		assertEquals(op1 / op2, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void DivInstructionThrowsExceptionWhenDivisorIsZero() {
		int s1 = 20;
		int s2 = 21;
		int r = 22;
		int op1 = 3;
		int op2 = 0;
		String l1 = "f1";
		m.getRegisters().setRegister(s1, op1);
		m.getRegisters().setRegister(s2, op2);
		Instruction ins = new DivInstruction(l1, r, s1, s2);
		ins.execute(m);
	}
}
