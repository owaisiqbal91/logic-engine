package core;

import terms.*;

import java.util.HashMap;
import java.util.Map;

public class LogicUtils
{
    public static Term copy(Term term)
    {
        return copy(term, new HashMap<String, Term>(), null);
    }

    public static Term[] copy(Term[] terms)
    {
        return copy(terms, new HashMap<String, Term>());
    }

    public static Term[] copy(Term[] terms, Map<String, Term> termsMap)
    {
        Term[] newTerms = new Term[terms.length];
        for (int i = 0; i < newTerms.length; i++)
        {
            newTerms[i] = copy(terms[i], termsMap, null);
        }

        return newTerms;
    }

    public static Term copy(Term term, Map<String, Term> termsMap, Evaluator ev)//terms map to store same atom, variable for given fact/rule
    {
        if (term instanceof Variable)
        {
            if (ev == null)
            {
                if (termsMap.get(term.getName()) == null)
                {
                    termsMap.put(term.getName(), new Variable(term.getName()));
                }
                return termsMap.get(term.getName());
            } else
            {
                return ev.getVariableValue((Variable) term);
            }
        } else if (term instanceof Expression)
        {
            Expression exp = (Expression) term;
            Term[] args = new Term[exp.getTerms().length];
            for (int i = 0; i < exp.getTerms().length; i++)
            {
                args[i] = copy(exp.getTermAt(i), termsMap, ev);
            }
            return new Expression(args, exp.getOperator());
        } else if (term instanceof Compound)
        {
            Compound comp = (Compound) term;
            if (comp.getArgs().length == 0)
            {
                if (termsMap.get(comp.getName()) == null)
                {
                    termsMap.put(comp.getName(), new Atom(comp.getName()));
                }
                return termsMap.get(comp.getName());
            }
            Term[] args = new Term[comp.getArgs().length];
            for (int i = 0; i < comp.getArgs().length; i++)
            {
                args[i] = copy(comp.getArg(i), termsMap, ev);
            }
            return new Compound(comp.getName(), args);
        }

        return null;
    }

   /* public static boolean equals(Term term1, Term term2)
    {
        if (term1 instanceof Compound && term2 instanceof Compound)
        {
            boolean isEqual = true;
            Compound comp1 = (Compound) term1;
            Compound comp2 = (Compound) term2;
            if (term1.getName().equals(term2.getName()) && comp1.getArity() == comp2.getArity())
            {
                for (int i = 0; i < comp1.getArity(); i++)
                {
                    if (!equals(comp1.getArg(i), comp2.getArg(i)))
                    {
                        isEqual = false;
                        break;
                    }
                }
            } else
            {
                return false;
            }

            return isEqual;

        } else if (term1 instanceof Variable && term2 instanceof Variable)
        {
            if (term1.getName().equals(term2.getName()))
            {
                return true;
            }
        }

        return false;
    }*/
}
