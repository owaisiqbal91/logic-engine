package interpreter;

import core.Evaluator;
import core.KnowledgeBase;
import exceptions.LogicException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import terms.*;
import tests.KBCreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class JSONInterpreter
{
    private Set<String> operatorValues = new HashSet<String>();

    public JSONInterpreter()
    {
        for (Operator o : Operator.values())
        {
            operatorValues.add(o.name());
        }
    }

    public static void main(String[] args) throws Exception
    {
        KnowledgeBase kb = KBCreator.setupJSONKnowledgeBase();
        Variable yVar = new Variable("Y");

        Evaluator ev = new Evaluator(kb);
        assertTrue(ev.query(new Query(new Compound("k", new Term[]{yVar}))));
        assertTrue(ev.getVariableValue(yVar).getName().equals("b"));
    }

    /*
    Some implicit assumptions:
    facts only have to be compounds
    first of any term jsonArray will always be a string(the name of the term or the operator name)
     */
    public void parse(JSONObject obj, KnowledgeBase kb)
    {
        JSONObject kbObj = (JSONObject) obj.get("kb");

        JSONArray facts = (JSONArray) kbObj.get("facts");
        for (int i = 0; i < facts.size(); i++)
        {
            Compound fact = (Compound) parseTerm((JSONArray) facts.get(i), new HashMap<String, Term>());
            kb.add(fact);
        }

        JSONArray rules = (JSONArray) kbObj.get("rules");
        for (int i = 0; i < rules.size(); i++)
        {
            JSONArray jsonRule = (JSONArray) rules.get(i);
            Map<String, Term> termsMap = new HashMap<String, Term>();
            Compound head = (Compound) parseTerm((JSONArray) jsonRule.get(0), termsMap);
            Term body[] = new Term[jsonRule.size() - 1];
            for (int j = 1; j < jsonRule.size(); j++)
            {
                JSONArray termJson = (JSONArray) jsonRule.get(j);
                body[j - 1] = parseTerm(termJson, termsMap);
            }

            Rule rule = new Rule(head, body);
            kb.add(rule);
        }
    }

    private Term parseTerm(JSONArray array, Map<String, Term> termsMap)//terms map to store same atom, variable for given fact/rule
    {
        if (array.size() == 1)//atom
        {
            return parseAtom((String) array.get(0), termsMap);
        }
        if (operatorValues.contains(array.get(0)))//expression
        {
            Operator op = Operator.valueOf((String) array.get(0));
            Term[] operands = parseArguments(array, 1, termsMap);
            Expression expression = new Expression(operands, op);

            return expression;
        }

        Term[] args = parseArguments(array, 1, termsMap);
        return new Compound((String) array.get(0), args);
    }

    private Term[] parseArguments(JSONArray array, int startIndex, Map<String, Term> termsMap)
    {
        Term[] args = new Term[array.size() - startIndex];
        for (int i = startIndex; i < array.size(); i++)
        {
            if (array.get(i) instanceof JSONArray)
            {
                args[i - startIndex] = parseTerm((JSONArray) array.get(i), termsMap);
            } else if (((String) array.get(i)).isEmpty())
            {
                throw new LogicException("String is empty");
            } else if (Character.isUpperCase(((String) array.get(i)).charAt(0)))
            {
                args[i - startIndex] = parseVariable((String) array.get(i), termsMap);
            } else
            {
                args[i - startIndex] = parseAtom((String) array.get(i), termsMap);
            }
        }

        return args;
    }

    private Atom parseAtom(String atomName, Map<String, Term> termsMap)
    {
        if (termsMap.get(atomName) == null)
        {
            termsMap.put(atomName, new Atom(atomName));
        }
        return (Atom) termsMap.get(atomName);
    }

    private Variable parseVariable(String variableName, Map<String, Term> termsMap)
    {
        if (termsMap.get(variableName) == null)
        {
            termsMap.put(variableName, new Variable(variableName));
        }
        return (Variable) termsMap.get(variableName);
    }
}
