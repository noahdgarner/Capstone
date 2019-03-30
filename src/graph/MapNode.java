package graph;

import java.util.HashSet;

//represents a vertice, a name, but in number form for ease of use
public class MapNode {

    //a node in the map has a value, and a set of edges coming out of it
    int val;//remember, names = numbers, this number represents a name
    boolean covered;
    HashSet<MapEdge> nodeEdgeSet;

    //construct our MapNode
    public MapNode(int val) {
        this.val = val;
        this.covered = false;
        this.nodeEdgeSet = new HashSet<>();
    }

    //this could potentially confuse the shit out of users. Total anti-pattern, refactor later haha
    public void setCovered() {
        this.covered = !this.covered;
    }

    public int getVal(){
        return val;
    }

    //what if we want to add an edge? 'a friendship'
    public void addEdge(MapEdge edge){
        nodeEdgeSet.add(edge);
    }


    //what if we want to get all the friends of this node? val = human name!?
    public HashSet<MapNode> getNodeFriends(){
        HashSet<MapNode> friends = new HashSet<>();
        for (MapEdge mapEdge : nodeEdgeSet) {
            //this is the current node
            friends.add(mapEdge.getOtherNode(this));
        }
        return friends;
    }

    //so that we can use this in our export graph function, yes really bad way of polymorphism, is this dynamic or compile?
    public HashSet<Integer> getIntegerFriends(){
        HashSet<Integer> friends = new HashSet<>();
        for (MapEdge mapEdge : nodeEdgeSet) {
            //this is the current node
            friends.add(mapEdge.getOtherNode(this).getVal());
        }
        return friends;
    }




    @Override
    public String toString() {
        return "value: "+val;
    }

    //this is the function we needed to get our algorithm to work. Only works with twitter data,
    //facebook data everything is strongly connected because its an undirected graph
    public boolean hasEdgeTo(MapNode neighbor) {
        for (MapEdge mapEdge : this.nodeEdgeSet) {
            if (mapEdge.endNode == neighbor) {
                return true;
            }
        }
        return  false;
    }
}

