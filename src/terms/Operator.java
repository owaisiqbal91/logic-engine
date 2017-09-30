package terms;

public enum Operator
{
    EQUALS(2),
    NOT_EQUALS(2),
    NOT(1),//unary operator
    RETRACT(1),
    ASSERT(1);

    public int arity;

    Operator(int arity)
    {
        this.arity = arity;
    }
}
