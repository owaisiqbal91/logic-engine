package terms;

public class Variable extends Term
{
    private String initialValue;

    public Variable(String name)
    {
        setName(name);
    }

    public String getInitialValue()
    {
        return initialValue;
    }

    public void setInitialValue(String initialValue)
    {
        this.initialValue = initialValue;
    }
}
