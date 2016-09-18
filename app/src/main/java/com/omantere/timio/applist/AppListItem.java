package com.omantere.timio.applist;

/**
 * Created by omantere on 06/09/16.
 */
public class AppListItem {
    String name;
    double usedSeconds;

    public AppListItem(String name, double usedSeconds) {
        this.name = name;
        this.usedSeconds = usedSeconds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUsedSeconds() {
        return usedSeconds;
    }

    public void setUsedSeconds(int usedSeconds) {
        this.usedSeconds = usedSeconds;
    }
}
