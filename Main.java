package com.company;

import java.util.Scanner;

import static com.company.Node.IConstants.RED;

public class Main {
    public static void main(String[] args) {
	// write your code here
        Red_Black_Tree tree = new Red_Black_Tree();
        System .out.println("The first you test is red-black-tree:\n");

        tree.initiate();
        System.out.println("First, insert into trees the data in the file 1_initial.txt as follows, " +
                "\nShow pre_order below:");
        tree.preorder_tree_walk(tree.getRoot(),0,0);


        System.out.println("\nEnter the word you want to delete" +
                "\n（input \"file_delete\" means delete the data in the file 2_delete.txt）:");
        Scanner input1 = new Scanner(System.in);
        String word_delete = input1.nextLine();
        if(word_delete.equals("file_delete")){
            tree.deleteFile();
        }
        else {
            Node del = tree.search(word_delete,false);
            if(del != null) {
                tree.delete(del);
            }
            else {
                System.out.println("There isn't the word to delete! fuck!");
            }
        }


        System.out.println("Enter the word you want to insert:" +
                "\n(\"word  meaning\" or \"file_insert\"):");
        String word_insert = input1.next();
        if(word_insert.equals("file_insert")){
            tree.insertFile();
        }
        else {
            String now_Explain = input1.next();
            tree.insert(new Node(word_insert,RED,now_Explain));
        }

        System.out.println("Enter the word you want to query:");
        String word_query = input1.next();
        tree.search(word_query,true);

        System.out.println("Enter the range you want to query:");
        String gabbage = input1.next();
        String begin = input1.next();
        gabbage = input1.next();
        String end = input1.next();
        if(end.compareTo(begin) != 0) {
            tree.printOnRoad(begin, end);
        }
//        System.out.println("Enter the word you want to delete:");
//        Scanner input2 = new Scanner(System.in);
//        String word_delete = input2.nextLine();



        System.out.println("Next, we test B+ tree:\n\nEnter a number as order:");
        B_Add_Tree.main(null);
    }
}
