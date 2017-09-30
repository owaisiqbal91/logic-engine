package tests;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphStreamTest {
    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        Graph graph = new SingleGraph("Tutorial 1");

        graph.addAttribute("ui.stylesheet", "url('file://" + System.getProperty("user.dir") + "/input/GraphStreamStyleSheet.css')");
        graph.addAttribute("ui.antialias");

        Node n = graph.addNode("A");
        n.addAttribute("ui.label", "The quick brown fox jumped over the lazy dog");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");
        graph.addNode("D");

        graph.getNode("B").setAttribute("ui.class", "solution");
        graph.getNode("C").setAttribute("ui.class", "incorrect");

        graph.display();
    }
}
