
package graph;

import java.util.*;

import static util.GraphLoader.loadGraph;

/**
 * @author Noah Garner
 */
public class CapGraph implements Graph {

    //no we want to use a hashmap
    //an integer that maps to a list of integers to mapnodes
    HashMap<Integer, MapNode> myGraph;
    //to track all edges between nodes in our graph
    HashSet<MapEdge> myGraphEdges;

    //construct our graph
    public CapGraph() {
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
    public CapGraph getEgonet(int center) {
        //asked us to return a copy, lets makes a copy.
        //generate subgraph, copies of original graph nodes, return it
        CapGraph egonet = new CapGraph();
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
    /* (non-Javadoc)
     * @see graph.Graph#getSCCs()
     */

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
                DFS(node, visited, finished);
            }
        }
        //100% working
        this.swapEdges();
        //working
        HashSet<Integer> visitedTranspose = new HashSet<>();
        List<Graph> strongComponentList = new ArrayList<>();
        // Now process all vertices in order defined by Stack
        //want to count the calls
        while (!finished.empty()) {
            //create a graph
            CapGraph component = new CapGraph();
            int aNode = finished.pop();
            if(!visitedTranspose.contains(aNode)) {
                //whats wrong here? Its chaining everything to 1
                DFSUtil(aNode, visitedTranspose, component);
                strongComponentList.add(component);
            }
        }
        return strongComponentList;
    }

    public void DFSUtil(int aNode, HashSet<Integer> visitedTranspose, CapGraph component) {
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

    //helper dfs, make sure vertices stack has same nodes as in graph
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


    //swap all edges in the graph, do not change this because this one actually works..
    public void swapEdges() {
        for (MapEdge edge : myGraphEdges) {
            MapNode temp;
            temp = edge.endNode;
            edge.endNode = edge.startNode;
            edge.startNode = temp;
        }
    }
    /* (non-Javadoc)
     * @see graph.Graph#exportGraph()
     * //basically, this method should simply return a node, and all of its neighbors.
     */

    @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        //init return map
        HashMap<Integer, HashSet<Integer>> convertedGraph = new HashMap<>();

        //now we should iterate over our map, and put all its neighbors into the hashmap's hashset vals
        this.myGraph.forEach((key, value) -> {
            convertedGraph.put(key, value.getIntegerFriends());
        });
        return convertedGraph;
    }

    public Set<MapNode> getFriends(MapNode node) {
        return node.getNodeFriends();
    }

    private HashSet<Integer> getIntegerFriends(MapNode node) {
        return node.getIntegerFriends();
    }


    public static void main(String[] args) {


        //lets instantiate our graph class
        CapGraph ourGraph = new CapGraph();
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
        CapGraph twitterData = new CapGraph();
        loadGraph(twitterData, "data/wikivotes");


        CapGraph g = twitterData.getEgonet(4098);
        HashMap<Integer, HashSet<Integer>> res = g.exportGraph();
        System.out.println(res);


        List<Graph> listofG = twitterData.getSCCs();
        for(int i = 0;i<listofG.size();i++) {
            System.out.println(listofG.get(i).exportGraph());
        }

        //obviously, 8 will be connected to all


        /*for(HashSet<Integer> n :  res.values()){
            System.out.println(n);
        }*/
        //print the components for testing purposes




        /*Iterator<Map.Entry<Integer,MapNode>> iter = ourGraph.myGraph.entrySet().iterator();
        while(iter.hasNext()) {
            System.out.println(iter.next().getValue().getNodeFriends());
        }
        */

        //way simpler way to iterate through map elements, i.e. key values with lambdas
        //okay lambdas are amazing for hashmaps, never going back to iterator
        //ourGraph.myGraph.forEach((key, value) -> p("User :" + key + " Friends: " + ourGraph.getFriends(value))); //same as value.getNodeFriends():


    }



    public static void p(String printStuff) {
        System.out.println(printStuff);
    }

    public HashSet<MapEdge> getEdges() {
        return this.myGraphEdges;
    }
}
