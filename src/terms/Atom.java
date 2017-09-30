package terms;

public class Atom extends Compound
{
    public Atom(String name)
    {
        super(name);
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
