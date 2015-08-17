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
            System.out.println(query);
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
            solution.remove(0);
            solution.remove(0);
            for (int i = 0; i < solution.size() - 1; i += 2) {
                String varName = solution.get(i).substring(solution.get(i).indexOf("(define-fun ") + 12).replaceAll("!.*", "");
                int varValuation = Integer.parseInt(solution.get(i + 1).replaceAll("(\\s|\\))", ""));
                normalizedSolution.put(varName, new SMTTerm(varValuation));
            }
            return normalizedSolution;
        }

    }

}
