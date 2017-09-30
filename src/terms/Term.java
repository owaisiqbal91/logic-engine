package terms;

/*
terms.Term
 |
 +--- terms.Variable
 |
 +--- terms.Compound
        |
        +--- terms.Atom
 |
 +--- terms.Expression
 */
public abstract class Term
{
    private String name;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
