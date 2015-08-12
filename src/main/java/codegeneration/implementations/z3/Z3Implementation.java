package codegeneration.implementations.z3;

import codegeneration.implementations.Implementation;
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
    private LinkedHashMap<Place, SMTVar> xisTerms;

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
        xisTerms = new LinkedHashMap<>();
        for (Place p : workflow.getPlaces()) {
            masTerms.put(p, new SMTVar(Prefixes.MA + p, ESMTType.INT));
            mbsTerms.put(p, new SMTVar(Prefixes.MB + p, ESMTType.INT));
            vpsTerms.put(p, new SMTVar(Prefixes.VP + p, ESMTType.INT));
            xisTerms.put(p, new SMTVar(Prefixes.XI + p, ESMTType.INT));
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
    public SMTPredicateDefinition getInitialMarking() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.addAll(masTerms.values());
        body.add(new SMTEquality(
                masTerms.get(workflow.getSource()),
                new SMTTerm(1)
        ));
        for (Place p : workflow.getPlaces()) {
            if (p != workflow.getSource()) {
                body.add(new SMTEquality(
                        masTerms.get(p),
                        new SMTTerm(0)
                ));
            }
        }
        return new SMTPredicateDefinition(
                "initialMarking",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public SMTPredicateDefinition getFinalMarking() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.addAll(mbsTerms.values());
        body.add(new SMTEquality(
                mbsTerms.get(workflow.getSink()),
                new SMTTerm(1)
        ));
        for (Place p : workflow.getPlaces()) {
            if (p != workflow.getSink()) {
                body.add(new SMTEquality(
                        mbsTerms.get(p),
                        new SMTTerm(0)
                ));
            }
        }
        return new SMTPredicateDefinition(
                "finalMarking",
                parameters,
                ESMTType.BOOL,
                body
        );
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
        for (Place p : workflow.getPlaces()) {
            body.add(new SMTGreaterOrEqual(
                    masTerms.get(p),
                    new SMTTerm(0)
            ));
            body.add(new SMTGreaterOrEqual(
                    mbsTerms.get(p),
                    new SMTTerm(0)
            ));
            body.add(new SMTDomain(
                    vpsTerms.get(p),
                    new SMTTerm(0),
                    term_VMax
            ));
        }
        for (Transition t : workflow.getTransitions()) {
            body.add(new SMTDomain(
                    vtsTerms.get(t),
                    new SMTTerm(0),
                    term_VMax
            ));
        }
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
    public SMTPredicateDefinition getNoSiphon() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        SMTConjunction existsBody = new SMTConjunction();
        /**********************************/
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        /**********************************/
        existsBody.addParameter(new SMTGreaterThan(
                new SMTSum(new ArrayList<SMTTerm>(xisTerms.values())),
                new SMTTerm(0)
        ));
        /**********************************/
        for (SMTVar xi : xisTerms.values()) {
            existsBody.addParameter(new SMTDomain(
                    xi,
                    new SMTTerm(0),
                    new SMTTerm(1)
            ));
        }
        /**********************************/
        for (Place p : workflow.getPlaces()) {
            existsBody.addParameter(new SMTImplication(
                    new SMTDisjunction(
                            new SMTGreaterThan(
                                    masTerms.get(p),
                                    new SMTTerm(0)
                            ),
                            new SMTGreaterThan(
                                    mbsTerms.get(p),
                                    new SMTTerm(0)
                            ),
                            new SMTEquality(
                                    vpsTerms.get(p),
                                    new SMTTerm(0)
                            )
                    ),
                    new SMTEquality(
                            xisTerms.get(p),
                            new SMTTerm(0)
                    )
            ));
        }
        /**********************************/
        for (Transition t : workflow.getTransitions()) {
            ArrayList<SMTTerm> xisPreT = new ArrayList<>();
            for (Place p : t.getPreset()) {
                xisPreT.add(xisTerms.get(p));
            }
            SMTConjunction implicationRight = new SMTConjunction();
            for (Place p : t.getPostset()) {
                implicationRight.addParameter(new SMTGreaterOrEqual(
                        new SMTSum(xisPreT),
                        xisTerms.get(p)
                ));
            }
            existsBody.addParameter(new SMTImplication(
                    new SMTGreaterOrEqual(
                            vtsTerms.get(t),
                            new SMTTerm(0)
                    ),
                    implicationRight
            ));
        }
        /**********************************/
        body.add(new SMTNegation(
                new SMTExists(
                        new ArrayList<>(xisTerms.values()),
                        existsBody
                )
        ));
        return new SMTPredicateDefinition(
                "noSiphon",
                parameters,
                ESMTType.BOOL,
                body
        );
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
        body.add(getInitialMarking().getCallWith(new ArrayList<SMTTerm>(masTerms.values())));
        body.add(getFinalMarking().getCallWith(new ArrayList<SMTTerm>(mbsTerms.values())));
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
    public SMTPredicateDefinition getOverApproximation2() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        ArrayList<SMTVar> noSiphonParameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        noSiphonParameters.addAll(masTerms.values());
        noSiphonParameters.addAll(mbsTerms.values());
        noSiphonParameters.addAll(vpsTerms.values());
        noSiphonParameters.addAll(vtsTerms.values());
        body.add(getInitialMarking().getCallWith(new ArrayList<SMTTerm>(masTerms.values())));
        body.add(getFinalMarking().getCallWith(new ArrayList<SMTTerm>(mbsTerms.values())));
        body.add(getStateEquation().getCallWith(new ArrayList<SMTTerm>(parameters)));
        body.add(getFormula().getCallWith(new ArrayList<SMTTerm>(vtsOptimizedTerms)));
        body.add(getNoSiphon().getCallWith(new ArrayList<SMTTerm>(noSiphonParameters)));
        return new SMTPredicateDefinition(
                "overApproximation2",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public String getOverApproximation2Assertion() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        return new SMTAssert(
                new SMTExists(
                        parameters,
                        getOverApproximation2().getCallWith(new ArrayList<SMTTerm>(parameters))
                )
        ).toString() + new SMTCheckSat() + new SMTGetModel();
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
