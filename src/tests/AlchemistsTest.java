package tests;

import core.Evaluator;
import core.KnowledgeBase;
import core.SolutionSnapshots;
import interpreter.JSONInterpreter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import terms.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class AlchemistsTest
{
    private static Map<String, Compound> ingredientMap = new HashMap<String, Compound>();
    private static Map<String, Variable[]> ingredientComponentsMap = new HashMap<String, Variable[]>();

    public static void main(String[] args) throws FileNotFoundException, ParseException
    {
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(new File("input/alchemists.json")).useDelimiter("\\Z");
            String content = scanner.next();

            JSONParser parser = new JSONParser();

            JSONObject obj = (JSONObject) parser.parse(content);
            JSONInterpreter interpreter = new JSONInterpreter();
            KnowledgeBase kb = new KnowledgeBase();
            interpreter.parse(obj, kb);

            initializeMaps();

            Evaluator ev = new Evaluator(kb);

            String[][] input = new String[][]{{"claw", "mandrake", "paralysis"}, {"claw", "scorpion", "wisdom"}, {"mandrake", "scorpion", "paralysis"}, {"claw", "toad", "paralysis"}, {"mandrake", "toad", "insanity"}, {"claw", "fern", "neutral"}, {"toad", "fern", "poison"}, {"claw", "feather", "healing"}, {"fern", "feather", "insanity"}};

            String[][] input1 = new String[][]{{"claw", "mandrake", "healing"}, {"mandrake", "fern", "speed"}, {"toad", "claw", "healing"}};

            Query query = composeQuery(input1);

            /*Compound testing = testIngredients("claw", "mandrake", "paralysis");
            Expression exp1 = retractFact("claw");
            Expression exp3 = assertFact("claw");
            Expression exp2 = retractFact("mandrake");
            Expression exp4 = assertFact("mandrake");
            Compound testing2 = testIngredients("claw", "scorpion", "wisdom");
            Expression exp5 = retractFact("scorpion");
            Expression exp6 = assertFact("scorpion");
            Compound testing3 = testIngredients("mandrake", "scorpion", "paralysis");
            Compound testing4 = testIngredients("claw", "toad", "paralysis");
            Expression exp7 = retractFact("toad");
            Expression exp8 = assertFact("toad");
            Compound testing5 = testIngredients("mandrake", "toad", "insanity");
            Compound testing6 = testIngredients("claw", "fern", "neutral");
            Expression exp9 = retractFact("fern");
            Expression exp10 = assertFact("fern");
            Compound testing7 = testIngredients("toad", "fern", "poison");
            Compound testing8 = testIngredients("claw", "feather", "healing");
            Expression exp11 = retractFact("feather");
            Expression exp12 = assertFact("feather");
            Compound testing9 = testIngredients("fern", "feather", "insanity");

            Query query = new Query(new Term[]{testing, exp1, exp2, exp3, exp4, testing2, testing3, exp5, exp6, testing4, exp7, exp8, testing5, testing6, exp9, exp10, testing7, testing8, exp11, exp12, testing9});*/

            ev.query(query);
            SolutionSnapshots solutionSnapshots = ev.getSolutionSnapshots();
            List<Map<Variable, Compound>> solutionMapList = solutionSnapshots.getSolutionMapList();

            List<Map<String, String>> output = new ArrayList<Map<String, String>>();
            for (Map<Variable, Compound> solutionMap : solutionMapList)
            {
                Map<String, String> outputMap = new HashMap<String, String>();
                for (Map.Entry<String, Variable[]> entry : ingredientComponentsMap.entrySet())
                {
                    String alchemical = "";
                    for (int i = 0; i < entry.getValue().length; i++)
                    {
                        if (solutionMap.get(ingredientComponentsMap.get(entry.getKey())[i]) != null)
                            alchemical += solutionMap.get(ingredientComponentsMap.get(entry.getKey())[i]).getName();
                        else
                            alchemical += "NULL";
                        if (i != entry.getValue().length - 1)
                        {
                            alchemical += "-";
                        }
                    }
                    outputMap.put(entry.getKey(), alchemical);
                }
                output.add(outputMap);
            }

            System.out.println("Printing components");
            for (Map<String, String> outputMap : output)
            {
                System.out.println("Solution-----");
                for (Map.Entry<String, String> entry : outputMap.entrySet())
                {
                    System.out.println(entry.getKey() + " ====> " + entry.getValue());
                }
                System.out.println("-----");
            }

            /*System.out.println("Printing components");
            printComponents(ev, "claw");
            printComponents(ev, "mandrake");
            printComponents(ev, "scorpion");
            printComponents(ev, "toad");
            printComponents(ev, "fern");
            printComponents(ev, "feather");*/
        } finally
        {
            if(scanner != null)
            {
                scanner.close();
            }
        }
    }

    private static Query composeQuery(String[][] input)
    {
        Set<String> seenIngredients = new HashSet<String>();
        List<Term> queryTerms = new ArrayList<Term>();

        for (int i = 0; i < input.length; i++)
        {
            String[] test = input[i];
            Compound testingCompound = testIngredients(test[0], test[1], test[2]);
            queryTerms.add(testingCompound);

            if (i != input.length - 1)
            {
                if (seenIngredients.add(test[0]))
                {
                    Expression retractExp = retractFact(test[0]);
                    Expression assertExp = assertFact(test[0]);
                    queryTerms.add(retractExp);
                    queryTerms.add(assertExp);
                }
                if (seenIngredients.add(test[1]))
                {
                    Expression retractExp = retractFact(test[1]);
                    Expression assertExp = assertFact(test[1]);
                    queryTerms.add(retractExp);
                    queryTerms.add(assertExp);
                }
            }
        }

        System.out.println(queryTerms.size());
        Query query = new Query(queryTerms.toArray(new Term[queryTerms.size()]));

        return query;
    }


    private static void initializeMaps()
    {
        ingredientMap.put("claw", new Atom("claw"));
        ingredientMap.put("mandrake", new Atom("mandrake"));
        ingredientMap.put("toad", new Atom("toad"));
        ingredientMap.put("fern", new Atom("fern"));
        ingredientMap.put("lily", new Atom("lily"));
        ingredientMap.put("mushroom", new Atom("mushroom"));
        ingredientMap.put("feather", new Atom("feather"));
        ingredientMap.put("scorpion", new Atom("scorpion"));

        int counter = 1;

        for (String ingredient : ingredientMap.keySet())
        {
            Variable[] varArr = new Variable[3];
            for (int i = 0; i < 3; i++)
            {
                varArr[i] = new Variable("S" + counter);
                counter++;
            }

            ingredientComponentsMap.put(ingredient, varArr);
        }
    }


    private static Compound testIngredients(String ing1, String ing2, String potion)
    {
        Atom potionAtom = new Atom(potion);

        Term[] args = new Term[9];
        args[0] = ingredientMap.get(ing1);
        args[1] = ingredientMap.get(ing2);
        for (int i = 0; i < 3; i++)
        {
            args[2 + i] = ingredientComponentsMap.get(ing1)[i];
        }
        for (int j = 0; j < 3; j++)
        {
            args[5 + j] = ingredientComponentsMap.get(ing2)[j];
        }
        args[8] = potionAtom;

        return new Compound("testing", args);
    }

    private static Expression retractFact(String ingredient)
    {
        Term term = new Compound("alchemical", ingredientComponentsMap.get(ingredient));
        return new Expression(new Term[]{term}, Operator.RETRACT);
    }

    private static Expression assertFact(String ingredient)
    {
        Term[] args = new Term[4];
        args[0] = ingredientMap.get(ingredient);
        Term[] vars = ingredientComponentsMap.get(ingredient);
        args[1] = vars[0];
        args[2] = vars[1];
        args[3] = vars[2];
        Term term = new Compound("seenAlchemical", args);
        return new Expression(new Term[]{term}, Operator.ASSERT);
    }

    private static void printComponents(Evaluator ev, String ingredient)
    {
        String printString = "For ingredient " + ingredient + " :- ";
        for (int i = 0; i < 3; i++)
        {
            printString += ev.getVariableValue(ingredientComponentsMap.get(ingredient)[i]).getName() + "(" + ingredientComponentsMap.get(ingredient)[i].getName() + ")" + ", ";
        }
        System.out.println(printString);
    }
}

