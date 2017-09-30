package tests;

import core.Evaluator;
import core.KnowledgeBase;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import terms.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class LogicTests
{
    private KnowledgeBase kb;
    private Evaluator ev;

    @Rule
    public TestRule watcher = new TestWatcher()
    {
        protected void starting(Description description)
        {
            System.out.println("Starting test: " + description.getMethodName());
        }

        protected void finished(Description description)
        {
            //FIXME infinite loop to keep the graph display up
            while (true)
            {

            }
        }
    };

    @Parameterized.Parameters(name = "{index}: kb({0})")
    public static Iterable<Object[]> data() throws FileNotFoundException, ParseException
    {
        KnowledgeBase kb = KBCreator.setupKnowledgeBase();
        Object[] normalParameters = new Object[]{kb};
        //KnowledgeBase kb2 = KBCreator.setupJSONKnowledgeBase();
        //Object[] jsonParameters = new Object[]{kb2};
        List<Object[]> inputs = new ArrayList<Object[]>();
        inputs.add(normalParameters);
        //inputs.add(jsonParameters);

        return inputs;
    }

    public LogicTests(KnowledgeBase kb)
    {
        this.kb = kb;
        ev = new Evaluator(kb);
    }

    @Test
    public void test1()
    {
        assertTrue(ev.query(new Query(new Atom("party"))));
    }

    @Test
    public void test2()
    {
        assertTrue(ev.query(new Query(new Compound("plays", new Term[]{new Atom("mia"), new Atom("guitar")}))));
    }

    @Test
    public void test3()
    {
        assertFalse(ev.query(new Query(new Compound("plays", new Term[]{new Atom("guitar"), new Atom("mia")}))));
    }

    @Test
    public void test4()
    {
        Variable x = new Variable("X");
        ev.query(new Query(new Compound("woman", new Term[]{x})));
        assertTrue(ev.getVariableValue(x).getName().equals(("mia")));
    }

    @Test
    public void test5()
    {
        Variable x = new Variable("X");
        Variable y = new Variable("Y");
        ev.query(new Query(new Compound("plays", new Term[]{x, y})));
        assertTrue(ev.getVariableValue(x).getName().equals(("mia")));
        assertTrue(ev.getVariableValue(y).getName().equals(("guitar")));
    }

    @Test
    public void test6()
    {
        //vertical(line(point(1,1),point(1,3))).
        Variable x = new Variable("X");
        Compound qComp = new Compound("vertical", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{new Atom("1"), new Atom("1")}), new Compound("point", new Term[]{x, new Atom("2")})})});

        if (ev.query(new Query(qComp)))
        {
            assertTrue(ev.getVariableValue(x).getName().equals(("1")));
        } else
        {
            assert false;
        }

        x = new Variable("X");
        qComp = new Compound("vertical", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{x, new Atom("1")}), new Compound("point", new Term[]{new Atom("5"), new Atom("2")})})});

        if (ev.query(new Query(qComp)))
        {
            assertTrue(ev.getVariableValue(x).getName().equals(("5")));
        } else
        {
            assert false;
        }

        Atom seven = new Atom("7");
        qComp = new Compound("vertical", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{seven, new Atom("5")}), new Compound("point", new Term[]{seven, new Atom("6")})})});
        assertTrue(ev.query(new Query(qComp)));

        qComp = new Compound("vertical", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{new Atom("7"), new Atom("6")}), new Compound("point", new Term[]{new Atom("5"), new Atom("6")})})});
        assertFalse(ev.query(new Query(qComp)));
    }

    @Test
    public void test7()
    {
        //horizontal(line(point(2,3),P)).
        Variable p = new Variable("P");
        Compound qComp = new Compound("horizontal", new Term[]{new Compound("line", new Term[]{new Compound("point", new Term[]{new Atom("2"), new Atom("3")}), p})});
        ev.query(new Query(qComp));
        Compound answer = ev.getVariableValue(p);

        // P  =  point(_1972,3);
        assertTrue(answer.getName().equals("point"));
        assertTrue(ev.getVariableValue((Variable) answer.getArg(1)).getName().equals("3"));
    }

    @Test
    public void test8()
    {
        Variable lovesX = new Variable("X");
        Compound qComp = new Compound("loves", new Term[]{lovesX, lovesX});

        assertFalse(ev.query(new Query(qComp)));
    }

    //multiple goals in queries
    @Test
    public void test9()
    {
        Variable lovesX = new Variable("X");
        Compound qComp = new Compound("loves", new Term[]{new Atom("marcellus"), lovesX});
        Compound qComp2 = new Compound("woman", new Term[]{lovesX});

        ev.query(new Query((new Term[]{qComp, qComp2})));
        assertTrue(ev.getVariableValue(lovesX).getName().equals(("mia")));
    }

    @Test
    public void test10()
    {
        //k(s(g),  t(k))  =  k(X,t(Y)).
        Variable xVar = new Variable("X");
        Variable yVar = new Variable("Y");
        Compound qComp = new Compound("k", new Term[]{xVar, new Compound("t", new Term[]{yVar})});

        Compound sComp = new Compound("s", new Term[]{new Atom("g")});

        if (ev.query(new Query(qComp)))
        {
            Compound xValue = ev.getVariableValue(xVar);
            assertTrue(xValue.getName().equals(sComp.getName()));
            assertTrue(xValue.getArg(0).getName().equals(sComp.getArg(0).getName()));
            assertTrue(ev.getVariableValue(yVar).getName().equals("k"));
        } else
        {
            assert false;
        }
    }

    @Test
    public void test11()
    {
        //jealous(marsellus,W).
        Variable w = new Variable("W");
        Compound qComp = new Compound("jealous", new Term[]{new Atom("marcellus"), w});
        ev.query(new Query(qComp));
        //here if the fact of loves(marc, mia) was inserted before loves(vincent, mia), it would give the output as marc instead of vincent(multiple solutions not yet implemented), and would imply that marc is jealous of marc.  A NOT_EQUAL operator applied in the jealous rule(X!=Y) would always give vincent, the order wouldnt matter.
        assertTrue(ev.getVariableValue(w).getName().equals("vincent"));
    }

    //backtracking test
    @Test
    public void test12()
    {
        /*f(a).
        f(b).

        g(a).
        g(b).

        h(b).

        k(X)  :-  f(X),  g(X),  h(X).

        ?- k(Y).*/

        //evaluate
        Variable yVar = new Variable("Y");
        assertTrue(ev.query(new Query(new Compound("k", new Term[]{yVar}))));
        assertTrue(ev.getVariableValue(yVar).getName().equals("b"));
    }

    //test equals and not equals
    @Test
    public void test13()
    {
        Variable lovesX = new Variable("X");
        Compound qComp = new Compound("loves", new Term[]{lovesX, lovesX});
        Compound qComp2 = new Compound("loves", new Term[]{new Atom("marcellus"), new Atom("mia")});

        assertFalse(ev.query(new Query(new Expression(new Term[]{qComp, qComp2}, Operator.EQUALS))));
        assertTrue(ev.query(new Query(new Expression(new Term[]{qComp, qComp2}, Operator.NOT_EQUALS))));
    }

    //multiple solution
    @Test
    public void test14()
    {
        //jealous(A,B).
        Variable A = new Variable("A");
        Variable B = new Variable("B");
        Compound qComp = new Compound("jealous", new Term[]{A, B});
        ev.query(new Query(qComp), 3);
        //System.out.println(ev.getVariableValue(A.getName()).getName());
        //System.out.println(ev.getVariableValue(B.getName()).getName());
        //assertTrue(ev.getVariableValue(a.getName()).getName().equals("vincent"));
    }

    //this is for testing resetting of variables of kb after a goalterm
    @Test
    public void test15()
    {
        Compound qComp = new Compound("jealous", new Term[]{new Atom("marcellus"), new Atom("vincent")});
        Compound qComp2 = new Compound("jealous", new Term[]{new Atom("vincent"), new Atom("marcellus")});
        assertTrue(ev.query(new Query(new Term[]{qComp, qComp2})));
    }


    @Before
    public void printBefore()
    {
        System.out.println();
        System.out.println("---------------------------");
    }

    @After
    public void printAfter()
    {
        System.out.println("---------------------------");
        System.out.println();
    }
}