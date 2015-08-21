package codegeneration.implementations.z3;

import codegeneration.implementations.AbstractImplementation;
import codegeneration.z3.*;
import mvc2.models.ParametersModel;
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

public class Z3Implementation extends AbstractImplementation {

    private SMTVar term_VMax;
    private LinkedHashMap<Place, SMTVar> masTerms;
    private LinkedHashMap<Place, SMTVar> mbsTerms;
    private LinkedHashMap<Place, SMTVar> vpsTerms;
    private LinkedHashMap<Transition, SMTVar> vtsTerms;
    private ArrayList<SMTVar> vtsOptimizedTerms;
    private LinkedHashMap<Place, SMTVar> xisTerms;
    private ArrayList<ArrayList<SMTVar>> mksTerms;
    private ArrayList<ArrayList<SMTVar>> vpksTerms;
    private ArrayList<ArrayList<SMTVar>> vtksTerms;
    private ArrayList<ArrayList<SMTVar>> vtksOptimizedTerms;

    public Z3Implementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        super(workflow, specification, parametersModel);
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
        mksTerms = new ArrayList<>();
        vpksTerms = new ArrayList<>();
        vtksTerms = new ArrayList<>();
        vtksOptimizedTerms = new ArrayList<>();
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
        int nbSegments = getParameters().getMaxNumberOfSegments();
        for (int segment = 1; segment <= nbSegments; segment++) {
            ArrayList<SMTVar> mksTermsSegment = new ArrayList<>();
            ArrayList<SMTVar> vpksTermsSegment = new ArrayList<>();
            ArrayList<SMTVar> vtksTermsSegment = new ArrayList<>();
            for (Place p : workflow.getPlaces()) {
                mksTermsSegment.add(new SMTVar(Prefixes.MK + (segment - 1) + "_" + p, ESMTType.INT));
                vpksTermsSegment.add(new SMTVar(Prefixes.VPK + (segment) + "_" + p, ESMTType.INT));
            }
            for (Transition t : workflow.getTransitions()) {
                vtksTermsSegment.add(new SMTVar(Prefixes.VTK + (segment) + "_" + t, ESMTType.INT));
            }
            mksTerms.add(mksTermsSegment);
            vpksTerms.add(vpksTermsSegment);
            vtksTerms.add(vtksTermsSegment);
        }
        ArrayList<SMTVar> mksTermsSegment = new ArrayList<>();
        for (Place p : workflow.getPlaces()) {
            mksTermsSegment.add(new SMTVar(Prefixes.MK + nbSegments + "_" + p, ESMTType.INT));
        }
        mksTerms.add(mksTermsSegment);
        for (Transition t : workflow.getTransitions()) {
            if (usedTransitions.contains(t)) {
                ArrayList<SMTVar> vtksOptimizedTermsSegment = new ArrayList<>();
                for (int segment = 1; segment <= nbSegments; segment++) {
                    vtksOptimizedTermsSegment.add(new SMTVar(Prefixes.VTK + (segment) + "_" + t, ESMTType.INT));
                }
                vtksOptimizedTerms.add(vtksOptimizedTermsSegment);
            }
        }
    }

    @Override
    public String getHeader() {
        return "";
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
                    new SMTGreaterThan(
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
    public SMTPredicateDefinition getMarkedGraph() {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        parameters.addAll(masTerms.values());
        parameters.addAll(vpsTerms.values());
        for (Place p : workflow.getPlaces()) {
            body.add(new SMTLowerOrEqual(
                    new SMTSub(vpsTerms.get(p), masTerms.get(p)),
                    new SMTTerm(1)
            ));
        }
        return new SMTPredicateDefinition(
                "markedGraph",
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public Object getPairwiseSum() {
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
        ArrayList<SMTTerm> parameters = new ArrayList<>();
        ArrayList<SMTVar> existsParameters = new ArrayList<>();
        parameters.add(new SMTTerm(getParameters().getMaxNodeValuation()));
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        existsParameters.addAll(masTerms.values());
        existsParameters.addAll(mbsTerms.values());
        existsParameters.addAll(vpsTerms.values());
        existsParameters.addAll(vtsTerms.values());
        return new SMTAssert(
                new SMTExists(
                        existsParameters,
                        getOverApproximation1().getCallWith(parameters)
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
        ArrayList<SMTTerm> parameters = new ArrayList<>();
        ArrayList<SMTVar> existsParameters = new ArrayList<>();
        parameters.add(new SMTTerm(getParameters().getMaxNodeValuation()));
        parameters.addAll(masTerms.values());
        parameters.addAll(mbsTerms.values());
        parameters.addAll(vpsTerms.values());
        parameters.addAll(vtsTerms.values());
        existsParameters.addAll(masTerms.values());
        existsParameters.addAll(mbsTerms.values());
        existsParameters.addAll(vpsTerms.values());
        existsParameters.addAll(vtsTerms.values());
        return new SMTAssert(
                new SMTExists(
                        existsParameters,
                        getOverApproximation2().getCallWith(parameters)
                )
        ).toString() + new SMTCheckSat() + new SMTGetModel();
    }

    @Override
    public SMTPredicateDefinition getOverApproximation3(int nbSegments) {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        ArrayList<SMTTerm> formulaParameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(mksTerms.get(0));
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(mksTerms.get(segment));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vpksTerms.get(segment - 1));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vtksTerms.get(segment - 1));
        }
        body.add(getInitialMarking().getCallWith(new ArrayList<SMTTerm>(mksTerms.get(0))));
        body.add(getFinalMarking().getCallWith(new ArrayList<SMTTerm>(mksTerms.get(nbSegments))));
        for (int segment = 1; segment <= nbSegments; segment++) {
            ArrayList<SMTTerm> stateEquationParameters = new ArrayList<>();
            ArrayList<SMTTerm> markedGraphParameters = new ArrayList<>();
            stateEquationParameters.add(term_VMax);
            stateEquationParameters.addAll(mksTerms.get(segment - 1));
            stateEquationParameters.addAll(mksTerms.get(segment));
            stateEquationParameters.addAll(vpksTerms.get(segment - 1));
            stateEquationParameters.addAll(vtksTerms.get(segment - 1));
            markedGraphParameters.addAll(mksTerms.get(segment - 1));
            markedGraphParameters.addAll(vpksTerms.get(segment - 1));
            body.add(getStateEquation().getCallWith(stateEquationParameters));
            body.add(getMarkedGraph().getCallWith(markedGraphParameters));
        }
        for (ArrayList<SMTVar> vtkOptimizedTerms : vtksOptimizedTerms) {
            formulaParameters.add(new SMTSum(
                    new ArrayList<SMTTerm>(vtkOptimizedTerms.subList(0, nbSegments))
            ));
        }
        body.add(getFormula().getCallWith(formulaParameters));
        return new SMTPredicateDefinition(
                "overApproximation3_" + nbSegments,
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public String getOverApproximation3Assertion(int nbSegments) {
        ArrayList<SMTTerm> parameters = new ArrayList<>();
        ArrayList<SMTVar> existsParameters = new ArrayList<>();
        parameters.add(new SMTTerm(getParameters().getMaxNodeValuation()));
        parameters.addAll(mksTerms.get(0));
        existsParameters.addAll(mksTerms.get(0));
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(mksTerms.get(segment));
            existsParameters.addAll(mksTerms.get(segment));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vpksTerms.get(segment - 1));
            existsParameters.addAll(vpksTerms.get(segment - 1));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vtksTerms.get(segment - 1));
            existsParameters.addAll(vtksTerms.get(segment - 1));
        }
        return new SMTAssert(
                new SMTExists(
                        existsParameters,
                        getOverApproximation3(nbSegments).getCallWith(parameters)
                )
        ).toString() + new SMTCheckSat() + new SMTGetModel();
    }

    @Override
    public SMTPredicateDefinition getUnderApproximation(int nbSegments) {
        ArrayList<SMTVar> parameters = new ArrayList<>();
        ArrayList<SMTTerm> body = new ArrayList<>();
        ArrayList<SMTTerm> formulaParameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.addAll(mksTerms.get(0));
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(mksTerms.get(segment));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vpksTerms.get(segment - 1));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vtksTerms.get(segment - 1));
        }
        body.add(getInitialMarking().getCallWith(new ArrayList<SMTTerm>(mksTerms.get(0))));
        body.add(getFinalMarking().getCallWith(new ArrayList<SMTTerm>(mksTerms.get(nbSegments))));
        for (int segment = 1; segment <= nbSegments; segment++) {
            ArrayList<SMTTerm> stateEquationParameters = new ArrayList<>();
            ArrayList<SMTTerm> markedGraphParameters = new ArrayList<>();
            ArrayList<SMTTerm> noSiphonParameters = new ArrayList<>();
            stateEquationParameters.add(term_VMax);
            stateEquationParameters.addAll(mksTerms.get(segment - 1));
            stateEquationParameters.addAll(mksTerms.get(segment));
            stateEquationParameters.addAll(vpksTerms.get(segment - 1));
            stateEquationParameters.addAll(vtksTerms.get(segment - 1));
            markedGraphParameters.addAll(mksTerms.get(segment - 1));
            markedGraphParameters.addAll(vpksTerms.get(segment - 1));
            noSiphonParameters.addAll(mksTerms.get(segment - 1));
            noSiphonParameters.addAll(mksTerms.get(segment));
            noSiphonParameters.addAll(vpksTerms.get(segment - 1));
            noSiphonParameters.addAll(vtksTerms.get(segment - 1));
            body.add(getStateEquation().getCallWith(stateEquationParameters));
            body.add(getMarkedGraph().getCallWith(markedGraphParameters));
            body.add(getNoSiphon().getCallWith(noSiphonParameters));
        }
        for (ArrayList<SMTVar> vtkOptimizedTerms : vtksOptimizedTerms) {
            formulaParameters.add(new SMTSum(
                    new ArrayList<SMTTerm>(vtkOptimizedTerms.subList(0, nbSegments))
            ));
        }
        body.add(getFormula().getCallWith(formulaParameters));
        return new SMTPredicateDefinition(
                "underApproximation_" + nbSegments,
                parameters,
                ESMTType.BOOL,
                body
        );
    }

    @Override
    public String getUnderApproximationAssertion(int nbSegments) {
        ArrayList<SMTTerm> parameters = new ArrayList<>();
        ArrayList<SMTVar> existsParameters = new ArrayList<>();
        parameters.add(new SMTTerm(getParameters().getMaxNodeValuation()));
        parameters.addAll(mksTerms.get(0));
        existsParameters.addAll(mksTerms.get(0));
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(mksTerms.get(segment));
            existsParameters.addAll(mksTerms.get(segment));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vpksTerms.get(segment - 1));
            existsParameters.addAll(vpksTerms.get(segment - 1));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            parameters.addAll(vtksTerms.get(segment - 1));
            existsParameters.addAll(vtksTerms.get(segment - 1));
        }
        return new SMTAssert(
                new SMTExists(
                        existsParameters,
                        getUnderApproximation(nbSegments).getCallWith(parameters)
                )
        ).toString() + new SMTCheckSat() + new SMTGetModel();
    }

}
