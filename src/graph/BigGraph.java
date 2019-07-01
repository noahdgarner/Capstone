
package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static util.GraphLoader.loadGraph;

/**
 * @author Noah Garner
 */
public class BigGraph implements Graph {
    //an integer that maps to a list of integers to mapnodes
    HashMap<Integer, MapNode> graphNodes;
    //to track all edges between nodes in our graph
    HashSet<MapEdge> myGraphEdges;
    //construct our graph
    public BigGraph() {
        this.graphNodes = new HashMap<>();
        this.myGraphEdges = new HashSet<>();
    }

    @Override
    public void addVertex(int num) throws IllegalArgumentException {
        //add a mapNode at this particular name, oh lets do error checking!
        //i.e. what if this guy is already in the map!
        if (!!!graphNodes.containsKey(num)) {
            this.graphNodes.put(num, new MapNode(num));
        } else {
            //System.out.println("num "+num+" was already added.");
        }
    }
    /* (non-Javadoc)
     * @see graph.Graph#addEdge(int, int)
     */
    @Override
    public void addEdge(int from, int to) {
        //we need the node at this value
        //note to remember, each node has a set of edges i.e. friends when contstructed!!
        //what if 4 and 5 aren' in the map? Error check this
        MapNode fromNode = graphNodes.get(from);
        MapNode toNode = graphNodes.get(to);
        if (fromNode == null || toNode == null)
            throw new NullPointerException(toNode.toString() + " is not in the graph");

        MapEdge edge = new MapEdge(fromNode, toNode);
        this.myGraphEdges.add(edge);
        //oh my god i think i just needed to add this
        fromNode.addEdge(edge);
        toNode.addEdge(edge);
    }

    //return all edges in this graph
    public HashSet<MapEdge> getEdges() {
        return this.myGraphEdges;
    }
    //get all the vertices in the graph
    public Stack<Integer> getAllIntegerVertices() {
        Stack<Integer> verticesAsStack = new Stack<>();
        for (MapNode node : graphNodes.values()) {
            verticesAsStack.push(node.val);
        }
        return verticesAsStack;
    }
    /* (non-Javadoc)
     * @see graph.Graph#getEgonet(int)
     */
    @Override
    public BigGraph getEgonet(int center) {
        //asked us to return a copy, lets makes a copy.
        //generate subgraph, copies of original graph nodes, return it
        BigGraph egonet = new BigGraph();
        if (!this.graphNodes.containsKey(center)) {
            return egonet;
        }
        //put center into the egonet
        MapNode centerNode = new MapNode(center);
        egonet.addVertex(center);
        //put all of these nodes neighbors into the map
        for (MapNode x : this.graphNodes.get(center).getNodeFriends()) {
            //create the web between center and friends
            egonet.addVertex(x.val);
            egonet.addEdge(centerNode.val, x.val);
        }
        //check the friend of friends of center to see if it has an edge
        Set<Integer> egoNodes = egonet.graphNodes.keySet();
        for (Integer i : egoNodes) {
            //skip center, we just added centers edges
            if (i == center)
                continue;
            for (MapNode n : this.graphNodes.get(i).getNodeFriends()) {
                //does our set contain anything related to this element
                if (egoNodes.contains(n.val)) {
                    egonet.addEdge(i, n.val);
                }
            }
        }
        return egonet;
    }
    //SCC: maximal, and SCC.
    @Override
    public List<Graph> getSCCs() {
        //stack done
        Stack<Integer> finished = new Stack<>();
        //ones we've seen
        HashSet<Integer> visited = new HashSet<>();
        //all graph vertices
        Stack<Integer> vertices = getAllIntegerVertices();
        //for all vertices, working?
        for (int node : vertices) {
            if (!visited.contains(node)) {
                //part 1
                DFS(node, visited, finished);
            }//
        }
        //100% working
        this.swapEdges();
        //working
        HashSet<Integer> visitedTranspose = new HashSet<>();
        List<Graph> strongComponentList = new ArrayList<>();
        // Now process all vertices in order defined by Stack in previous for loop
        while (!finished.empty()) {
            //create a graph
            BigGraph component = new BigGraph();
            int aNode = finished.pop();
            if (!visitedTranspose.contains(aNode)) {
                //part 2
                DFSUtil(aNode, visitedTranspose, component);
                strongComponentList.add(component);
            }
        }
        return strongComponentList;
    }
    //part 1, build the stack
    public void DFS(int aNode, HashSet<Integer> visited, Stack<Integer> finished) {
        visited.add(aNode);
        //for each neighbor of the node
        for (int neighbor : graphNodes.get(aNode).getIntegerFriends()) {
            //this is the most important line of code in the entire algorithm and is what was causing so many issues
            //if the neighbor has not been visited, and the neighbor is an ENDNODE! (because we swapped the nodes) i.e. a = aNode, b = neighbor then a <- b
            if (!visited.contains(neighbor) && graphNodes.get(aNode).hasEdgeTo(graphNodes.get(neighbor))) {
                //call DFS again, and also, edit go into hasEdgeTo
                DFS(neighbor, visited, finished);
            }
        }
        finished.push(aNode);
    }
    //part 2, build the component (implement comparable for nodeval)
    public void DFSUtil(int aNode, HashSet<Integer> visitedTranspose, BigGraph component) {
        visitedTranspose.add(aNode); //add 5, working
        component.addVertex(aNode); //add the vertex (has a set of edges)
        //this is where something is going wrong
        for (Integer neighbor : graphNodes.get(aNode).getIntegerFriends()) {
            //this is the most important line of code in the entire algorithm and is what was causing so many issues
            //if the neighbor has not been visited, and the neighbor is an ENDNODE! (because we swapped the nodes) i.e. a = aNode, b = neighbor then a <- b
            if (!visitedTranspose.contains(neighbor) && graphNodes.get(aNode).hasEdgeTo(graphNodes.get(neighbor))) {
                DFSUtil(neighbor, visitedTranspose, component);
            }
        }
    }
    //swap all edges in the graph, for SCC method.
    public void swapEdges() {
        for (MapEdge edge : myGraphEdges) {
            MapNode temp;
            temp = edge.endNode;
            edge.endNode = edge.startNode;
            edge.startNode = temp;
        }
    }
    //start of the minDomSet Algorithm lets do this (skeleton)
    public static Set<Integer> minDomSetGreedy(BigGraph graph){
        //a dominating set
        Set<Integer> aDomSet = new HashSet<>();
        //uncover all vertices to start. working: O(n)
        uncoverVertices(graph);
        //to track each node's in a table
        HashMap<Integer, Integer> vertexTable = new HashMap<>();
        //create look up table <Node,#neighbors>: O(n)
        for (MapNode aNode : graph.graphNodes.values()) {
            vertexTable.put(aNode.getVal(), aNode.getNumNeighbors());
        }
        //init algorithm, we will break when all are marked
        boolean hasUncovered = true;
        while (hasUncovered) {
            //create max <K,V> pair in table init null: O(1) space, working
            Map.Entry<Integer, Integer> maxEntry = null;
            //find the max entry in the table O(n), working
            for (Map.Entry<Integer, Integer> entry : vertexTable.entrySet()) {
                if (maxEntry == null || entry.getValue() > maxEntry.getValue())
                    maxEntry = entry;
            }
            //get the graph vertex at maxEntry: O(1)
            int currMaxVertex = maxEntry.getKey();
            //remove max entry from the table: O(1), working
            p("CurrMaxVertex: " + maxEntry.getKey() + "\n" + "CurrMaxValue: " + maxEntry.getValue());
            //remove this from the vertexTable, don't need for ref anymore
            vertexTable.remove(currMaxVertex);
            //cover the currMaxVertex: O(1)..., working!!!
            graph.graphNodes.get(currMaxVertex).setCovered(true);
            //cover curMaxVertexs neighbors + update vertexTable!!!O(n)... working!!
            graph.graphNodes.get(currMaxVertex).coverNodeNeighbors(vertexTable);
            //add the currMaxEntry to the minDomSet
            aDomSet.add(currMaxVertex);
            //Do we have more covered nodes? Assume all are covered Time: O(n)
            hasUncovered = false;
            //check if any are uncovered
            for (MapNode aNode : graph.graphNodes.values()) {
                //if we find one that is not covered... we have to run algorithm again
                if (!aNode.isCovered()) {
                    hasUncovered = true;
                    break;
                }
            }
        }
        //print the updated HashTable of vertex and their neighbor#s
        p("VertexTable State After update: \n"+vertexTable.keySet().toString());
        //
        p("Curr minDomSet (KEYS from Vertex Table): " + aDomSet.toString());
        //printnewline for new runthrough
        p("");
        return aDomSet;
    }

    public static Set<Integer> minDomSetHillClimbing(BigGraph graph) {
        //cover all vertices in graph
        coverVertices(graph);
        //set containing visited vertices
        Set<Integer> visited = new HashSet<>();
        //create a vertexTable  with all nodes and # of neighbors
        HashMap<Integer, Integer> vertexTable = new HashMap<>();
        //working, convert graph to vertex table easier to work with
        for(MapNode aNode : graph.graphNodes.values()) {
            vertexTable.put(aNode.getVal(), aNode.getNumNeighbors());
        }
        //so for now, the minDomSet is the keys of vertexTable.
        //for each node
        boolean keepGoing = true;
        while (keepGoing) {
            //find the most uninfluential node in the vertexTable
            Map.Entry<Integer, Integer> nodeWithLeastNeighbors = new AbstractMap.SimpleEntry<>(1000000,1000000);
            for (Map.Entry<Integer, Integer> entry : vertexTable.entrySet()) {
                //is it the lowest, and have we not visited it yet
                if (entry.getValue() < nodeWithLeastNeighbors.getValue()
                && !visited.contains(entry.getKey()))
                    nodeWithLeastNeighbors = entry;
            }
            int currMinVertex = nodeWithLeastNeighbors.getKey();
            //find node 4, with 1 neighbor, remember, even numbers (6 means 3 neighbors etc)
            //check to see if it has any covered neighbors
            //for now, remove the node from the solutionSet
            vertexTable.remove(currMinVertex);
            //okay this is how we do this part nice..
            int leastInfluentialVertex = currMinVertex;
            //set it to uncovered in the graph
            graph.graphNodes.get(leastInfluentialVertex).setCovered(false);
            //check to see we still have a minDomSet by checking if any of its neighbors covered
            boolean hasCoveredNeighbor = false;
            //to start, this is 4
            HashSet<MapNode> nodeNeighbors = graph.graphNodes.get(leastInfluentialVertex).getNodeFriends();
            for(MapNode aNode : nodeNeighbors) {
                if (aNode.isCovered()) {
                    hasCoveredNeighbor = true;
                    //we can leave early since we found a cover
                    break;
                }
            }
            //if we found a coveredNeighbor, we must check any uncovered neighbors
            //to make sure they themselves have a covered neighbor
            //Else, we cannot uncover this neighbor, do the following 3 ops
            if (hasCoveredNeighbor) {
                //for each neighbor
                for (MapNode aNode : nodeNeighbors) {
                    //if the neighbor is un-covered, check if it has atleast 1 neighbor that's covered
                    if (!aNode.isCovered()) {
                        boolean atleast1CoveredNeighbor = false;
                        for (MapNode friendOfFriend : aNode.getNodeFriends()) {
                            if (friendOfFriend.isCovered()) {
                                atleast1CoveredNeighbor = true;
                                break;
                            }
                        }
                        if (!atleast1CoveredNeighbor) {
                            //we must cover the node back up
                            graph.graphNodes.get(leastInfluentialVertex).setCovered(true);
                            //and put it back into our vertexTable
                            vertexTable.put(nodeWithLeastNeighbors.getKey(), nodeWithLeastNeighbors.getValue());
                            break;
                        }
                        //check the rest of the uncovered neighbors
                        else {
                            continue;
                        }
                    }
                    //keep checking for uncovered neighbors
                    else {
                        continue;
                    }
                }
            }
            //it has no covered neighbors...
            else {
                //we must cover the node back up
                graph.graphNodes.get(leastInfluentialVertex).setCovered(true);
                //and put it back into our vertexTable
                vertexTable.put(nodeWithLeastNeighbors.getKey(), nodeWithLeastNeighbors.getValue());
            }
            //regardless, we must say we visited the node.
            visited.add(graph.graphNodes.get(leastInfluentialVertex).getVal());
            //if we visited all the nodes, we are done with the algorithm
            if (visited.size() == graph.graphNodes.size()){
                break;
            }
        }
        p("VertexTable State After update (KEYS): \n"+vertexTable.keySet().toString());
        return vertexTable.keySet();
    }

    //helper to init "uncover" vertices
    public static void uncoverVertices(BigGraph graph) {
        for(MapNode currNode : graph.graphNodes.values()) {
            currNode.setCovered(false);
        }
    }
    //helper to init "cover" vertices
    public static void coverVertices(BigGraph graph) {
        for(MapNode currNode : graph.graphNodes.values()) {
            currNode.setCovered(true);
        }
    }

    //for testing purposes
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        //init return map
        HashMap<Integer, HashSet<Integer>> convertedGraph = new HashMap<>();
        //now we should iterate over our map, and put all its neighbors into the hashmap's hashset vals
        this.graphNodes.forEach((key, value) -> convertedGraph.put(key, value.getIntegerFriends()));
        return convertedGraph;
    }
    //program insertion point
    public static void main(String[] args) throws Exception{
/*
        String fileToLoad = "data/facebook_ucsd.txt";
*/
        String fileToLoad = "data/octogram.txt";
        //instantiate graph object
        BigGraph fileGraph = new BigGraph();

        loadGraph(fileGraph, fileToLoad);
        p("\n\n*****testing minDomFunction stuff*****\n\n");
       long startTime = System.nanoTime();
        p("Timer Start");
        minDomSetGreedy(fileGraph);
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        p("Elapsed Time in ms: "+elapsedTime/1000000);
        p("\n\n*****testing Hill Climbing Function*****\n\n");
         startTime = System.nanoTime();
        p("Timer Start");
        minDomSetHillClimbing(fileGraph);
         endTime = System.nanoTime();
         elapsedTime = endTime - startTime;
        p("Elapsed Time in ms: "+elapsedTime/1000000);
        /*BufferedReader br = new BufferedReader(new FileReader("data/data"));
            String line = null;
            int count = 0;
            while((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (String str : values){
                    count++;
                    System.out.println(str);
                }
            }
            p("Number in the minDomSet: "+count);
            br.close();
        */


/*      egonet test working.
        BigGraph egonet = fileGraph.getEgonet(22);

        egonet.graphNodes.forEach((k,v) ->
                p(k+" "+v));
        minDomSetGReedy(egonet);*/
        // 4333 nodes to cover a 15000 node graph. hmmm, i guess thats ok,seems bad

    }

    //quick scc print
    public static void sccPrinter(String fileName, boolean getEgo, int egoVal)   {
        //instantiate graph object
        BigGraph graphDataFromFile = new BigGraph();
        //test SCC, note we cannot run this on entire data, so we run on an egonet
        loadGraph(graphDataFromFile, fileName);
        //create components, weird, it was necessary to split the declaration
        List<Graph> graphSCCs;
        if(getEgo == true) {
            //so we can look at a specific area of interest in our graph
            graphSCCs = graphDataFromFile.getEgonet(egoVal).getSCCs();
        }
        else {
            graphSCCs = graphDataFromFile.getSCCs();
        }
        //a list of our components for output
        List<Set<Integer>> sccs = new ArrayList<>();
        // get student SCC result
        for (Graph aGraph : graphSCCs) {
            HashMap<Integer, HashSet<Integer>> curr = aGraph.exportGraph();
            //fancy set is all
            Set<Integer> scc = new HashSet<>();
            for (Map.Entry<Integer, HashSet<Integer>> entry : curr.entrySet()) {
                scc.add(entry.getKey());
            }
            sccs.add(scc);
        }
        for (Set<Integer> anSCC : sccs) {
            p(anSCC.toString());
        }
    }
    //quick print
    public static void p(String printStuff) {
        System.out.println(printStuff);
    }
}
