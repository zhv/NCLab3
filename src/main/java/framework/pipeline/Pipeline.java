package framework.pipeline;

import framework.steps.Step;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Pipeline {

    protected List<Step> steps = new LinkedList<>();

    public void start() {
        for (Step s : steps) {
            s.start();
        }
    }
}
