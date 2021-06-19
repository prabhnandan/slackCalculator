package model;

import java.util.Comparator;

public class SlackSorter implements Comparator<Slack> {

    @Override
    public int compare(Slack s1, Slack s2) {
        return s1.getStartTime().compareTo(s2.getStartTime());
    }
}
