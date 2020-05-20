package Optimizer;

import java.util.ArrayList;

class ValueInfo {
    private String type;
    private ArrayList<Integer> assignList;

    ValueInfo(String type, int initLoop) {
        this.type = type;
        assignList = new ArrayList<>();
        if(initLoop != -1) assignList.add(initLoop);
    }

    ArrayList<Integer> getAssignList() {
        return assignList;
    }

    void addToList(int loop){
        for (Integer integer : assignList) {
            if(integer == loop) return;
        }
        assignList.add(loop);
    }

    String getType() {
        return type;
    }
}
