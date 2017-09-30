package core;

import terms.Compound;

import java.util.*;

public class ChoicePointTracker
{
    private int choicePointCount = 0;

    private Map<Integer, Map<String, Compound>> variableValueChanges;
    private Map<Integer, List<Compound>> retractedFactChanges;
    private Map<Integer, List<Integer>> retractedFactOffsets;
    private Map<Integer, List<Compound>> assertedFactChanges;

    public ChoicePointTracker()
    {
        variableValueChanges = new HashMap<Integer, Map<String, Compound>>();
        retractedFactChanges = new HashMap<Integer, List<Compound>>();
        retractedFactOffsets = new HashMap<Integer, List<Integer>>();
        assertedFactChanges = new HashMap<Integer, List<Compound>>();
    }

    public int newChoicePoint()
    {
        choicePointCount++;
        variableValueChanges.put(choicePointCount, new HashMap<String, Compound>());
        retractedFactChanges.put(choicePointCount, new ArrayList<Compound>());
        retractedFactOffsets.put(choicePointCount, new ArrayList<Integer>());
        assertedFactChanges.put(choicePointCount, new ArrayList<Compound>());

        return choicePointCount;
    }

    public void removeChoicePoint()
    {
        variableValueChanges.remove(choicePointCount);
        retractedFactChanges.remove(choicePointCount);
        retractedFactOffsets.remove(choicePointCount);
        assertedFactChanges.remove(choicePointCount);
        choicePointCount--;
    }


    public void recordOldVariableValue(String variableInitialValue, Compound oldVariableValue)
    {
        if (choicePointCount > 0)
        {
            variableValueChanges.get(choicePointCount).put(variableInitialValue, oldVariableValue);
        }
    }

    public Map<String, Compound> getVariableChanges(int cpCount)
    {
        if (cpCount > 0)
        {
            return variableValueChanges.get(cpCount);
        } else
        {
            return new HashMap<String, Compound>();
        }
    }

    public void recordRetractedFactChange(Compound fact, int offset)
    {
        if (choicePointCount > 0)
        {
            retractedFactChanges.get(choicePointCount).add(fact);
            retractedFactOffsets.get(choicePointCount).add(offset);
        }
    }

    public List<Compound> getRetractChanges(int cpCount)
    {
        if (cpCount > 0)
        {
            return retractedFactChanges.get(cpCount);
        } else
        {
            return new ArrayList<Compound>();
        }
    }

    public List<Integer> getRetractOffsets(int cpCount)
    {
        if (cpCount > 0)
        {
            return retractedFactOffsets.get(cpCount);
        } else
        {
            return new ArrayList<Integer>();
        }
    }


    public void recordAssertedFactChange(Compound fact)
    {
        if (choicePointCount > 0)
        {
            assertedFactChanges.get(choicePointCount).add(fact);
        }
    }

    public List<Compound> getAssertChanges(int cpCount)
    {
        if (cpCount > 0)
        {
            return assertedFactChanges.get(cpCount);
        } else
        {
            return new ArrayList<Compound>();
        }
    }

    public int getChoicePointCount()
    {
        return choicePointCount;
    }

    /*class UniqueFact
    {
        public String factName;
        public int arity;
        public int offset;

        public UniqueFact(String factName, int arity, int offset)
        {
            this.factName = factName;
            this.arity = arity;
            this.offset = offset;
        }
    }*/
}
