package core;

import exceptions.LogicException;
import graphstream.LogicGraphManager;
import terms.*;

import java.util.*;

public class Evaluator {
    private KnowledgeBase kb;

    private Map<String, Compound> actualValuesMap;//initialized values mapped to actual values

    private ChoicePointTracker choicePointTracker;

    private SolutionSnapshots solutionSnapshots;

    private int maxNoOfSolutions = 1;

    private int initialValueCounter = 0;

    private LogicGraphManager logicGraphManager;

    public Evaluator(KnowledgeBase kb) {
        this.kb = kb;
        logicGraphManager = new LogicGraphManager();
    }

   /* private Map<String, Variable> initializeVariables(Term[] terms)
    {
        Map<String, Variable> variableInitializedMap = new HashMap<String, Variable>();

        initializeVariables(terms, variableInitializedMap);

        return variableInitializedMap;
    }*/

    private void initializeVariables(Term[] terms, Set<Variable> queryVariables) {
        for (Term term : terms) {
            Initializer.initializeVariables(term, queryVariables);
        }
    }

    public SolutionSnapshots getSolutionSnapshots() {
        return solutionSnapshots;
    }

    //overloaded method in case more than one solution is needed
    public boolean query(Query query, int maxNoOfSolutions) {
        this.maxNoOfSolutions = maxNoOfSolutions;
        return query(query);
    }

    public boolean query(Query query) {
        Set<Variable> queryVariables = new HashSet<Variable>();
        actualValuesMap = new HashMap<String, Compound>();
        choicePointTracker = new ChoicePointTracker();

        //initialization of variables of the goalTerms
        initializeVariables(query.getTerms(), queryVariables);
        //registering the variables to capture
        solutionSnapshots = new SolutionSnapshots(queryVariables);

        //graph visualization
        logicGraphManager.display();

        boolean unified = searchAndUnify(query.getTerms(), true);
        kb.undoAllRetract();
        return unified;
    }

    private Term[] concat(Term[] a, Term[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Term[] c = new Term[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    //<3 of the engine
    private boolean searchAndUnify(Term[] goalTerms, boolean rightmostBranch) {
        //graph visualization
        logicGraphManager.addQueryNode(goalTerms);

        boolean unified = false;

        Term goalTerm = goalTerms[0];
        //System.out.println("Rightmostbranch:- " + rightmostBranch);
        if (goalTerm instanceof Compound) {
            int noOfChoices = kb.getTotalCountOfKnowledge(goalTerm.getName(), ((Compound) goalTerm).getArity());
            int count = 0;

            int choicePointNo = 0;

            while (count < noOfChoices && !unified) {
                if (noOfChoices > 1) {
                    choicePointNo = choicePointTracker.newChoicePoint();
                    System.out.println("No of choices for " + goalTerm.getName() + " :- " + noOfChoices);
                    System.out.println("New choice point:- " + choicePointNo);
                }

                System.out.println("In choice no:- " + count + "/" + noOfChoices + " for " + goalTerm.getName() + " for choicePointNo:- " + choicePointNo);
                /*if (s.equals("5/8 for alchemical for choicePointNo:- 16"))
                {
                    System.out.println();
                }*/

                int totalFactCount = kb.getTotalCountOfFact(goalTerm.getName(), ((Compound) goalTerm).getArity());

                boolean leafNode = false;
                if (count < totalFactCount) {
                    Compound fact = kb.getFact(goalTerm.getName(), ((Compound) goalTerm).getArity(), count);
                    //FACT
                    if (fact != null) {
                        fact = (Compound) LogicUtils.copy(fact);
                        unified = unify(goalTerm, fact);
                        leafNode = true;

                        if (unified && goalTerms.length > 1)//more goal terms to process
                        {
                            Term[] restOfGoalTerms = Arrays.copyOfRange(goalTerms, 1, goalTerms.length);
                            unified = searchAndUnify(restOfGoalTerms, restOfGoalTerms.length == 1);
                            //unified = searchAndUnify(restOfGoalTerms, choicePointNo, rightmostBranch);
                        }
                    } else {
                        unified = false;
                    }
                }
                //RULE
                else {
                    Rule rule = kb.getRule(goalTerm.getName(), ((Compound) goalTerm).getArity(), (count + totalFactCount));

                    if (rule != null) {
                        Map<String, Term> ruleTermsMap = new HashMap<String, Term>();
                        Compound ruleHead = (Compound) LogicUtils.copy(rule.getHead(), ruleTermsMap, null);
                        unified = unify(goalTerm, ruleHead);
                        if (unified) {
                            //new shit
                            Term[] testTerms = LogicUtils.copy(rule.getBody(), ruleTermsMap);
                            if (goalTerms.length > 1) {
                                Term[] restOfGoalTerms = Arrays.copyOfRange(goalTerms, 1, goalTerms.length);
                                testTerms = concat(testTerms, restOfGoalTerms);
                            }
                            unified = searchAndUnify(testTerms, testTerms.length == 1);
                            //old shit
                            //unified = searchAndUnify(rule.getBody(), choicePointNo, goalTerms.length == 1);
                        }
                    } else {
                        unified = false;
                    }
                }

                /*if (unified && goalTerms.length > 1)//more goal terms to process
                {
                    Term[] restOfGoalTerms = Arrays.copyOfRange(goalTerms, 1, goalTerms.length);
                    unified = searchAndUnify(restOfGoalTerms, choicePointNo, rightmostBranch);
                }*/

                if (goalTerms.length == 1 /*&& rightmostBranch*/ && leafNode /*&& maxNoOfSolutions > 1*/ && unified) {
                    //found solution
                    System.out.println("Found a solution! Solution no:- " + (solutionSnapshots.currentSolutionsCount() + 1));
                    solutionSnapshots.captureSolution(actualValuesMap);
                    if (maxNoOfSolutions > 1) {
                        if (solutionSnapshots.currentSolutionsCount() < maxNoOfSolutions) {
                            System.out.println("Setting unified to false to find more solutions");
                            unified = false;
                        }
                    }

                }

                //ROLLBACK CHANGES
                if (!unified && choicePointNo > 0) {
                    //undo variable changes
                    System.out.println("Rolling back... for choicePointNo:- " + choicePointNo);
                    Map<String, Compound> variableChanges = choicePointTracker.getVariableChanges(choicePointNo);
                    actualValuesMap.putAll(variableChanges);

                    //undo retract changes
                    List<Compound> factsRetracted = choicePointTracker.getRetractChanges(choicePointNo);
                    List<Integer> factsOffsets = choicePointTracker.getRetractOffsets(choicePointNo);
                    //LIFO: last fact to be retracted, first to be inserted back in
                    for (int i = factsRetracted.size() - 1; i >= 0; i--) {
                        kb.add(factsRetracted.get(i), factsOffsets.get(i));
                        System.out.println("Rolling back Retracted Facts for:- " + choicePointNo + ", fact offset: " + factsOffsets.get(i));
                    }
                    choicePointTracker.getRetractChanges(choicePointNo).clear();
                    choicePointTracker.getRetractOffsets(choicePointNo).clear();

                    //undo assert changes
                    List<Compound> factsAsserted = choicePointTracker.getAssertChanges(choicePointNo);
                    for (Compound factAsserted : factsAsserted) {
                        kb.retractFact(factAsserted);
                        System.out.println("Rolling back Asserted Facts for:- " + choicePointNo);
                    }
                    choicePointTracker.getAssertChanges(choicePointNo).clear();

                    choicePointTracker.removeChoicePoint();
                }

                count++;
                noOfChoices = kb.getTotalCountOfKnowledge(goalTerm.getName(), ((Compound) goalTerm).getArity());
            }
        } else if (goalTerm instanceof Expression) {
            //Expression exp = (Expression) LogicUtils.copy(goalTerm);
            Expression exp = (Expression) goalTerm;
            if (((Expression) goalTerm).getOperator() == Operator.NOT) {
                unified = !searchAndUnify(exp.getTerms(), false);

                String s = unified ? "Expression holds true" : "Expression is false";
                System.out.println(s);
            } else if (((Expression) goalTerm).getOperator() == Operator.ASSERT) {
                Term assertTerm = exp.getTermAt(0);
                Compound fact = (Compound) LogicUtils.copy(assertTerm, new HashMap<String, Term>(), this);

                kb.add(fact);
                choicePointTracker.recordAssertedFactChange(fact);
                System.out.println("Asserting fact for choice point: " + choicePointTracker.getChoicePointCount());

                unified = true;
            } else if (((Expression) goalTerm).getOperator() == Operator.RETRACT) {
                Term retractTerm = exp.getTermAt(0);
                int totalCount = kb.getTotalCountOfKnowledge(retractTerm.getName(), ((Compound) retractTerm).getArity());

                for (int i = 0; i < totalCount; i++) {
                    Compound fact = kb.getFact(retractTerm.getName(), ((Compound) retractTerm).getArity(), i);
                    if (fact != null) {
                        fact = (Compound) LogicUtils.copy(fact);

                        if (unify(retractTerm, fact)) {
                            Compound retractedFact = kb.retractFact(fact.getName(), fact.getArity(), i);
                            choicePointTracker.recordRetractedFactChange(retractedFact, i);
                            System.out.println("Retracting fact for choice point: " + choicePointTracker.getChoicePointCount());
                            break;
                        }
                    }
                }
                unified = true;
            } else {
                boolean notEqualsOp = exp.getOperator() == Operator.NOT_EQUALS;//only choice between equals and not equals
                Term term1 = exp.getTermAt(0);
                Term term2 = exp.getTermAt(1);

                unified = unify(term1, term2);

                unified ^= notEqualsOp;

                String s = unified ? "Expression holds true" : "Expression is false";
                System.out.println(s);
            }

            if (goalTerms.length == 1 && rightmostBranch /*&& maxNoOfSolutions > 1*/ && unified)//since expressions are leafs?
            {
                //found solution
                System.out.println("Found a solution! Solution no:- " + (solutionSnapshots.currentSolutionsCount() + 1));
                solutionSnapshots.captureSolution(actualValuesMap);
                if (maxNoOfSolutions > 1) {
                    if (solutionSnapshots.currentSolutionsCount() < maxNoOfSolutions) {
                        System.out.println("Setting unified to false to find more solutions");
                        unified = false;
                    }
                }
            }

            if (unified && goalTerms.length > 1)//more goal terms to process
            {
                Term[] restOfGoalTerms = Arrays.copyOfRange(goalTerms, 1, goalTerms.length);
                unified = searchAndUnify(restOfGoalTerms, rightmostBranch);
            }
        } else {
            unified = true;
        }

        return unified;
    }

    public Compound getVariableValue(Variable variable) {
        if (!isVariableAssigned(variable)) {
            return new Atom(variable.getInitialValue());
        }

        return actualValuesMap.get(variable.getInitialValue());
    }

    private boolean isVariableAssigned(Variable variable) {
        return actualValuesMap.get(variable.getInitialValue()) != null;
    }

    private void putVariableValue(Variable variable, Compound compound) {
        if (variable.getInitialValue() == null) {
            initialValueCounter++;
            variable.setInitialValue("_" + initialValueCounter);
        }

        choicePointTracker.recordOldVariableValue(variable.getInitialValue(), actualValuesMap.get(variable.getInitialValue()));
        actualValuesMap.put(variable.getInitialValue(), compound);

        printActualValuesMap();
    }

    private void printActualValuesMap() {
        /*System.out.println("~~~~~~~~~~~~~");
        for (Map.Entry<String, Compound> entry : actualValuesMap.entrySet())
        {
            System.out.println(entry.getKey() + " => " + (entry.getValue() == null ? "null" : entry.getValue().getName()));
        }
        System.out.println("~~~~~~~~~~~~~");*/
    }


    private void initVariables(Variable var1, Variable var2) {
        if (var1.getInitialValue() == null && var2.getInitialValue() == null) {
            initialValueCounter++;
            var1.setInitialValue("_" + initialValueCounter);
            var2.setInitialValue("_" + initialValueCounter);
        } else if (var1.getInitialValue() == null) {
            var1.setInitialValue(var2.getInitialValue());
        } else if (var2.getInitialValue() == null) {
            var2.setInitialValue(var1.getInitialValue());
        }
    }


    private boolean unify(Term term1, Term term2) {
        if (term1 == null || term2 == null) {
            throw new LogicException("One of the terms to unify is null:- term1: " + term1 + " term2: " + term2);
        }

        //graph visualization
        logicGraphManager.addUnifyingNode(term1, term2);

        System.out.println("Unifying:- " + term1.getName() + " and " + term2.getName());

        if (term1 instanceof Variable && term2 instanceof Variable) {
            if (!isVariableAssigned((Variable) term1) || !isVariableAssigned((Variable) term2)) {
                initVariables((Variable) term1, (Variable) term2);
                return true;
            } else if (unify(getVariableValue((Variable) term1), getVariableValue((Variable) term2))) {
                return true;
            }
        } else if (term1 instanceof Variable) {
            if (!isVariableAssigned((Variable) term1)) {
                putVariableValue((Variable) term1, (Compound) term2);
                return true;
            } else if (unify(getVariableValue((Variable) term1), term2))//if (getVariableValue(term1.getName()).equals((term2)))
            {
                return true;
            }
        } else if (term2 instanceof Variable) {
            if (!isVariableAssigned((Variable) term2)) {
                putVariableValue((Variable) term2, (Compound) term1);
                return true;
            } else if (unify(getVariableValue((Variable) term2), term1))//if (getVariableValue(term2.getName()).equals((term1)))
            {
                return true;
            }
        } else {
            Compound compoundTerm1 = (Compound) term1;
            Compound compoundTerm2 = (Compound) term2;

            if (compoundTerm1.getName().equals(compoundTerm2.getName())) {
                if (compoundTerm1.getArity() == compoundTerm2.getArity()) {
                    for (int i = 0; i < compoundTerm1.getArgs().length; i++) {
                        if (!unify(compoundTerm1.getArgs()[i], compoundTerm2.getArg(i))) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }

        System.out.println("Failed to unify!");
        return false;
    }
}