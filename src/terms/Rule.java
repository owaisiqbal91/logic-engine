package terms;

public class Rule
{
    private Compound head;
    private Term[] body;

    public Rule(Compound head, Term[] body)
    {
        this.head = head;
        this.body = body;
    }

    public Compound getHead()
    {
        return head;
    }

    public Term[] getBody()
    {
        return body;
    }

    public Term getBodyTerm(int index)
    {
        return body[index];
    }
}
