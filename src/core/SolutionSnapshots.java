package core;

import terms.Atom;
import terms.Compound;
import terms.Variable;

import java.util.*;

public class SolutionSnapshots
{
    private Set<Variable> variablesToStore;
    private List<Map<Variable, Compound>> solutionMapList;

    public SolutionSnapshots(Set<Variable> queryVariables)
    {
        this.variablesToStore = queryVariables;
        solutionMapList = new ArrayList<Map<Variable, Compound>>();
    }

    public void captureSolution(Map<String, Compound> actualValuesMap)
    {
        System.out.println("In capture solution");
        Map<Variable, Compound> solutionMap = new HashMap<Variable, Compound>();
        for (Variable var : variablesToStore)
        {
            Compound value = getVariableValue(var, actualValuesMap);
            solutionMap.put(var, value);
            System.out.println("For " + var.getName() + ": " + value.getName());
        }
        solutionMapList.add(solutionMap);
    }

    public Compound getVariableValue(Variable variable, Map<String, Compound> actualValuesMap)//CAN GO INFINITE??
    {
        if (actualValuesMap.get(variable.getInitialValue()) == null)
        {
            return new Atom(variable.getInitialValue());
        }

        return actualValuesMap.get(variable.getInitialValue());
    }

    public List<Map<Variable, Compound>> getSolutionMapList()
    {
        return solutionMapList;
    }

    public int currentSolutionsCount()
    {
        return solutionMapList.size();
    }
}
