package terms;

import org.apache.commons.lang3.StringUtils;

public class Expression extends Term
{
    private Term[] terms;
    private Operator operator;

    public Expression(Term[] terms, Operator operator)
    {
        this.terms = terms;
        this.operator = operator;
    }

    public Term[] getTerms()
    {
        return terms;
    }

    public Term getTermAt(int index)
    {
        return terms[index];
    }

    public Operator getOperator()
    {
        return operator;
    }

    @Override
    public String toString()
    {
        return operator + "(" + StringUtils.join(terms, ",") + ")";
    }
}
