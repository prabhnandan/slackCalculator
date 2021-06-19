package model;

public class Slack {

    private int startTime;
    private int endTime;
    private int processor;

    public Slack(int startTime, int endTime, int processor) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.processor = processor;
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
    public int getAmount(){
        return this.endTime - this.startTime;
    }

    public int getProcessor() {
        return processor;
    }
}
