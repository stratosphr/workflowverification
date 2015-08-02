package verifiers.sicstus;

import codegeneration.sicstus.PlTerm;
import exceptions.UnableToInitializeSicstusException;
import se.sics.jasper.*;

import java.io.File;
import java.util.HashMap;

public class Sicstus {

    private static Sicstus singleton;
    private static Prolog prolog;

    private Sicstus() {
    }

    public static Sicstus getSingleton() {
        if (singleton == null) {
            singleton = new Sicstus();
            try {
                prolog = Jasper.newProlog();
            } catch (InterruptedException e) {
                throw new UnableToInitializeSicstusException(e.toString());
            }
        }
        return singleton;
    }

    public static Prolog getProlog() {
        return prolog;
    }

    public HashMap<String, PlTerm> query(File file, String query) {
        try {
            return (new SicstusQuery(file, query)).getSolution();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private class SicstusQuery {

        private final String query;

        public SicstusQuery(File file, String query) {
            this.query = "consult('" + file.getAbsolutePath() + "'), " + query + (query.endsWith(".") ? "" : ".");
        }

        public HashMap<String, PlTerm> getSolution() {
            try {
                HashMap<String, Term> solution = new HashMap<String, Term>();
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
            HashMap<String, PlTerm> normalizedSolution = new HashMap<String, PlTerm>();
            for (String key : solution.keySet()) {
                normalizedSolution.put(key, PlTerm.parsePlTerm(solution.get(key)));
            }
            return normalizedSolution;
        }

    }

}
