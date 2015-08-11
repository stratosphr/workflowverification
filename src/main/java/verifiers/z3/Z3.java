package verifiers.z3;

import codegeneration.z3.SMTTerm;
import tools.CmdTools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Z3 {

    private static Z3 singleton;

    private Z3() {
    }

    public static Z3 getSingleton() {
        if (singleton == null) {
            singleton = new Z3();
        }
        return singleton;
    }

    public HashMap<String, SMTTerm> query(File file, String query) {
        return (new Z3Query(file, query)).getSolution();
    }

    private class Z3Query {

        private final String query;

        private Z3Query(File file, String query) {
            this.query = "echo \"" + query + "\" | cat " + file.getAbsolutePath() + " /dev/stdin | z3 -in";
        }

        public HashMap<String, SMTTerm> getSolution() {
            try {
                ArrayList<String> solution = CmdTools.executeCommand(query);
                if ("unsat".equals(solution.get(0)) || "unknown".equals(solution.get(0))) {
                    return new HashMap<>();
                } else {
                    return normalizeSolution(solution);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private HashMap<String, SMTTerm> normalizeSolution(ArrayList<String> solution) {
            HashMap<String, SMTTerm> normalizedSolution = new HashMap<>();
            for (String line : solution) {
                System.out.println(line.indexOf("define-fun") + " - Line : " + line);
            }
            return normalizedSolution;
        }

    }

}
