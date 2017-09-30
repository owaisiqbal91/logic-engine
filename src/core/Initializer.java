package core;

import terms.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Initializer
{
    private static int genNumber = 0;

    public static void initializeVariables(Term term, Set<Variable> variableSet)
    {
        if (term instanceof Variable)
        {
            variableSet.add((Variable) term);
            /*if (variableMap.get(term.getName()) == null)//not initialized yet in this term
            {
                genNumber++;

                //create new var with diff name
                //variableMap.put(term.getName(), new Atom("_" + String.valueOf(genNumber)));
            }*/
        } else
        {
            if (term instanceof Expression)
            {
                for (Term argTerm : ((Expression) term).getTerms())
                {
                    initializeVariables(argTerm, variableSet);
                }
            } else if (term instanceof Compound)
            {
                for (Term argTerm : ((Compound) term).getArgs())
                {
                    initializeVariables(argTerm, variableSet);
                }
            }
        }
    }
}
