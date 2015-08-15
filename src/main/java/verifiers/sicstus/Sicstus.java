package verifiers.sicstus;

import codegeneration.sicstus.PlTerm;
import exceptions.UnableToInitializeSicstusException;
import se.sics.jasper.Jasper;
import se.sics.jasper.Prolog;
import se.sics.jasper.Term;

import java.io.File;
import java.util.HashMap;

public class Sicstus {

    private static Sicstus singleton;
    private final Prolog prolog;

    private Sicstus() {
        try {
            prolog = Jasper.newProlog();
        } catch (InterruptedException e) {
            throw new UnableToInitializeSicstusException(e.toString());
        }
    }

    public static Sicstus getSingleton() {
        if (singleton == null) {
            singleton = new Sicstus();
        }
        return singleton;
    }

    public HashMap<String, PlTerm> query(File file, String query) {
        return (new SicstusQuery(file, query)).getSolution();
    }

    private class SicstusQuery {

        private final String query;

        private SicstusQuery(File file, String query) {
            this.query = "consult('" + file.getAbsolutePath() + "'), " + query + (query.endsWith(".") ? "" : ".");
        }

        public HashMap<String, PlTerm> getSolution() {
            try {
                HashMap<String, Term> solution = new HashMap<>();
                synchronized (prolog) {
                    prolog.query(this.query, solution);
                }
                return normalizeSolution(solution);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private HashMap<String, PlTerm> normalizeSolution(HashMap<String, Term> solution) {
            HashMap<String, PlTerm> normalizedSolution = new HashMap<>();
            for (String key : solution.keySet()) {
                normalizedSolution.put(key, PlTerm.parsePlTerm(solution.get(key)));
            }
            return normalizedSolution;
        }

    }

}
