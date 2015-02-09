package sml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/*
 * The translator of a <b>S</b><b>M</b>al<b>L</b> program.
 */
public class Translator {

	// word + line is the part of the current line that's not yet processed
	// word has no whitespace
	// If word and line are not empty, line begins with whitespace
	private String line = "";
	private Labels labels; // The labels of the program being translated
	private ArrayList<Instruction> program; // The program to be created
	private String fileName; // source file of SML code

	private static final String SRC = "src";

	public Translator(String fileName) {
		this.fileName = SRC + "/" + fileName;
	}

	// translate the small program in the file into lab (the labels) and
	// prog (the program)
	// return "no errors were detected"
	public boolean readAndTranslate(Labels lab, ArrayList<Instruction> prog) {

		try (Scanner sc = new Scanner(new File(fileName))) {
			// Scanner attached to the file chosen by the user
			labels = lab;
			labels.reset();
			program = prog;
			program.clear();

			try {
				line = sc.nextLine();
			} catch (NoSuchElementException ioE) {
				return false;
			}

			// Each iteration processes line and reads the next line into line
			while (line != null) {
				// Store the label in label
				String label = scan();

				if (label.length() > 0) {
					Instruction ins = getInstruction(label);
					if (ins != null) {
						labels.addLabel(label);
						program.add(ins);
					}
				}

				try {
					line = sc.nextLine();
				} catch (NoSuchElementException ioE) {
					return false;
				}
			}
		} catch (IOException ioE) {
			System.out.println("File: IO error " + ioE.getMessage());
			return false;
		}
		return true;
	}

	// line should consist of an MML instruction, with its label already
	// removed. Translate line into an instruction with label label
	// and return the instruction
	public Instruction getInstruction(String label) {
		int s1; // Possible operands of the instruction
		int s2;
		int r;
		int x;
		String L2;

		if (line.equals(""))
			return null;
		
		/*
		 * Create the fully qualified classname for the instruction and
		 * then obtain a Class object. Next we get all the constructors declared
		 * for the class.
		 * 
		 * To create a new instance of the class, we first find the constructor with more than
		 * two parameters, since this corresponds to the full instruction rather than the
		 * Instruction(String, String) constructor that does not contain parameters.
		 * 
		 * We will also need to check for two-parameter constructors with signature
		 * Instruction(String, int), such as OutInstruction.
		 */
		
		/*
		 * Create a fully qualified class name for the instruction
		 * Declare Class and Constructor variables
		 */
		String ins = scan();
		String insClassName = "sml." + ins.substring(0,1).toUpperCase() + ins.substring(1).toLowerCase() + "Instruction";
		Class<?> insClass;
		Constructor<?> insConstructor;
		
		/*
		 * Attempt to create an instance of the class
		 * 
		 * Note that currently we are using reflection to capture the constructor signatures
		 * that are present in the current Instruction subclass constructors
		 */
		try {
			insClass = Class.forName(insClassName);
			Constructor<?>[] allConstr = insClass.getConstructors();
			for (Constructor<?> constr : allConstr) {
				if (constr.getParameterTypes().length == 2 &&
						constr.getParameterTypes()[1].equals(int.class)) {
					// need to make sure we call the constructor and not the call to the superclass constructor
					// needed for OutInstruction
					insConstructor = insClass.getConstructor(new Class<?>[]{String.class, int.class});
					s1 = scanInt();
					return (Instruction)insConstructor.newInstance(label, s1);
				} else if (constr.getParameterTypes().length == 3 &&
						constr.getParameterTypes()[2].equals(int.class)) {
					// this is a 3-parameter constructor with two integers, e.g. for LinInstruction 
					insConstructor = insClass.getConstructor(new Class<?>[]{String.class, int.class, int.class});
					r = scanInt();
					x = scanInt();
					return (Instruction)insConstructor.newInstance(label, r, x);
				} else if (constr.getParameterTypes().length == 3 &&
						constr.getParameterTypes()[2].equals(String.class)) {
					// this is a 3-parameter constructor with an integer and a string, e.g. for BnzInstruction
					insConstructor = insClass.getConstructor(new Class<?>[]{String.class, int.class, String.class});
					s1 = scanInt();
					L2 = scan();
					return (Instruction)insConstructor.newInstance(label, s1, L2);
				} else if (constr.getParameterTypes().length == 4) {
					// this is a 4-parameter constructor with three integers, e.g. for AddInstruction
					insConstructor = insClass.getConstructor(new Class<?>[]{String.class, int.class, int.class, int.class});
					r = scanInt();
					s1 = scanInt();
					s2 = scanInt();
					return (Instruction)insConstructor.newInstance(label, r, s1, s2);
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Class " + insClassName + "not found.");
		} catch (SecurityException e) {
			System.out.println("Access to constructors for class " + insClassName + " denied.");
		} catch (NoSuchMethodException e) {
			System.out.println("No matching constructor found.");
		} catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
			System.out.println("Could not call constructor.");
		}
		return null;
		
		/*
		 * This is the old switch expression-based approach that directly instantiates Instruction subclasses
		 * 
		String ins = scan();
		switch (ins) {
		case "add":
			r = scanInt();
			s1 = scanInt();
			s2 = scanInt();
			return new AddInstruction(label, r, s1, s2);
		case "sub":
			r = scanInt();
			s1 = scanInt();
			s2 = scanInt();
			return new SubInstruction(label, r, s1, s2);
		case "mul":
			r = scanInt();
			s1 = scanInt();
			s2 = scanInt();
			return new MulInstruction(label, r, s1, s2);
		case "div":
			r = scanInt();
			s1 = scanInt();
			s2 = scanInt();
			return new DivInstruction(label, r, s1, s2);
		case "out":
			s1 = scanInt();
			return new OutInstruction(label, s1);
		case "lin":
			r = scanInt();
			x = scanInt();
			return new LinInstruction(label, r, x);
		case "bnz":
			s1 = scanInt();
			L2 = scan();
			return new BnzInstruction(label, s1, L2);
		}
		return null;
		*/
	}


	/*
	 * Return the first word of line and remove it from line. If there is no
	 * word, return ""
	 */
	private String scan() {
		line = line.trim();
		if (line.length() == 0)
			return "";

		int i = 0;
		while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
			i = i + 1;
		}
		String word = line.substring(0, i);
		line = line.substring(i);
		return word;
	}

	// Return the first word of line as an integer. If there is
	// any error, return the maximum int
	private int scanInt() {
		String word = scan();
		if (word.length() == 0) {
			return Integer.MAX_VALUE;
		}

		try {
			return Integer.parseInt(word);
		} catch (NumberFormatException e) {
			return Integer.MAX_VALUE;
		}
	}
}