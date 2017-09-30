package terms;

import exceptions.LogicException;
import org.apache.commons.lang3.StringUtils;

public class Compound extends Term
{
    private Term[] args;
    private boolean initialCompound;

    public Compound(String name, Term[] args)
    {
        if (args == null || args.length == 0)
            throw new LogicException("Compound terms must have at least one argument");
        setName(name);
        this.args = args;
    }

    protected Compound(String name)//only subclasses can have zero arguments
    {
        setName(name);
        args = new Term[]{};
    }

    public int getArity()
    {
        return args.length;
    }

    public Term[] getArgs()
    {
        return args;
    }

    public Term getArg(int index)
    {
        return args[index];
    }

    @Override
    public String toString()
    {
        return getName() + "(" + StringUtils.join(args, ",") + ")";
    }
}
