/**
 *
 */

import model.Edge;
import model.Node;
import model.Processor;
import net.sourceforge.gxl.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class fileReader {

    private static ArrayList<Node> nodes = new ArrayList<Node>();
    private static ArrayList<Edge> edges = new ArrayList<Edge>();
    private static ArrayList<Processor> processors = new ArrayList<>();
    private static int numProcessors;

    public static void read(File file) {
        try {

            GXLDocument doc = new GXLDocument(file);
            GXLGraph elements = doc.getDocumentElement().getGraphAt(0);
            numProcessors = ((GXLInt) elements.getChildAt(5).getChildAt(0)).getIntValue();
            for (int i = 0; i < elements.getChildCount(); i++) {
                if (elements.getChildAt(i) instanceof GXLNode) {
                    GXLNode n = (GXLNode) elements.getChildAt(i);
                    String id = n.getAttribute("id");
                    int weight = ((GXLInt) n.getChildAt(0).getChildAt(0)).getIntValue();
                    int startTime = ((GXLInt) n.getChildAt(1).getChildAt(0)).getIntValue();
                    int processor = ((GXLInt) n.getChildAt(2).getChildAt(0)).getIntValue();
                    nodes.add(new Node(id, weight, startTime, processor));
                } else if (elements.getChildAt(i) instanceof GXLEdge) {
                    GXLEdge e = (GXLEdge) elements.getChildAt(i);
                    String from = e.getSourceID();
                    String to = e.getTargetID();
                    int weight = ((GXLInt) e.getChildAt(0).getChildAt(0)).getIntValue();
                    edges.add(new Edge(from, to, weight));
                }
            }
            setPredecessors();
            setupProcessors();
            return;
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Node> getNodes() {
        return nodes;
    }

    public static ArrayList<Edge> getEdges() {
        return edges;
    }

    public static ArrayList<Processor> getProcessors() {
        return processors;
    }

    public static int getNumProcessors() {
        return numProcessors;
    }

    private static void setPredecessors() {
        for (Edge e : edges) {
            for (Node n : nodes) {
                if (e.getEndNode().equals(n.getId())) {
                    for (Node n2 : nodes) {
                        if (e.getStartNode().equals(n2.getId())) n.addPredecessor(n2, e.getWeight());
                    }
                }
                ;
            }
        }
        return;
    }

    private static void setupProcessors() {
        for (int i = 0; i < numProcessors; i++) {
            Processor processor = new Processor(i);
            for (Node n : nodes) {
                if (n.getProcessor() == i) processor.addNode(n);
            }
            processors.add(processor);
        }
    }
}
