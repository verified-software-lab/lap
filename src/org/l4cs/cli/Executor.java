package org.l4cs.cli;

public class Executor {

	private static void execute_PL(CommandLine cl) {
		switch (cl.command) {
		case "help":
			new Help(cl).execute();
			break;
		case "cnf":
			Cnf.execute(cl);
			break;
		case "dnf":
			Dnf.execute(cl);
			break;
		case "nnf":
			Nnf.execute(cl);
			break;
		case "equiv":
			Equiv.execute(cl);
			break;
		case "tseytin":
			Tseytin.execute(cl);
			break;
		case "sat":
			Sat.execute(cl);
			break;
		case "dpll":
			Dpll.execute(cl);
			break;
		case "valid":
			Valid.execute(cl);
			break;
		case "check":
			new Check_PL(cl).execute();
			break;
		default:
			cl.clErr("unknown command: " + cl.command);
		}
	}

	private static void execute_FOL(CommandLine cl) {
		switch (cl.command) {
		case "help":
			new Help(cl).execute();
			break;
		case "check":
			Check_FOL.execute(cl);
			break;
		default:
			cl.clErr("unknown command: " + cl.command);
		}
	}

	public static void execute(String[] args) {
		CommandLine cl = new CommandLine(args);
		switch (cl.lang) {
		case PL:
			execute_PL(cl);
			break;
		case FOL:
			execute_FOL(cl);
			break;
		default:
			throw new RuntimeException("unreachable");

		}
	}
}
