package graph;

//represents an edge between 2 nodes
public class MapEdge {

    //has a from and to node
    MapNode startNode;
    MapNode endNode;
    double weight;

    //construct the edge, between nodes
    MapEdge(MapNode start, MapNode end) {
        this.startNode = start;
        this.endNode = end;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }

    //given a node, we want the other node it attaches to
    //because calling on this edge, it has to have the opposite of
    //the node sent into this function
    public MapNode getOtherNode(MapNode node) {
        //if the node is start, return end, and vice a vers a
        if (node.equals(startNode)) {
            return endNode;
        } else if (node.equals(endNode)) {
            return startNode;
        } else
            throw new IllegalArgumentException("Bad node data");
    }


    @Override
    public String toString() {
        return "This edge begins at "+startNode.toString()+" and ends at "+endNode.toString();
    }
}
