package graphstream;

import org.apache.commons.lang3.StringUtils;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import terms.Term;

public class LogicGraphManager {
    private Graph graph;
    private Node currentNode;
    private int edgeCounter = 0;
    private boolean enable = true;

    private int x = 10;
    private int y = 10;

    /**
     * Remember to use DepthFirstSearchIterator to trace solution path or incorrect path
     */
    public LogicGraphManager() {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph = new SingleGraph("LogicGraphManager");

        graph.addAttribute("ui.stylesheet", "url('file://" + System.getProperty("user.dir") + "/input/GraphStreamStyleSheet.css')");
        graph.addAttribute("ui.antialias");
    }

    public void addQueryNode(Term[] goalTerms) {
        String nodeLabel = "?- " + StringUtils.join(goalTerms, ",");
        addNode(nodeLabel);
    }

    public void addUnifyingNode(Term term1, Term term2) {
        String nodeLabel = term1.toString() + "==" + term2.toString();
        addNode(nodeLabel);
    }

    public void addNode(String nodeLabel)
    {
        if(enable)
        {
            Node n = graph.addNode(nodeLabel);
            n.addAttribute("ui.label", nodeLabel);
            n.setAttribute("xy", x, y);
            x += 10;
            y -= 10;
            n.addAttribute("layout.frozen");

            if (currentNode != null) {
                edgeCounter++;
                graph.addEdge(Integer.toString(edgeCounter), currentNode, n);
            }
            currentNode = n;

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void display() {
        if (enable) {
            Viewer viewer = graph.display(false);
            /*HierarchicalLayout h1 = new HierarchicalLayout();
            viewer.enableAutoLayout(h1);*/
        }
    }
}
