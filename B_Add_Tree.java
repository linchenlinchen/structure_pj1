package com.company;

/**
 * B+树的定义：
 *
 * 1.任意非叶子结点最多有M个子节点；且M>2；M为B+树的阶数
 * 2.除根结点以外的非叶子结点至少有 (M+1)/2个子节点；
 * 3.根结点至少有2个子节点；
 * 4.除根节点外每个结点存放至少（M-1）/2和至多M-1个关键字；（至少1个关键字）
 * 5.非叶子结点的子树指针比关键字多1个；
 * 6.非叶子节点的所有key按升序存放，假设节点的关键字分别为K[0], K[1] … K[M-2],
 *  指向子女的指针分别为P[0], P[1]…P[M-1]。则有：
 *  P[0] < K[0] <= P[1] < K[1] …..< K[M-2] <= P[M-1]
 * 7.所有叶子结点位于同一层；
 * 8.为所有叶子结点增加一个链指针；
 * 9.所有关键字都在叶子结点出现
 */
/**
 * @author LinChen 2018/11/13
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static com.company.Node.IConstants.RED;

public class B_Add_Tree <K extends Comparable<K>, V>{
    //B+树的高度
    protected int height = 0;
    //根节点
    protected B_Add_Node<K, V> root;
    // 阶数，M值 相当于2t
    protected int order;
    //删除的节点数量
    protected int numDel = 0;
    //插入的节点数量
    protected int numInsert = 0;
    //总节点数//
    protected int numOfNode = 0;
    // 叶子节点的链表头
    protected B_Add_Node<K, V> head;
    //传入M值
    public B_Add_Tree(int order) {
        if (order < 3) {
            System.out.print("order must be greater than 2");
            System.exit(0);
        }
        this.order = order;
        root = new B_Add_Node<K, V>(true, true);
        head = root;
    }



    // ——————————————————————————————main方法，输入阶数，创建B+树并初始化
    public static void main(String[] args) {
        System.out.println("Enter your order:");
        Scanner input = new Scanner(System.in);
        String orderStr = input.nextLine();
        int order = 3;
        try {
            order = Integer.parseInt(orderStr);
        }catch (Exception e){
            System.out.println("Error order! so we assume order = 3!");
        }

        B_Add_Tree<String,String> tree = new B_Add_Tree<String, String>(order);
        tree.initiate(tree);
        tree.pre_order_print(tree.getRoot(),0);

        System.out.println("Enter value you want to delete" +
                "(input \"file_delete\" or \"word\"):");
        String del = input.nextLine();
        if(del.equals("file_delete")){
            tree.deleteFile();
        }
        else if(tree.search(del) == null){
            System.out.println("There isn't the word to delete!");
        }
        else {
            tree.delete(del);
        }

        System.out.println("Enter key you want to insert or update" +
                "(input \"file_insert\" or \"key\"): ");
        String key = input.nextLine();
        if(key.equals("file_insert")){
            tree.insertFile();
        }
        else {
            System.out.println("Enter value you want to insert or update");
            String value = input.nextLine();
            tree.insertOrUpdate(key, value);
        }

        System.out.println("Enter a key you want to query:");
        String search = input.nextLine();
        System.out.println(tree.search(search));
        System.out.println("Enter keys you want to query(from wordA to wordB):");
        String gabbage = input.next();
        String begin = input.next();
        gabbage = input.next();
        String end = input.next();
        B_Add_Node temp = tree.getRoot();
        System.out.println("There are the word between "+ begin + " and " + end );
        while (temp.children != null){
            temp = (B_Add_Node) temp.children.get(0);
        }
        for (int i = 0; i < order; i++) {
            String[] tempKey = new String[1];
            tempKey[0] = "";
            try {
               tempKey = temp.keys.get(i).toString().split("=");
            }catch (Exception e){
                tempKey = new String[1];
                tempKey[0] = "";
            }

            if(i < temp.keys.size() && (tempKey[0].toLowerCase().compareTo(begin) >= 0) && tempKey[0].toLowerCase().compareTo(end) <= 0){
                System.out.println(temp.keys.get(i).toString());
            }
            else if(i >= temp.keys.size() || i == order - 1){
                if(temp.getNext() != null) {
                    temp = temp.getNext();
                }
                else{
                    break;
                }
                i = -1;
            }
        }

    }

    //—————————————————————————————————————初始化的过程，读取文件
    private void initiate(B_Add_Tree tree){
        File file = new File("/Users/L2595/IdeaProjects/Red_Black_Tree/src/com/company/1_initial.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GB18030");
            BufferedReader bufferedReader = new BufferedReader(isr);
            //读出第一行没用的
            String now_Read = bufferedReader.readLine();
            while ((now_Read = bufferedReader.readLine()) != null){
                String key = now_Read;
                String now_Explain = bufferedReader.readLine();
                Node node = new Node(key,RED,now_Explain);
                tree.insertOrUpdate(key,now_Explain);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //————————————————————————————————————————插入或者更新
    public void insertOrUpdate(K key, V value) {
        numInsert++;
        numOfNode++;
        if(numInsert % 100 == 0 && numOfNode <= 500){
            System.out.println("You have successfully inserted several hundred keys !");
        }
        System.out.println("successfully inserted ********************** " + key +" "+ value);
        root.insertOrUpdate(key, value, this);
    }

    protected void insertFile(){
        File file = new File("/Users/L2595/IdeaProjects/Red_Black_Tree/src/com/company/3_insert.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GB18030");
            BufferedReader bufferedReader = new BufferedReader(isr);
            //读出第一行没用的
            String now_Read = bufferedReader.readLine();
            while ((now_Read = bufferedReader.readLine()) != null){
                String key = now_Read;
                String now_Explain = bufferedReader.readLine();
                insertOrUpdate((K)key,(V)now_Explain);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //——————————————————————————————————————删除键值是key的节点
    public V delete(K key) {

        if(numDel % 100 == 0 && numOfNode <= 500){
            System.out.println("You have successfully deleted several hundred keys !");
        }
        System.out.println("successful delete  ********************* " + key);
        V v = root.remove(key, this);
        if( v != null) {
            numOfNode--;
            numDel++;
        }
        return v;
    }

    //
    public void deleteFile(){
        File file = new File("/Users/L2595/IdeaProjects/Red_Black_Tree/src/com/company/2_delete.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GB18030");
            BufferedReader bufferedReader = new BufferedReader(isr);
            //读出第一行没用的
            String now_Read = bufferedReader.readLine();
            while ((now_Read = bufferedReader.readLine()) != null){
                String key = now_Read;
                delete((K)key);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Successful delete file!");
    }

    //——————————————————————————————————————搜索键值为key的节点
    public V search(K key) {
        return root.get(key);
    }

    //————————————————————————————————————————递归先序遍历！
    public void pre_order_print(B_Add_Node rootNode,int level){
        B_Add_Node<String,String> node = rootNode;
        if (node.children != null) {
            for (int i = 0; i < node.children.size(); i++) {
                System.out.print("level=" + level);
                System.out.println(" child=" + i + "  " + node.children.get(i).keys);
            }
        }
        level++;
        if(rootNode.children != null) {
            for (int i = 0; i < rootNode.children.size(); i++) {
                pre_order_print((B_Add_Node) rootNode.children.get(i), level);
            }
        }
    }





    //————————————————————————————————————————下面是简单函数
    public void setHead(B_Add_Node<K, V> head) {
        this.head = head;
    }

    public B_Add_Node<K, V> getRoot() {
        return root;
    }

    public void setRoot(B_Add_Node<K, V> root) {
        this.root = root;
    }

    public int getOrder() {
        return order;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }
}

