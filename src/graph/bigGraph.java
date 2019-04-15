
package graph;

import java.util.*;

import static util.GraphLoader.loadGraph;

/**
 * @author Noah Garner
 */
public class bigGraph implements Graph {

    //no we want to use a hashmap
    //an integer that maps to a list of integers to mapnodes
    HashMap<Integer, MapNode> myGraph;
    //to track all edges between nodes in our graph
    HashSet<MapEdge> myGraphEdges;

    //construct our graph
    public bigGraph() {
        this.myGraph = new HashMap<>();
        this.myGraphEdges = new HashSet<>();
    }

    /* (non-Javadoc)
     * @see graph.Graph#addVertex(int)
     */
    @Override
    public void addVertex(int num) throws IllegalArgumentException {
        //add a mapNode at this particular name, oh lets do error checking!
        //i.e. what if this guy is already in the map!
        if (!!!myGraph.containsKey(num)) {
            this.myGraph.put(num, new MapNode(num));
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
        MapNode fromNode = myGraph.get(from);
        MapNode toNode = myGraph.get(to);
        if (fromNode == null || toNode == null)
            throw new NullPointerException(toNode.toString() + " is not in the graph");

        MapEdge edge = new MapEdge(fromNode, toNode);
        this.myGraphEdges.add(edge);
        //oh my god i think i just needed to add this
        fromNode.addEdge(edge);
        toNode.addEdge(edge);
    }

    //get all the vertices in the graph
    public Stack<Integer> getAllIntegerVertices() {
        Stack<Integer> verticesAsStack = new Stack<>();
        for (MapNode node : myGraph.values()) {
            verticesAsStack.push(node.val);
        }
        return verticesAsStack;
    }

    /* (non-Javadoc)
     * @see graph.Graph#getEgonet(int)
     */

    @Override
    public bigGraph getEgonet(int center) {
        //asked us to return a copy, lets makes a copy.
        //generate subgraph, copies of original graph nodes, return it
        bigGraph egonet = new bigGraph();
        if (!this.myGraph.containsKey(center)) {
            return egonet;
        }
        //put center into the egonet
        MapNode centerNode = new MapNode(center);
        egonet.addVertex(center);
        //put all of these nodes neighbors into the map
        for (MapNode x : this.myGraph.get(center).getNodeFriends()) {
            //create the web between center and friends
            egonet.addVertex(x.val);
            egonet.addEdge(centerNode.val, x.val);
        }
        //check the friend of friends of center to see if it has an edge

        Set<Integer> egoNodes = egonet.myGraph.keySet();

        for (Integer i : egoNodes) {
            //skip center, we just added centers edges
            if(i == center)
                continue;
            for (MapNode n: this.myGraph.get(i).getNodeFriends()) {
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
            }
        }
        //100% working
        this.swapEdges();
        //working
        HashSet<Integer> visitedTranspose = new HashSet<>();
        List<Graph> strongComponentList = new ArrayList<>();
        // Now process all vertices in order defined by Stack in previous for loop
        while (!finished.empty()) {
            //create a graph
            bigGraph component = new bigGraph();
            int aNode = finished.pop();
            if(!visitedTranspose.contains(aNode)) {
                //part 2
                DFSUtil(aNode, visitedTranspose, component);
                strongComponentList.add(component);
            }
        }
        return strongComponentList;
    }
    //part 1 of algorithm
    public void DFSUtil(int aNode, HashSet<Integer> visitedTranspose, bigGraph component) {
        visitedTranspose.add(aNode); //add 5, working
        component.addVertex(aNode); //add the vertex (has a set of edges)
        //this is where something is going wrong
        for (int neighbor : myGraph.get(aNode).getIntegerFriends()) {
            //this is the most important line of code in the entire algorithm and is what was causing so many issues
            //if the neighbor has not been visited, and the neighbor is an ENDNODE! (because we swapped the nodes) i.e. a = aNode, b = neighbor then a <- b
            if (!visitedTranspose.contains(neighbor) && myGraph.get(aNode).hasEdgeTo(myGraph.get(neighbor))) {
                    DFSUtil(neighbor, visitedTranspose, component);
                }
        }
    }
    //helper dfs, make sure vertices stack has same nodes as in graph. Part 2 of algorithm
    public void DFS(int aNode, HashSet<Integer> visited, Stack<Integer> finished) {
        visited.add(aNode);
        //for each neighbor of the node
        for (int neighbor : myGraph.get(aNode).getIntegerFriends()) {
            //this is the most important line of code in the entire algorithm and is what was causing so many issues
            //if the neighbor has not been visited, and the neighbor is an ENDNODE! (because we swapped the nodes) i.e. a = aNode, b = neighbor then a <- b
            if (!visited.contains(neighbor) && myGraph.get(aNode).hasEdgeTo(myGraph.get(neighbor))) {
                //call DFS again, and also, edit go into hasEdgeTo
                DFS(neighbor, visited, finished);
            }
        }
        finished.push(aNode);
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


    @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        //init return map
        HashMap<Integer, HashSet<Integer>> convertedGraph = new HashMap<>();
        //now we should iterate over our map, and put all its neighbors into the hashmap's hashset vals
        this.myGraph.forEach((key, value) -> convertedGraph.put(key, value.getIntegerFriends()));
        return convertedGraph;
    }


    public static void main(String[] args) {


        //lets instantiate our graph class
        bigGraph ourGraph = new bigGraph();
        //we create a vertex 4, converts to MapNode, with blank edgeset
        ourGraph.addVertex(0);
        ourGraph.addVertex(1);
        ourGraph.addVertex(2);
        ourGraph.addVertex(3);
        ourGraph.addVertex(4);

        ourGraph.addEdge(1, 0);
        ourGraph.addEdge(0, 2);
        ourGraph.addEdge(2, 1);
        ourGraph.addEdge(0, 3);
        ourGraph.addEdge(3, 4);

        Stack<MapNode> vertices = new Stack<>();

        MapNode aNode1 = new MapNode(1);
        MapNode aNode2 = new MapNode(2);
        MapNode aNode3 = new MapNode(3);
        MapNode aNode4 = new MapNode(4);
        MapNode aNode5 = new MapNode(5);
        vertices.push(aNode1);
        vertices.push(aNode2);
        vertices.push(aNode3);
        vertices.push(aNode4);
        vertices.push(aNode5);


        //test on twitter data
        bigGraph wikiData = new bigGraph();
        //uncomment the loader you want to load.
        //loadGraph(wikiData, "data/twitter_combined.txt");
        loadGraph(wikiData, "data/twitter_higgs.txt");
        //loadGraph(wikiData, "data/wikivotes");


        bigGraph g = wikiData.getEgonet(88);
        //remember: res is a HashMap where Key=node, Value=setOfDirectNeighbors
        HashMap<Integer, HashSet<Integer>> res = g.exportGraph();
        for(HashSet<Integer> set : res.values()){

            p(set.toString());
        }

    }



    public static void p(String printStuff) {
        System.out.println(printStuff);
    }

    public HashSet<MapEdge> getEdges() {
        return this.myGraphEdges;
    }
}
