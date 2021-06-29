package model;

public class Slack {

    private int startTime;
    private int endTime;
    private int processor;
    private String fromNode;

    public Slack(int startTime, int endTime, int processor, String fromNode) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.processor = processor;
        this.fromNode = fromNode;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getFromNode() {
        return fromNode;
    }

    public int getAmount() {
        return this.endTime - this.startTime;
    }

    public int getProcessor() {
        return processor;
    }
}
