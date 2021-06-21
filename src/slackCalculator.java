import model.Node;
import model.Processor;
import model.Slack;
import model.SlackSorter;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class slackCalculator {
    private static ArrayList<Slack> allSlackTimes = new ArrayList<>();

    public static void main(String[] args) {
        Runtime rt = Runtime.getRuntime();
        try {
            Process process = rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f src/data/in.gxl -o src/data/out.gxl -clusterer dcp");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        fileReader.read(new File("src/data/out.gxl").getAbsoluteFile());
        calculateSlack(fileReader.getProcessors());
        calculateSlack2(fileReader.getProcessors());
        calculateTotalSlack(allSlackTimes, fileReader.getNumProcessors());
    }

    public static void calculateSlack(ArrayList<Processor> processors) {
        for (Processor processor : processors) {
            ArrayList<Node> nodesOnProcessor = processor.getNodesScheduled();
            for (Node node : nodesOnProcessor) {
                Slack slack = new Slack(0, node.getStartTime(), node.getProcessor());
                HashMap<Node, Integer> predecessors = node.getPredecessors();
                for (Map.Entry<Node, Integer> predecessor : predecessors.entrySet()) {
                    if (predecessor.getKey().getProcessor() != node.getProcessor()) {
                        int predecessorEndTime = predecessor.getKey().getEndTime() + predecessor.getValue();
                        if (predecessorEndTime > slack.getStartTime()) slack.setStartTime(predecessorEndTime);
                    }
                }
                int positionOfNode = nodesOnProcessor.indexOf(node);
                if (positionOfNode > 0) {
                    if (nodesOnProcessor.get(positionOfNode - 1).getEndTime() > slack.getStartTime())
                        slack.setStartTime(nodesOnProcessor.get(positionOfNode - 1).getEndTime());
                }
                allSlackTimes.add(slack);
            }
        }
    }

    private static void calculateSlack2(ArrayList<Processor> processors) {
        for (Processor processor : processors) {
            ArrayList<Node> nodesOnProcessor = processor.getNodesScheduled();
            for (Node node : nodesOnProcessor) {
                Slack slack = new Slack(node.getEndTime(), fileReader.getScheduleLength(), node.getProcessor());
                HashMap<Node, Integer> successors = node.getSuccessors();
                for (Map.Entry<Node, Integer> successor : successors.entrySet()) {
                    if (successor.getKey().getProcessor() != node.getProcessor()) {
                        int nodeEndTime = node.getEndTime() + successor.getValue();
                        int successorStartTime = successor.getKey().getStartTime();
                        int latestNodeEndTime = successorStartTime - nodeEndTime + node.getEndTime();
                        if (latestNodeEndTime < slack.getEndTime()) slack.setEndTime(latestNodeEndTime);
                    }
                }
                int positionOfNode = nodesOnProcessor.indexOf(node);
                if (positionOfNode < nodesOnProcessor.size() - 1) {
                    if (nodesOnProcessor.get(positionOfNode + 1).getStartTime() < slack.getEndTime())
                        slack.setEndTime(nodesOnProcessor.get(positionOfNode + 1).getStartTime());
                }
                allSlackTimes.add(slack);
            }
        }
    }

    private static void calculateTotalSlack(ArrayList<Slack> allSlackTimes, int numProcessors) {
        int totalSlack = 0;
        int totalUnscheduledTime = 0;

        for(int i = 0; i< numProcessors; i++){
            ArrayList<Slack> slackInProcessor = new ArrayList<>();
            for (Slack slack : allSlackTimes){
                if(slack.getProcessor() == i) slackInProcessor.add(slack);
            }
            slackInProcessor.sort(new SlackSorter());
            for(int slackIndex = 0; slackIndex < slackInProcessor.size() - 1;slackIndex++){
                int slackEndTime = slackInProcessor.get(slackIndex).getEndTime();
                int nextSlackStartTime = slackInProcessor.get(slackIndex+1).getStartTime();
                if(slackEndTime > nextSlackStartTime){
                    slackInProcessor.get(slackIndex).setEndTime(slackInProcessor.get(slackIndex+1).getEndTime());
                    slackInProcessor.remove(slackIndex+1);
                }
            }
            for(int j = 0; j< slackInProcessor.size(); j++){
                Slack slack = slackInProcessor.get(j);
                totalSlack += slack.getAmount();
                if(j == 0 ) totalUnscheduledTime += slack.getStartTime();
                else if(j == slackInProcessor.size() - 1) totalUnscheduledTime += fileReader.getScheduleLength() - slack.getEndTime();
                else totalUnscheduledTime += slackInProcessor.get(j+1).getStartTime() - slack.getEndTime();
            }
        }
        int totalScheduleTime = numProcessors * fileReader.getScheduleLength();
        int idleTime = totalScheduleTime - totalUnscheduledTime;
        System.out.println("Total processor time = "+totalScheduleTime);
        System.out.println("Total slack time = "+ totalSlack+"("+String.format("%.2f",(float)totalSlack*100/totalScheduleTime)+"%)");
        System.out.println("Total idle time = "+ idleTime+"("+String.format("%.2f",(float)idleTime*100/totalScheduleTime)+"%)");
    }

    private void multipleScheduleCalculate(String folderPath) {
        // Get Folder Contents
        File f = new File(folderPath);
        List<String> files = Arrays.asList(Objects.requireNonNull(f.list()));
        // Filter only .gxl files
        List<String> gxlFiles = files.stream().filter(".gxl"::equals).collect(Collectors.toList());
        // Calculate Slack for each file
        for (String gxlFile : gxlFiles) {
            fileReader.read(new File(folderPath + gxlFile).getAbsoluteFile());
            calculateSlack(fileReader.getProcessors());
            calculateSlack2(fileReader.getProcessors());
            calculateTotalSlack(allSlackTimes, fileReader.getNumProcessors());
            allSlackTimes.clear();
        }
    }
}
