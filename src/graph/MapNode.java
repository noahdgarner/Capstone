package graph;

import java.util.HashMap;
import java.util.HashSet;

import static graph.BigGraph.p;

//represents a vertice, a name, but in number form for ease of use
public class MapNode {

    //a node in the map has a value, and a set of edges coming out of it
    int val;//remember, names = numbers, this number represents a name

    int numNeighbors;
    boolean covered; //have we visited this node yet for min dom set?
    HashSet<MapEdge> nodeEdgeSet;

    //construct our MapNode
    public MapNode(int val) {
        this.val = val;
        //so we can access #neighbors in O(1) time.
        this.numNeighbors = 0;
        this.covered = false;
        this.nodeEdgeSet = new HashSet<>();
    }

    //this could potentially confuse the shit out of users. Total anti-pattern, refactor later haha
    public void setCovered(boolean covered) {
        this.covered = covered;
    }


    public boolean isCovered() {
        return this.covered;
    }

    public int getVal() {
        return val;
    }

    //what if we want to add an edge? 'a friendship'
    //it is added as a toNode AND a fromNode... i.e. twice each time..
    public void addEdge(MapEdge edge) {
        nodeEdgeSet.add(edge);
        //this way we can access #neighbors in O(1) time instead of O(n)
        this.numNeighbors++;
        //if a node has a two, and a from, then subtract 1?
    }

    //just in case, for the future if we need to remove friendships, etc...
    public void removeEdge(MapEdge edge) {
        nodeEdgeSet.remove(edge);
        this.numNeighbors--;
    }

    //note, we do NOT want a setter for this, the setting is done above..
    public int getNumNeighbors() {
        return numNeighbors;
    }

    //what if we want to get all the friends of this node? val = human name!?
    public HashSet<MapNode> getNodeFriends() {
        HashSet<MapNode> friends = new HashSet<>();
        for (MapEdge mapEdge : nodeEdgeSet) {
            //this is the current node
            friends.add(mapEdge.getOtherNode(this));
        }
        return friends;
    }

    //specifically for the exportGraph Method because it wants integer vals
    public HashSet<Integer> getIntegerFriends() {
        HashSet<Integer> friends = new HashSet<>();
        for (MapEdge mapEdge : nodeEdgeSet) {
            //this is the current node
            friends.add(mapEdge.getOtherNode(this).getVal());
        }
        return friends;
    }

    //check cover function. This needs to be refactored
    public void coverNodeNeighbors(HashMap<Integer, Integer> vertexTable) {
        HashSet<MapNode> neighbors = new HashSet<>();
        //get the neighbors Time O(n)
        for (MapEdge mapEdge : nodeEdgeSet) {
            //this is the current node
            neighbors.add(mapEdge.getOtherNode(this));
        }
        //cover the neighbor. We start here for chadi's improvement. Working Time O(n)
        //create array of neighbors to check
        for (MapNode neighbor : neighbors) {
            //get the key(neighbor) from vertexTable, see if it exists in vertexTable
            if (vertexTable.containsKey(neighbor.getVal())) {
                //make sure it exists
                if (neighbor.covered == false) {
                    //cover it
                    neighbor.setCovered(true);
                    //update vertex table, -2 because of to/from edges per node
                    vertexTable.put(neighbor.getVal(), vertexTable.get(neighbor.getVal()) - 2);
                }
                else
                    //This node has been covered, remove from our indexing table.
                    vertexTable.remove(neighbor.getVal());
            }
            else {
                //its not in the table... therefore it must be covered... lmao idk..
                neighbor.setCovered(true);
            }
        }
        for (MapNode neighbor : neighbors) {
            //get the key(neighbor) from vertexTable, see if it exists in vertexTable
            if (vertexTable.containsKey(neighbor.getVal())) {
                //make sure it exists
                if (neighbor.covered == false) {
                    //cover it
                    neighbor.setCovered(true);
                    //update vertex table
                    vertexTable.put(neighbor.getVal(), vertexTable.get(neighbor.getVal()) - 2);
                }
                else
                    //This node has been covered, remove from our indexing table.
                    vertexTable.remove(neighbor.getVal());
            }
            else {
                //its not in the table... therefore it must be covered... lmao idk..
                neighbor.setCovered(true);
            }
        }

    }

    @Override
    public String toString() {
        return "value: " + val;
    }
    //this is the function we needed to get our algorithm to work. Only works with twitter data,
    //facebook data everything is strongly connected because its an undirected graph
    public boolean hasEdgeTo(MapNode neighbor) {
        for (MapEdge mapEdge : this.nodeEdgeSet) {
            if (mapEdge.endNode == neighbor) {
                return true;
            }
        }
        return false;
    }
}

