package terms;

public class Query
{
    private Term[] terms;

    public Query(Term term)
    {
        terms = new Term[]{term};
    }

    public Query(Term terms[])
    {
        this.terms = terms;
    }

    public Term[] getTerms()
    {
        return terms;
    }

    public Term getTermAt(int index)
    {
        return terms[index];
    }
}
