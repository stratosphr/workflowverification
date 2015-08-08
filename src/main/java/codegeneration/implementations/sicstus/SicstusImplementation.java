package codegeneration.implementations.sicstus;

import codegeneration.implementations.Implementation;
import codegeneration.sicstus.*;
import codegeneration.sicstus.fd.PlFDEquality;
import petrinets.model.Place;
import petrinets.model.Transition;
import petrinets.model.Workflow;
import specifications.model.Specification;
import specifications.visitors.SicstusFormulaVisitor;
import tools.Prefixes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class SicstusImplementation extends Implementation {

    private PlTerm term_VMax;
    private PlList list_MAs;
    private PlList list_MBs;
    private PlList list_VPs;
    private PlList list_VTs;
    private PlList list_VTsOptimized;
    private LinkedHashMap<Place, PlTerm> masTerms;
    private LinkedHashMap<Place, PlTerm> mbsTerms;
    private LinkedHashMap<Place, PlTerm> vpsTerms;
    private LinkedHashMap<Transition, PlTerm> vtsTerms;
    private ArrayList<PlTerm> vtsOptimizedTerms;

    public SicstusImplementation(Workflow workflow, Specification specification) {
        super(workflow, specification);
    }

    @Override
    public void init() {
        term_VMax = new PlTerm("VMax");
        masTerms = new LinkedHashMap<>();
        mbsTerms = new LinkedHashMap<>();
        vpsTerms = new LinkedHashMap<>();
        vtsTerms = new LinkedHashMap<>();
        vtsOptimizedTerms = new ArrayList<>();
        for (Place p : workflow.getPlaces()) {
            masTerms.put(p, new PlTerm(Prefixes.MA + p));
            mbsTerms.put(p, new PlTerm(Prefixes.MB + p));
            vpsTerms.put(p, new PlTerm(Prefixes.VP + p));
        }
        list_MAs = new PlList(new ArrayList<>(masTerms.values()));
        list_MBs = new PlList(new ArrayList<>(mbsTerms.values()));
        list_VPs = new PlList(new ArrayList<>(vpsTerms.values()));
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
    }

    @Override
    public String getStateEquation() {
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        ArrayList<PlTerm> parameters = new ArrayList<>();
        parameters.add(term_VMax);
        parameters.add(list_MAs);
        parameters.add(list_MBs);
        parameters.add(list_VPs);
        parameters.add(list_VTs);
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
            body.add(new PlFDEquality(
                    presList,
                    vpsTerms.get(p)
            ));
            body.add(new PlFDEquality(
                    postsList,
                    vpsTerms.get(p)
            ));
            /**********************************/
        }
        return new PlPredicateDefinition(
                "stateEquation",
                parameters,
                body
        ).toString();
    }

    @Override
    public String getFormulaConstraint() {
        ArrayList<PlTerm> parameters = new ArrayList<>();
        ArrayList<PlBooleanExpr> body = new ArrayList<>();
        parameters.add(list_VTsOptimized);
        SicstusFormulaVisitor sicstusFormulaVisitor = new SicstusFormulaVisitor();
        specification.getFormula().accept(sicstusFormulaVisitor);
        body.add(sicstusFormulaVisitor.getConstraint());
        return new PlPredicateDefinition(
                "formulaConstraint",
                parameters,
                body
        ).toString();
    }

}
