package com.example.ge.gepaintapp;

/**
 * Created by yuanda on 2015/5/11.
 */
public class Story {
    String storyName;
    int storyStatus;

    public Story(String sn, int ss){
        storyName = sn;
        storyStatus = ss;
    }

    public String getStoryName() {
        return storyName;
    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public int getStoryStatus() {
        return storyStatus;
    }

    public void setStoryStatus(int storyStatus) {
        this.storyStatus = storyStatus;
    }
}
