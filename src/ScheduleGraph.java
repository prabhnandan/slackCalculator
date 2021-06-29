import model.Node;
import model.Processor;
import model.Slack;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
    ArrayList<Slack> allSlackTimes;

    public ScheduleGraph(ArrayList<Slack> allSlackTimes) {
        this.allSlackTimes = allSlackTimes;
        numProcessors = fileReader.getNumProcessors();
        scheduleLength = fileReader.getScheduleLength();
        this.nodes = fileReader.getNodes();
        width = (100 * numProcessors) + X_OFFSET;
        height = (15 * scheduleLength) + Y_OFFSET + STRING_OFFSET;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        makeScheduleImage(bufferedImage);
        JPanel map = new JPanel();
        JScrollPane scroll = new JScrollPane(map);
        this.getContentPane().add(scroll, BorderLayout.CENTER);
        map.setLayout(new GridLayout());
        bufferedImage.getScaledInstance(width,height,Image.SCALE_SMOOTH);
        JLabel imageLabel =  new JLabel();
        imageLabel.setIcon(new ImageIcon(bufferedImage));
        JScrollPane scrollpane=new JScrollPane(imageLabel);
        map.add(scrollpane);
        add(map);
        setSize(new Dimension(600,800));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    public void makeScheduleImage(BufferedImage image) {
        Graphics graph = image.createGraphics();
        int xUnit = (width - X_OFFSET) / (numProcessors);
        int yUnit = (height - Y_OFFSET) / scheduleLength;
        graph.setColor(Color.WHITE);
        graph.fillRect(0,0, width, height);
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