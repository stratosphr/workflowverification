package codegeneration.implementations.z3;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.PlPredicateDefinition;
import codegeneration.z3.*;
import petrinets.model.Place;
import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.model.Specification;
import specifications.model.formulas.NegationFormula;
import specifications.visitors.Z3FormulaVisitor;
import tools.Prefixes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class Z3Implementation extends Implementation {

    private SMTVar term_VMax;
    private LinkedHashMap<Place, SMTVar> masTerms;
    private LinkedHashMap<Place, SMTVar> mbsTerms;
    private LinkedHashMap<Place, SMTVar> vpsTerms;
    private LinkedHashMap<Transition, SMTVar> vtsTerms;
    private ArrayList<SMTVar> vtsOptimizedTerms;

    public Z3Implementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public void init() {
        term_VMax = new SMTVar("VMax", ESMTType.INT);
        masTerms = new LinkedHashMap<>();
        mbsTerms = new LinkedHashMap<>();
        vpsTerms = new LinkedHashMap<>();
        vtsTerms = new LinkedHashMap<>();
        vtsOptimizedTerms = new ArrayList<>();
        for (Place p : workflow.getPlaces()) {
            masTerms.put(p, new SMTVar(Prefixes.MA + p, ESMTType.INT));
            mbsTerms.put(p, new SMTVar(Prefixes.MB + p, ESMTType.INT));
            vpsTerms.put(p, new SMTVar(Prefixes.VP + p, ESMTType.INT));
        }
        LinkedHashSet<Transition> usedTransitions = specification.getFormula().getUsedTransitions(workflow);
        for (Transition t : workflow.getTransitions()) {
            vtsTerms.put(t, new SMTVar(Prefixes.VT + t, ESMTType.INT));
            if (usedTransitions.contains(t)) {
                vtsOptimizedTerms.add(new SMTVar(Prefixes.VT + t, ESMTType.INT));
            }
        }
    }

    @Override
    public Object getHeader() {
        return null;
    }

    @Override
    public PlPredicateDefinition getInitialMarking() {
        return null;
    }

    @Override
    public Object getFinalMarking() {
        return null;
    }

    @Override
    public SMTPredicateDefinition getStateEquation() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        /**********************************/
        //body.add(new PlFDDomain(list_MAs, new PlTerm(0), new PlTerm("sup")));
        //body.add(new PlFDDomain(list_MBs, new PlTerm(0), new PlTerm("sup")));
        //body.add(new PlFDDomain(list_VPs, new PlTerm(0), term_VMax));
        //body.add(new PlFDDomain(list_VTs, new PlTerm(0), term_VMax));
        /**********************************/
        for (Place p : workflow.getPlaces()) {
            /**********************************/
            ArrayList<SMTTerm> pres = new ArrayList<>();
            pres.add(masTerms.get(p));
            for (Transition t : p.getPreset()) {
                pres.add(vtsTerms.get(t));
            }
            /**********************************/
            ArrayList<SMTTerm> posts = new ArrayList<>();
            posts.add(mbsTerms.get(p));
            for (Transition t : p.getPostset()) {
                posts.add(vtsTerms.get(t));
            }
            /**********************************/
            body.add(new SMTEquality(
                    new SMTSum(pres),
                    vpsTerms.get(p)
            ));
            body.add(new SMTEquality(
                    new SMTSum(posts),
                    vpsTerms.get(p)
            ));
            /**********************************/
        }
        return new SMTPredicateDefinition(
                "stateEquation",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public SMTPredicateDefinition getFormula() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.addAll(vtsOptimizedTerms);
        Z3FormulaVisitor z3FormulaVisitor = new Z3FormulaVisitor();
        switch (specification.getType()) {
            case MAY:
                specification.getFormula().accept(z3FormulaVisitor);
                break;
            case MUST:
                new NegationFormula(specification.getFormula()).accept(z3FormulaVisitor);
                break;
        }
        body.add(z3FormulaVisitor.getConstraint());
        return new SMTPredicateDefinition(
                "formula",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public Object getNoSiphon() {
        return null;
    }

    @Override
    public Object[] getMarkedGraph() {
        return null;
    }

    @Override
    public SMTPredicateDefinition getOverApproximation1() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        body.add(getStateEquation().getCallWith(new ArrayList<SMTTerm>(parameters)));
        body.add(getFormula().getCallWith(new ArrayList<SMTTerm>(vtsOptimizedTerms)));
        return new SMTPredicateDefinition(
                "overApproximation1",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public String getOverApproximation1Assertion() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        return new SMTAssert(
                new SMTExists(
                        parameters,
                        getOverApproximation1().getCallWith(new ArrayList<SMTTerm>(parameters))
                )
        ).toString() + new SMTCheckSat() + new SMTGetModel();
    }

    @Override
    public String getOverApproximation2() {
        return null;
    }

    @Override
    public String getOverApproximation2Assertion() {
        return null;
    }

    @Override
    public String getOverApproximation3() {
        return null;
    }

    @Override
    public String getOverApproximation3Assertion() {
        return null;
    }

    @Override
    public String getUnderApproximation() {
        return null;
    }

    @Override
    public String getUnderApproximationAssertion() {
        return null;
    }

}
