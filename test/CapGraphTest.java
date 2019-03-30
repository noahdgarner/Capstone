import graph.CapGraph;
import graph.Graph;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static junit.framework.TestCase.fail;
import static util.GraphLoader.loadGraph;

class CapGraphTest {

    //generate graph
    Graph myGraph = new CapGraph();
    //load with data to test


    @Test
    void exportGraph() {
        //lets create a class first working
        loadGraph(myGraph, "data/facebook_1000.txt");
        HashMap<Integer, HashSet<Integer>> exportedGraph = myGraph.exportGraph();
        System.out.println("\nKey " + exportedGraph.values().toString());

    }

    @Test void testAddVertex() {
        myGraph.addVertex(5);
        myGraph.addVertex(4);
        myGraph.addVertex(3);
        myGraph.addVertex(2);
        myGraph.addVertex(1);
        myGraph.addVertex(0);
        myGraph.addEdge(5,4);
        myGraph.addEdge(4,5);
        myGraph.addEdge(3,4);
        myGraph.addEdge(3,5);

        HashMap<Integer, HashSet<Integer>> exportedG = myGraph.exportGraph();
        exportedG.forEach((key, value) -> {
            System.out.println("val to strig" + value);
        });


    }


    @Test
    public void getEgonet() {

        loadGraph(myGraph, "data/facebook_ucsd.txt");
       // HashMap<Integer, HashSet<Integer>> xported = myGraph.exportGraph();
        CapGraph xp = (CapGraph) myGraph.getEgonet(8);
        //if this prints blank, it means those vertexes have no neighbors
        HashMap<Integer, HashSet<Integer>> res = xp.exportGraph();
        //obviously, 8 will be connected to all
        for(HashSet<Integer> n :  res.values()){
            System.out.println(n);
        }
    }

    @Test
    public void testExceptionMessage() {
        try {
            loadGraph(myGraph, "data/facebook_1000.txt");
            myGraph.addVertex(0);
            fail("Expected an IllegalArgument Exception");
        }
        catch (IllegalArgumentException anIllegalArg) {

        }
    }

    @Test void testNeighborsInEgonet(){
        loadGraph(myGraph, "data/facebook_2000.txt");
        CapGraph grp = (CapGraph) myGraph.getEgonet(0);

    }


    @Test
    void getSCCs6() {
        loadGraph(myGraph, "data/scc/test_6.txt");
        // HashMap<Integer, HashSet<Integer>> xported = myGraph.exportGraph();

        List<Graph> x = myGraph.getSCCs();

        System.out.println(x.get(0).exportGraph());
        System.out.println(x.get(1).exportGraph());

    }

    @Test
    void getSCCs7() {
        loadGraph(myGraph, "data/scc/test_7.txt");
        // HashMap<Integer, HashSet<Integer>> xported = myGraph.exportGraph();

        List<Graph> x = myGraph.getSCCs();

        System.out.println(x.get(0).exportGraph());
        System.out.println(x.get(1).exportGraph());
        System.out.println(x.get(2).exportGraph());
    }


    @Test
    void swapEdges() {
        //working
        loadGraph(myGraph, "data/scc/test_2.txt");

        CapGraph capGraph = (CapGraph) myGraph;

        System.out.println(capGraph.getEdges());
        capGraph.swapEdges();
        System.out.println("\n\n\n\n\n");
        System.out.println(capGraph.getEdges());

    }
}









