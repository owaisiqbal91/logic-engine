package core;

import terms.Compound;
import terms.Rule;

import java.util.*;

public class KnowledgeBase
{
    //the key for these two maps is a string: name + "-" + arity + "-" + count
    private Map<String, List<Compound>> factMap;
    private Map<String, List<Rule>> ruleMap;

    //this is for saving the maps and returning the KB to its old state
    private Map<String, List<Compound>> savedFactMap;
    private Map<String, List<Rule>> savedRuleMap;

    public KnowledgeBase()
    {
        factMap = new HashMap<String, List<Compound>>();
        ruleMap = new HashMap<String, List<Rule>>();
    }

    public void add(Compound fact)
    {
        String signature = fact.getName() + "-" + fact.getArity();

        if (factMap.get(signature) == null)
        {
            factMap.put(signature, new ArrayList<Compound>());
        }
        factMap.get(signature).add(fact);
    }

    public void add(Compound fact, int offset)
    {
        String signature = fact.getName() + "-" + fact.getArity();

        if (factMap.get(signature) == null)
        {
            factMap.put(signature, new ArrayList<Compound>());
        }
        factMap.get(signature).add(offset, fact);
    }

    public void add(Rule rule)
    {
        /*String signature = rule.getHead().getName() + "-" + rule.getHead().getArity();

        int newCount = 0;
        if (sameKnowledgeSignatureMap.get(signature) != null)
        {
            newCount = (sameKnowledgeSignatureMap.get(signature));
        }
        newCount++;
        sameKnowledgeSignatureMap.put(signature, newCount);


        ruleMap.put(signature + "-" + newCount, rule);*/

        String signature = rule.getHead().getName() + "-" + rule.getHead().getArity();

        if (ruleMap.get(signature) == null)
        {
            ruleMap.put(signature, new ArrayList<Rule>());
        }
        ruleMap.get(signature).add(rule);
    }

    public Rule getRule(String ruleName, int arity, int offset)
    {
        String signature = ruleName + "-" + arity;

        return ruleMap.get(signature) == null ? null : ruleMap.get(signature).get(offset);
    }

    public Compound getFact(String factName, int arity, int offset)
    {
        String signature = factName + "-" + arity;

        return factMap.get(signature) == null ? null : factMap.get(signature).get(offset);
    }

    public int getTotalCountOfKnowledge(String name, int arity)
    {
        String signature = name + "-" + arity;
        int totalFacts = factMap.get(signature) == null ? 0 : factMap.get(signature).size();
        int totalRules = ruleMap.get(signature) == null ? 0 : ruleMap.get(signature).size();
        return (totalFacts + totalRules);
    }

    public int getTotalCountOfFact(String name, int arity)
    {
        String signature = name + "-" + arity;
        return factMap.get(signature) == null ? 0 : factMap.get(signature).size();
    }

    public int getTotalCountOfRule(String name, int arity)
    {
        String signature = name + "-" + arity;
        return ruleMap.get(signature) == null ? 0 : ruleMap.get(signature).size();
    }

    public Compound retractFact(String factName, int arity, int offset)
    {
        String signature = factName + "-" + arity;
        return factMap.get(signature).remove(offset);
    }

    public void retractFact(Compound fact)
    {
        String signature = fact.getName() + "-" + fact.getArity();
        Iterator<Compound> iterator = factMap.get(signature).iterator();
        while (iterator.hasNext())
        {
            if (iterator.next().equals(fact))
            {
                iterator.remove();
                break;
            }
        }
    }

    //after a query finishes
    public void undoAllRetract()
    {
    }
}
