package com.company;

import static com.company.Node.IConstants;
import static com.company.Node.IConstants.RED;
import static com.company.Node.IConstants.BLACK;
public class Node {
    //data
    public interface IConstants{
        String RED = "red";
        String BLACK = "black";
    }
    private String color;
    private String key = null;
    private String value = null;
    private Node parent = null;
    private Node leftChild = null;
    private Node rightChild = null;
    private int level = -1;

    //constructor
    //无传参则认为是null的黑node
    public Node(){
        this.color = BLACK;

    }
    public Node(String key, String color,String value){
        this.key = key;
        this.value = value;
        this.color = color;
    }
    public Node(Node parent, String key, String color,String value){
        this.parent = parent;
        this.key = key;
        this.color = color;
        this.value = value;
    }
    public Node(Node parent, Node leftChild, Node rightChild ,String key, String color,String value){
        this.parent = parent;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.key = key;
        this.value = value;
        this.color = color;
    }

    //method
    public void setParent(Node parent1){
        parent = parent1;
    }
    public Node getParent(){
        return  parent;
    }
    public void setLeftChild(Node leftChild1){
        leftChild = leftChild1;
    }
    public Node getLeftChild(){
        return leftChild;
    }
    public void setRightChild(Node rightChild1){
        rightChild = rightChild1;
    }
    public Node getRightChild(){
        return rightChild;
    }
    public void setKey(String key){
        this.key = key;
    }
    public String getKey(){
        return key;
    }
    public void setColor(String color){
        this.color = color;
    }
    public String getColor(){
        return color;
    }
    public void setValue(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public void setLevel(int level){
        this.level = level;
    }
    public int getLevel(){
        return level;
    }
    public void addLevel(){
        this.setLevel(this.getLevel()+1);
    }
    public void subLevel(){
        this.setLevel(this.getLevel()-1);
    }
}

