package tests;

import core.KnowledgeBase;
import interpreter.JSONInterpreter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import terms.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class KBCreator
{
    public static KnowledgeBase setupKnowledgeBase()
    {
        KnowledgeBase kb = new KnowledgeBase();

        //basic facts
        kb.add(new Compound("woman", new Term[]{new Atom("mia")}));
        kb.add(new Compound("plays", new Term[]{new Atom("mia"), new Atom("guitar")}));
        kb.add(new Atom("party"));

        //lines
        //vertical(line(point(X,Y),point(X,Z))).
        Variable vertX = new Variable("X");
        Variable vertY = new Variable("Y");
        Variable vertZ = new Variable("Z");
        Compound vertical = new Compound("vertical", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{vertX, vertY}), new Compound("point", new Term[]{vertX, vertZ})})});
        kb.add(vertical);

        //horizontal(line(point(X,Y),point(Z,Y))).
        Variable horX = new Variable("X");
        Variable horY = new Variable("Y");
        Variable horZ = new Variable("Z");
        Compound horizontal = new Compound("horizontal", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{horX, horY}), new Compound("point", new Term[]{horZ, horY})})});
        kb.add(horizontal);

        //loves(X,X)  =  loves(marcellus,mia).
        Compound loves = new Compound("loves", new Term[]{new Atom("marcellus"), new Atom("mia")});
        Compound loves2 = new Compound("loves", new Term[]{new Atom("vincent"), new Atom("mia")});
        kb.add(loves);
        kb.add(loves2);
        // jealous(X,Y):-  loves(X,Z),  loves(Y,Z), X != Y.
        Variable jX = new Variable("X");
        Variable jY = new Variable("Y");
        Variable jZ = new Variable("Z");
        Compound head = new Compound("jealous", new Term[]{jX, jY});
        Compound body1 = new Compound("loves", new Term[]{jX, jZ});
        Compound body2 = new Compound("loves", new Term[]{jY, jZ});
        Expression exp = new Expression(new Term[]{new Expression(new Term[]{jX, jY}, Operator.EQUALS)}, Operator.NOT);
        Rule jRule = new Rule(head, new Term[]{body1, body2, exp});
        kb.add(jRule);

        //k(s(g),  t(k)).
        Compound sComp = new Compound("s", new Term[]{new Atom("g")});
        Compound tComp = new Compound("t", new Term[]{new Atom("k")});
        Compound kComp = new Compound("k", new Term[]{sComp, tComp});
        kb.add(kComp);

        /*f(a).
        f(b).

        g(a).
        g(b).

        h(b).

        k(X)  :-  f(X),  g(X),  h(X).

        ?- k(Y).*/
        Atom aAtom = new Atom("a");
        Atom bAtom = new Atom("b");

        Variable xVar = new Variable("X");

        Compound f1 = new Compound("f", new Term[]{aAtom});
        Compound f2 = new Compound("f", new Term[]{bAtom});
        kb.add(f1);
        kb.add(f2);

        Compound g1 = new Compound("g", new Term[]{aAtom});
        Compound g2 = new Compound("g", new Term[]{bAtom});
        kb.add(g1);
        kb.add(g2);

        Compound h = new Compound("h", new Term[]{bAtom});
        kb.add(h);

        head = new Compound("k", new Term[]{xVar});
        Compound[] body = new Compound[]{new Compound("f", new Term[]{xVar}), new Compound("g", new Term[]{xVar}), new Compound("h", new Term[]{xVar})};
        Rule rule = new Rule(head, body);
        kb.add(rule);

        return kb;
    }

    public static KnowledgeBase setupJSONKnowledgeBase() throws FileNotFoundException, ParseException
    {
        String content = new Scanner(new File("input/kb.json")).useDelimiter("\\Z").next();

        JSONParser parser = new JSONParser();

        JSONObject obj = (JSONObject) parser.parse(content);
        JSONInterpreter interpreter = new JSONInterpreter();
        KnowledgeBase kb = new KnowledgeBase();
        interpreter.parse(obj, kb);

        return kb;
    }
}
