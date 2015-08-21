package codegeneration.implementations.sicstus;

import codegeneration.implementations.AbstractImplementation;
import codegeneration.sicstus.*;
import codegeneration.sicstus.fd.*;
import mvc2.models.ParametersModel;
import petrinets.model.Place;
import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.model.Specification;
import specifications.model.formulas.NegationFormula;
import specifications.visitors.SicstusFormulaVisitor;
import tools.Prefixes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class SicstusImplementation extends AbstractImplementation {

    private PlTerm term_VMax;
    private PlTerm term_MAs;
    private PlTerm term_MBs;
    private PlTerm term_VPs;
    private PlTerm term_VTs;
    private PlTerm term_XIs;
    private PlList list_MAs;
    private PlList list_MBs;
    private PlList list_VPs;
    private PlList list_VTs;
    private PlList list_VTsOptimized;
    private PlList list_XIs;
    private LinkedHashMap<Place, PlTerm> masTerms;
    private LinkedHashMap<Place, PlTerm> mbsTerms;
    private LinkedHashMap<Place, PlTerm> vpsTerms;
    private LinkedHashMap<Transition, PlTerm> vtsTerms;
    private ArrayList<PlTerm> vtsOptimizedTerms;
    private LinkedHashMap<Place, PlTerm> xisTerms;
    private ArrayList<PlList> mksLists;
    private ArrayList<PlList> vpksLists;
    private ArrayList<PlList> vtksLists;
    private PlList list_MKs;
    private PlList list_VPKs;
    private PlList list_VTKs;
    private PlPredicateDefinition pairwiseSum;

    public SicstusImplementation(Workflow workflow, Specification specification, ParametersModel parametersModel) {
        super(workflow, specification, parametersModel);
    }

    @Override
    public void init() {
        term_VMax = new PlTerm("VMax");
        term_MAs = new PlTerm("MAs");
        term_MBs = new PlTerm("MBs");
        term_VPs = new PlTerm("VPs");
        term_VTs = new PlTerm("VTs");
        term_XIs = new PlTerm("XIs");
        masTerms = new LinkedHashMap<>();
        mbsTerms = new LinkedHashMap<>();
        vpsTerms = new LinkedHashMap<>();
        vtsTerms = new LinkedHashMap<>();
        vtsOptimizedTerms = new ArrayList<>();
        xisTerms = new LinkedHashMap<>();
        mksLists = new ArrayList<>();
        vpksLists = new ArrayList<>();
        vtksLists = new ArrayList<>();
        for (Place p : workflow.getPlaces()) {
            masTerms.put(p, new PlTerm(Prefixes.MA + p));
            mbsTerms.put(p, new PlTerm(Prefixes.MB + p));
            vpsTerms.put(p, new PlTerm(Prefixes.VP + p));
            xisTerms.put(p, new PlTerm(Prefixes.XI + p));
        }
        list_MAs = new PlList(new ArrayList<>(masTerms.values()));
        list_MBs = new PlList(new ArrayList<>(mbsTerms.values()));
        list_VPs = new PlList(new ArrayList<>(vpsTerms.values()));
        list_XIs = new PlList(new ArrayList<>(xisTerms.values()));
        LinkedHashSet<Transition> usedTransitions = specification.getFormula().getUsedTransitions(workflow);
        for (Transition t : workflow.getTransitions()) {
            vtsTerms.put(t, new PlTerm(Prefixes.VT + t));
            if (usedTransitions.contains(t)) {
                vtsOptimizedTerms.add(new PlTerm(Prefixes.VT + t));
            } else {
                vtsOptimizedTerms.add(new PlWildcard());
            }
        }
        list_VTsOptimized = new PlList(vtsOptimizedTerms);
        list_VTs = new PlList(new ArrayList<>(vtsTerms.values()));
        //TODO: nbSegments should be replaced by the number of segments specified by the user
        int nbSegments = getParameters().getMaxNumberOfSegments();
        ArrayList<PlTerm> mksTerms = new ArrayList<>();
        ArrayList<PlTerm> vpksTerms = new ArrayList<>();
        ArrayList<PlTerm> vtksTerms = new ArrayList<>();
        for (int segment = 1; segment <= nbSegments; segment++) {
            mksTerms.add(new PlTerm(Prefixes.MK + (segment - 1)));
            vpksTerms.add(new PlTerm(Prefixes.VPK + segment));
            vtksTerms.add(new PlTerm(Prefixes.VTK + segment));
            ArrayList<PlTerm> mksTermsSegment = new ArrayList<>();
            ArrayList<PlTerm> vpksTermsSegment = new ArrayList<>();
            ArrayList<PlTerm> vtksTermsSegment = new ArrayList<>();
            for (Place p : workflow.getPlaces()) {
                mksTermsSegment.add(new PlTerm(Prefixes.MK + (segment - 1) + "_" + p));
                vpksTermsSegment.add(new PlTerm(Prefixes.VPK + segment + "_" + p));
            }
            for (Transition t : workflow.getTransitions()) {
                vtksTermsSegment.add(new PlTerm(Prefixes.VTK + segment + "_" + t));
            }
            mksLists.add(new PlList(mksTermsSegment));
            vpksLists.add(new PlList(vpksTermsSegment));
            vtksLists.add(new PlList(vtksTermsSegment));
        }
        mksTerms.add(new PlTerm(Prefixes.MK + nbSegments));
        ArrayList<PlTerm> mksTermsSegment = new ArrayList<>();
        for (Place p : workflow.getPlaces()) {
            mksTermsSegment.add(new PlTerm(Prefixes.MK + nbSegments + "_" + p));
        }
        mksLists.add(new PlList(mksTermsSegment));
        list_MKs = new PlList(mksTerms);
        list_VPKs = new PlList(vpksTerms);
        list_VTKs = new PlList(vtksTerms);
    }

    @Override
    public PlPredicateDefinition getHeader() {
        return new PlUseModule(new PlLibrary("clpfd"));
    }

    @Override
    public PlPredicateDefinition getInitialMarking() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlTerm> initialMarking = new ArrayList<>();
        initialMarking.add(new PlTerm(1));
        for (int i = 1; i < workflow.getPlaces().size(); i++) {
            initialMarking.add(new PlTerm(0));
        }
        parameters.add(new PlList(initialMarking));
        return new PlPredicateDefinition(
                "initialMarking",
                parameters
        );
    }

    public PlPredicateDefinition getFinalMarking() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlTerm> finalMarking = new ArrayList<>();
        finalMarking.add(new PlTerm(0));
        finalMarking.add(new PlTerm(1));
        for (int i = 2; i < workflow.getPlaces().size(); i++) {
            finalMarking.add(new PlTerm(0));
        }
        parameters.add(new PlList(finalMarking));
        return new PlPredicateDefinition(
                "finalMarking",
                parameters
        );
    }

    @Override
    public PlPredicateDefinition getStateEquation() {
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        ArrayList<PlTerm> parameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(list_MAs);
        parameters.add(list_MBs);
        parameters.add(list_VPs);
        parameters.add(list_VTs);
        /**********************************/
        body.add(new PlFDDomain(list_MAs, new PlTerm(0), new PlTerm("sup")));
        body.add(new PlFDDomain(list_MBs, new PlTerm(0), new PlTerm("sup")));
        body.add(new PlFDDomain(list_VPs, new PlTerm(0), term_VMax));
        body.add(new PlFDDomain(list_VTs, new PlTerm(0), term_VMax));
        /**********************************/
        for (Place p : workflow.getPlaces()) {
            /**********************************/
            ArrayList<PlTerm> pres = new ArrayList<>();
            pres.add(masTerms.get(p));
            for (Transition t : p.getPreset()) {
                pres.add(vtsTerms.get(t));
            }
            PlList presList = new PlList(pres);
            /**********************************/
            ArrayList<PlTerm> posts = new ArrayList<>();
            posts.add(mbsTerms.get(p));
            for (Transition t : p.getPostset()) {
                posts.add(vtsTerms.get(t));
            }
            PlList postsList = new PlList(posts);
            /**********************************/
            body.add(new PlFDSum(
                    "#=",
                    presList,
                    vpsTerms.get(p)
            ));
            body.add(new PlFDSum(
                    "#=",
                    postsList,
                    vpsTerms.get(p)
            ));
            /**********************************/
        }
        return new PlPredicateDefinition(
                "stateEquation",
                parameters,
                body
        );
    }

    @Override
    public PlPredicateDefinition getFormula() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        parameters.add(list_VTsOptimized);
        SicstusFormulaVisitor sicstusFormulaVisitor = new SicstusFormulaVisitor();
        switch (specification.getType()) {
            case MAY:
                specification.getFormula().accept(sicstusFormulaVisitor);
                break;
            case MUST:
                new NegationFormula(specification.getFormula()).accept(sicstusFormulaVisitor);
                break;
        }
        body.add(sicstusFormulaVisitor.getConstraint());
        return new PlPredicateDefinition(
                "formula",
                parameters,
                body
        );
    }

    @Override
    public PlPredicateDefinition[] getNoSiphon() {
        ArrayList<PlTerm> noSiphonParameters = new ArrayList<>();
        ArrayList<PlTerm> siphonParameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> noSiphonBody = new ArrayList<>();
        ArrayList<PlTerm> siphonCallParameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> siphonBody = new ArrayList<>();
        /**********************************/
        siphonParameters.add(list_MAs);
        siphonParameters.add(list_MBs);
        siphonParameters.add(list_VPs);
        siphonParameters.add(list_VTs);
        siphonParameters.add(list_XIs);
        siphonBody.add(new PlFDDomain(
                list_XIs,
                new PlTerm(0),
                new PlTerm(1)
        ));
        siphonBody.add(new PlFDSum(
                "#>",
                list_XIs,
                new PlTerm(0)
        ));
        for (Place p : workflow.getPlaces()) {
            siphonBody.add(new PlFDImplies(
                    new PlFDDisjunction(
                            new PlFDGreaterThan(
                                    masTerms.get(p),
                                    new PlTerm(0)
                            ),
                            new PlFDGreaterThan(
                                    mbsTerms.get(p),
                                    new PlTerm(0)
                            ),
                            new PLFDEquals(
                                    vpsTerms.get(p),
                                    new PlTerm(0)
                            )
                    ),
                    new PLFDEquals(
                            xisTerms.get(p),
                            new PlTerm(0)
                    )
            ));
        }
        for (Transition t : workflow.getTransitions()) {
            ArrayList<PlTerm> xisPreT = new ArrayList<>();
            for (Place p : t.getPreset()) {
                xisPreT.add(xisTerms.get(p));
            }
            PlFDConjunction implicationRight = new PlFDConjunction();
            for (Place p : t.getPostset()) {
                implicationRight.addParameter(new PlFDGreaterOrEqual(
                        new PlSum(xisPreT),
                        xisTerms.get(p)
                ));
            }
            siphonBody.add(new PlFDImplies(
                    new PlFDGreaterThan(
                            vtsTerms.get(t),
                            new PlTerm(0)
                    ),
                    implicationRight
            ));
        }
        siphonBody.add(new PlFDLabeling(
                list_XIs
        ));
        PlPredicateDefinition siphon = new PlPredicateDefinition(
                "siphon",
                siphonParameters,
                siphonBody
        );
        /**********************************/
        noSiphonParameters.add(term_MAs);
        noSiphonParameters.add(term_MBs);
        noSiphonParameters.add(term_VPs);
        noSiphonParameters.add(term_VTs);
        siphonCallParameters.addAll(noSiphonParameters);
        siphonCallParameters.add(term_XIs);
        noSiphonBody.add(new PlFDLabeling(
                term_VTs
        ));
        noSiphonBody.add(new PlNegation(
                siphon.getCallWith(siphonCallParameters)
        ));
        PlPredicateDefinition noSiphon = new PlPredicateDefinition(
                "noSiphon",
                noSiphonParameters,
                noSiphonBody
        );
        /**********************************/
        return new PlPredicateDefinition[]{
                noSiphon,
                siphon
        };
    }

    @Override
    public PlPredicateDefinition[] getMarkedGraph() {
        ArrayList<PlTerm> markedGraph1Parameters = new ArrayList<>();
        ArrayList<PlTerm> markedGraph2Parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> markedGraph2Body = new ArrayList<>();
        /**********************************/
        markedGraph1Parameters.add(new PlList());
        markedGraph1Parameters.add(new PlList());
        PlPredicateDefinition markedGraph1 = new PlPredicateDefinition(
                "markedGraph",
                markedGraph1Parameters
        );
        /**********************************/
        markedGraph2Parameters.add(new PlHeadTailList(
                new PlTerm("MA"),
                term_MAs
        ));
        markedGraph2Parameters.add(new PlHeadTailList(
                new PlTerm("VP"),
                term_VPs
        ));
        markedGraph2Body.add(new PlFDLowerOrEqual(
                new PlSub(
                        new PlTerm("VP"),
                        new PlTerm("MA")
                ),
                new PlTerm(1)
        ));
        markedGraph2Body.add(new PlPredicateCall(
                "markedGraph",
                term_MAs,
                term_VPs
        ));
        PlPredicateDefinition markedGraph2 = new PlPredicateDefinition(
                "markedGraph",
                markedGraph2Parameters,
                markedGraph2Body
        );
        /**********************************/
        return new PlPredicateDefinition[]{
                markedGraph1,
                markedGraph2
        };
    }

    @Override
    public PlPredicateDefinition[] getPairwiseSum() {
        PlPredicateDefinition pairwiseSum1 = new PlPredicateDefinition(
                "pairwiseSum",
                new PlList(),
                new PlList(),
                new PlList()
        );
        PlPredicateDefinition pairwiseSum2 = new PlPredicateDefinition(
                "pairwiseSum",
                new PlTerm[]{
                        new PlHeadTailList(
                                new PlTerm("H1"),
                                new PlTerm("T1")
                        ),
                        new PlHeadTailList(
                                new PlTerm("H2"),
                                new PlTerm("T2")
                        ),
                        new PlHeadTailList(
                                new PlTerm("H3"),
                                new PlTerm("T3")
                        )
                },
                new PlBooleanExpr[]{
                        new PLFDEquals(
                                new PlTerm("H3"),
                                new PlSum(
                                        new PlTerm("H1"),
                                        new PlTerm("H2")
                                )
                        ),
                        new PlPredicateCall(
                                "pairwiseSum",
                                new PlTerm("T1"),
                                new PlTerm("T2"),
                                new PlTerm("T3")
                        )
                }
        );
        PlPredicateDefinition pairwiseSum3 = new PlPredicateDefinition(
                "pairwiseSum",
                new PlList(
                        new PlTerm("L")
                ),
                new PlTerm("L")
        );
        PlPredicateDefinition pairwiseSum4 = new PlPredicateDefinition(
                "pairwiseSum",
                new PlTerm[]{
                        new PlHeadTailList(
                                new PlTerm[]{
                                        new PlTerm("L1"),
                                        new PlTerm("L2")
                                },
                                new PlTerm("Ls")
                        ),
                        new PlTerm("R")
                },
                new PlBooleanExpr[]{
                        new PlPredicateCall(
                                "pairwiseSum",
                                new PlTerm("L1"),
                                new PlTerm("L2"),
                                new PlTerm("R1")
                        ),
                        new PlPredicateCall(
                                "pairwiseSum",
                                new PlHeadTailList(
                                        new PlTerm("R1"),
                                        new PlTerm("Ls")
                                ),
                                new PlTerm("R")
                        )
                }
        );
        return new PlPredicateDefinition[]{
                pairwiseSum1,
                pairwiseSum2,
                pairwiseSum3,
                pairwiseSum4,
        };
    }

    @Override
    public PlPredicateDefinition getOverApproximation1() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(term_MAs);
        parameters.add(term_MBs);
        parameters.add(term_VPs);
        parameters.add(term_VTs);
        body.add(getInitialMarking().getCallWith(term_MAs));
        body.add(getFinalMarking().getCallWith(term_MBs));
        body.add(getStateEquation().getCallWith(term_VMax, term_MAs, term_MBs, term_VPs, term_VTs));
        body.add(getFormula().getCallWith(term_VTs));
        body.add(new PlFDLabeling(
                term_VTs
        ));
        body.add(new PlFDLabeling(
                term_VPs
        ));
        return new PlPredicateDefinition(
                "overApproximation1",
                parameters,
                body
        );
    }

    @Override
    public String getOverApproximation1Assertion() {
        return getOverApproximation1().getCallWith(new PlTerm(getParameters().getMaxNodeValuation()), list_MAs, list_MBs, list_VPs, list_VTs).toString();
    }

    @Override
    public PlPredicateDefinition getOverApproximation2() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(term_MAs);
        parameters.add(term_MBs);
        parameters.add(term_VPs);
        parameters.add(term_VTs);
        body.add(getInitialMarking().getCallWith(term_MAs));
        body.add(getFinalMarking().getCallWith(term_MBs));
        body.add(getStateEquation().getCallWith(term_VMax, term_MAs, term_MBs, term_VPs, term_VTs));
        body.add(getFormula().getCallWith(term_VTs));
        body.add(getNoSiphon()[0].getCallWith(term_MAs, term_MBs, term_VPs, term_VTs));
        body.add(new PlFDLabeling(
                term_VTs
        ));
        body.add(new PlFDLabeling(
                term_VPs
        ));
        return new PlPredicateDefinition(
                "overApproximation2",
                parameters,
                body
        );
    }

    @Override
    public String getOverApproximation2Assertion() {
        return getOverApproximation2().getCallWith(new PlTerm(getParameters().getMaxNodeValuation()), list_MAs, list_MBs, list_VPs, list_VTs).toString();
    }

    @Override
    public PlPredicateDefinition getOverApproximation3(int nbSegments) {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        ArrayList<PlTerm> pairwiseSumParameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(new PlList(new ArrayList<>(list_MKs.getTerms().subList(0, nbSegments + 1))));
        parameters.add(new PlList(new ArrayList<>(list_VPKs.getTerms().subList(0, nbSegments))));
        parameters.add(new PlList(new ArrayList<>(list_VTKs.getTerms().subList(0, nbSegments))));
        pairwiseSumParameters.add(new PlList(new ArrayList<>(list_VTKs.getTerms().subList(0, nbSegments))));
        pairwiseSumParameters.add(term_VTs);
        body.add(getInitialMarking().getCallWith(list_MKs.getTerm(0)));
        body.add(getFinalMarking().getCallWith(list_MKs.getTerm(nbSegments)));
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(getStateEquation().getCallWith(term_VMax, list_MKs.getTerm(segment - 1), list_MKs.getTerm(segment), list_VPKs.getTerm(segment - 1), list_VTKs.getTerm(segment - 1)));
            body.add(getMarkedGraph()[0].getCallWith(list_MKs.getTerm(segment - 1), list_VPKs.getTerm(segment - 1)));
        }
        body.add(getPairwiseSum()[0].getCallWith(pairwiseSumParameters));
        body.add(getFormula().getCallWith(term_VTs));
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(new PlFDLabeling(
                    list_VTKs.getTerm(segment - 1)
            ));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(new PlFDLabeling(
                    list_VPKs.getTerm(segment - 1)
            ));
        }
        return new PlPredicateDefinition(
                "overApproximation3_" + nbSegments,
                parameters,
                body
        );
    }

    @Override
    public String getOverApproximation3Assertion(int nbSegments) {
        return getOverApproximation3(nbSegments).getCallWith(new PlTerm(getParameters().getMaxNodeValuation()), new PlList(new ArrayList<PlTerm>(mksLists.subList(0, nbSegments + 1))), new PlList(new ArrayList<PlTerm>(vpksLists.subList(0, nbSegments))), new PlList(new ArrayList<PlTerm>(vtksLists.subList(0, nbSegments)))).toString();
    }

    @Override
    public PlPredicateDefinition getUnderApproximation(int nbSegments) {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        ArrayList<PlTerm> pairwiseSumParameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(new PlList(new ArrayList<>(list_MKs.getTerms().subList(0, nbSegments + 1))));
        parameters.add(new PlList(new ArrayList<>(list_VPKs.getTerms().subList(0, nbSegments))));
        parameters.add(new PlList(new ArrayList<>(list_VTKs.getTerms().subList(0, nbSegments))));
        pairwiseSumParameters.add(new PlList(new ArrayList<>(list_VTKs.getTerms().subList(0, nbSegments))));
        pairwiseSumParameters.add(term_VTs);
        body.add(getInitialMarking().getCallWith(list_MKs.getTerm(0)));
        body.add(getFinalMarking().getCallWith(list_MKs.getTerm(nbSegments)));
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(getStateEquation().getCallWith(term_VMax, list_MKs.getTerm(segment - 1), list_MKs.getTerm(segment), list_VPKs.getTerm(segment - 1), list_VTKs.getTerm(segment - 1)));
            body.add(getMarkedGraph()[0].getCallWith(list_MKs.getTerm(segment - 1), list_VPKs.getTerm(segment - 1)));
            body.add(getNoSiphon()[0].getCallWith(list_MKs.getTerm(segment - 1), list_MKs.getTerm(segment), list_VPKs.getTerm(segment - 1), list_VTKs.getTerm(segment - 1)));
        }
        body.add(getPairwiseSum()[0].getCallWith(pairwiseSumParameters));
        body.add(getFormula().getCallWith(term_VTs));
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(new PlFDLabeling(
                    list_VTKs.getTerm(segment - 1)
            ));
        }
        for (int segment = 1; segment <= nbSegments; segment++) {
            body.add(new PlFDLabeling(
                    list_VPKs.getTerm(segment - 1)
            ));
        }
        return new PlPredicateDefinition(
                "underApproximation_" + nbSegments,
                parameters,
                body
        );
    }

    @Override
    public String getUnderApproximationAssertion(int nbSegments) {
        return getUnderApproximation(nbSegments).getCallWith(new PlTerm(getParameters().getMaxNodeValuation()), new PlList(new ArrayList<PlTerm>(mksLists.subList(0, nbSegments + 1))), new PlList(new ArrayList<PlTerm>(vpksLists.subList(0, nbSegments))), new PlList(new ArrayList<PlTerm>(vtksLists.subList(0, nbSegments)))).toString();
    }

}
