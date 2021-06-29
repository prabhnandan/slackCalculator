import model.Node;
import model.Processor;
import model.Slack;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ScheduleGraph extends JFrame {
    public final int STRING_OFFSET = 3;
    public final int Y_OFFSET = 25;
    public final int X_OFFSET = 20;

    int scheduleLength;
    ArrayList<Node> nodes;
    int numProcessors;
    int width;
    int height;
    ArrayList<Slack> allSlackTimes = slackCalculator.getAllSlackTimes();

    public ScheduleGraph() {
        numProcessors = fileReader.getNumProcessors();
        scheduleLength = fileReader.getScheduleLength();
        this.nodes = fileReader.getNodes();
        width = (100 * numProcessors) + X_OFFSET;
        height = (12 * scheduleLength) + Y_OFFSET;
        add(new NodeComponents());

        setSize(new Dimension(width, height));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    class NodeComponents extends JPanel {
        public void paintComponent(Graphics graph) {
            int xUnit = (width - X_OFFSET) / (numProcessors);
            int yUnit = (height - Y_OFFSET) / scheduleLength;
            graph.setColor(Color.DARK_GRAY);
            graph.fillRect(X_OFFSET, Y_OFFSET, width - X_OFFSET, height - Y_OFFSET);
            for (Processor processor : fileReader.getProcessors()) {
                graph.drawString("P" + processor.getID(), xUnit * processor.getID() + xUnit / 2 + X_OFFSET, yUnit + STRING_OFFSET);
                for (Node node : processor.getNodesScheduled()) {
                    int x = xUnit * node.getProcessor() + X_OFFSET;
                    int y = yUnit * node.getStartTime() + Y_OFFSET;
                    graph.setColor(Color.RED);
                    graph.fillRect(x, y, xUnit, node.getWeight() * yUnit);
                    graph.setColor(Color.BLACK);
                    graph.drawString(node.getId(), (x + (xUnit / 2)), y + (node.getWeight() * yUnit / 2) + STRING_OFFSET);
                    graph.drawRect(x, y, xUnit, node.getWeight() * yUnit);
                }
            }
            for (Slack slack : allSlackTimes) {
                graph.setColor(Color.GREEN);
                int x = xUnit * slack.getProcessor() + X_OFFSET;
                int y = yUnit * slack.getStartTime() + Y_OFFSET;
                int weight = slack.getEndTime() - slack.getStartTime();
                graph.fillRect(x, y, xUnit, weight * yUnit);
                graph.setColor(Color.BLACK);
                graph.drawString(slack.getFromNode(), (x + (xUnit / 2)), y + (weight * yUnit) / 2 + STRING_OFFSET);
                graph.drawRect(x, y, xUnit, weight * yUnit);
            }

            yUnit = height / (scheduleLength);

            for (int i = 0; i < scheduleLength + 1; i++) {
                int y = yUnit * i + Y_OFFSET;
                graph.drawString(i + " -", 0, y + STRING_OFFSET);
            }
        }
    }
}