package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class DBTreeNode {

    private String data;
    ArrayList<DBTreeNode> next = new ArrayList<>();
    DBTreeNode parent;
    int IDCount;

    DBTreeNode(String data) {
        this.data = data;
    }

    DBTreeNode addChild(String childData){
        DBTreeNode child = new DBTreeNode(childData);
        child.parent = this;
        this.next.add(child);
        return child;
    }

    public boolean isChild(String childData){
        for(DBTreeNode child : next){
            if(child.data.equals(childData)){
                return true;
            }
        }
        return false;
    }

    public DBTreeNode getParent(DBTreeNode child){
        return child.parent;
    }

    public int getChildNumber(DBTreeNode parent, DBTreeNode child){
        for(int i = 0 ; i < next.size() ; i++) {
            if (parent.next.get(i) == child) {
                return parent.next.indexOf(child);
            }
        }
        return 0;
    }

    public String getChildData(int index){
        return next.get(index).getData();
    }

    public List<DBTreeNode> getNext(){
        return next;
    }

    public String getData(){
        return this.data;
    }

    public int getAndIncrementID(){
        return ++IDCount;
    }

    public void setIDCount(){
        this.IDCount = 0;
    }

    public void deleteAllChildren(DBTreeNode root){
        if(root == null){
            return;
        }
        for(int i = 0 ; i < root.next.size() ; i++) {
            deleteAllChildren(root.next.get(i));
        }
        parent.next.remove(root);
    }
    





}
