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

    void addToList(ArrayList<Integer> forList){
        for (Integer integer : forList) {
            boolean exist = false;
            for (Integer integer2 : assignList) {
                if(integer == integer2) {
                    exist = true;
                    break;
                }
            }
            if(!exist) assignList.add(integer);
        }
    }

    String getType() {
        return type;
    }
}
