import model.Edge;
import model.Node;
import model.Processor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class slackCalculator {

    public static void main(String[] args) {
        fileReader.read(new File("src/data/out.gxl").getAbsoluteFile());
        calculateSlack(fileReader.getNodes(), fileReader.getEdges(), fileReader.getProcessors());
    }

    public static void calculateSlack(ArrayList<Node> nodes, ArrayList<Edge> edges, ArrayList<Processor> processors) {
        int slack = 0;
        for (Node node : nodes) {
            Processor processor = processors.get(node.getProcessor());
            HashMap<Node, Integer> predecessors = node.getPredecessors();
            int startTime = node.getStartTime();
            int minTimeFromPredecessor = Integer.MAX_VALUE;
            int gapInProcessor = 0;
            for (Map.Entry<Node, Integer> predecessor : predecessors.entrySet()) {
                if(predecessor.getKey().getProcessor() != node.getProcessor()){
                    int gap = node.getStartTime() - predecessor.getKey().getEndTime() - predecessor.getValue();
                    if (gap < minTimeFromPredecessor) {
                        minTimeFromPredecessor = gap;
                    }
                }
            }
            if (minTimeFromPredecessor != 0) {
                ArrayList<Node> scheduledNodes = processor.getNodesScheduled();
                int positionOfNode = scheduledNodes.indexOf(node);
                if (positionOfNode > 0){
                    gapInProcessor = startTime - scheduledNodes.get(positionOfNode - 1).getEndTime();
                }
                if (minTimeFromPredecessor > gapInProcessor) slack += gapInProcessor;
                else slack += minTimeFromPredecessor;
            }
        }
        System.out.println(slack);
    }
}
