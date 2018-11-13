package com.company;

import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.Stack;

import static com.company.Node.IConstants.BLACK;
import static com.company.Node.IConstants.RED;

public class Red_Black_Tree {
    //1 represent red; 0 represent black
    private int numOfNode = 0;
    private Node root = new Node();
    private Node nil = new Node();
    private int numDel = 0;
    private int numInsert = 0;

    public Red_Black_Tree(){
        root.setParent(nil);
    }
    public Red_Black_Tree(Node root){
        root.setParent(nil);
        this.root = root;
    }

    public Node getRoot(){
        return root;
    }
    //initiate the red-and-black-tree
    public void initiate(){
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
                insert(node);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //————————————————————————————————————————————————————————————插入函数
    public void insert(Node new_Node){
        //标记节点，就是沿着树目前走到的节点flag_node，初始时是一个null节点，表示root节点的父节点
        Node flag_Node = nil;
        Node root = this.getRoot();
        //一旦红黑树存在根节点，就将flag_node设置为根节点，并且判断新节点的字母序在当前标记节点的左边还是右边
        // 由此更新接下来要遍历的子树的根节点（每次循环后，flag_node的更新总是比root的慢一次，这个对于最终悬挂节点是有好处的）
        //这里还是要判断键值的，因为root和nil在初始时也是两个对象
        while (root.getKey() != null){
            flag_Node = root;
            String temp = new_Node.getKey().toLowerCase();
            if(temp.compareTo(root.getKey()) < 0 ){
                root = root.getLeftChild();
            }
            //重复就覆盖
            else if(new_Node.getKey().compareTo(root.getKey()) == 0){
                root.setValue(new_Node.getValue());
                return;
            }
            else {
                root = root.getRightChild();
            }
        }
        //循环结束表明已经找到了合适的悬挂处，于是将新加的节点挂在父母上。
        //值得注意的是，这个时候有可能出现这棵树是在一个节点都没有的时候，这个时候flag_node就会出现空值，此时直接把新节点设置为root
        new_Node.setParent(flag_Node);
        String temp = new_Node.getKey().toLowerCase();
        if(flag_Node == nil){
            //加入的节点就是根节点
            new_Node.setLevel(0);
            this.root = new_Node;
        }
        //如果初始不是空，并且新的节点在标记节点的左侧则设置为左儿子
        else if (temp.compareTo(flag_Node.getKey()) < 0){
            flag_Node.setLeftChild(new_Node);
        }
        //如果初始不是空，并且新的节点在标记节点的右侧则设置为右儿子
        else {
            flag_Node.setRightChild(new_Node);
        }
        //设置新加入的子节点的子节点全为空节点，自己的颜色设置为红色，然后修复
        //设置两个null对象的目的是把它们区分开来，一遍打印level的时候不同的null节点有不同的level
        new_Node.setLeftChild(nil);
        new_Node.setRightChild(nil);
        new_Node.setColor(RED);
        insert_fixup(new_Node);
        numInsert++;
        numOfNode++;
        System.out.println("Successful insert or update: " + new_Node.getKey() +"  " +  new_Node.getValue());
        if(numInsert % 100 == 0 && numOfNode <= 500){
            System.out.println("we have inserted several hundred node, There are the nodes now:\n");
            preorder_tree_walk(this.getRoot(),0,0);
        }
    }

    //
    public void insertFile(){
        File file = new File("/Users/L2595/IdeaProjects/Red_Black_Tree/src/com/company/3_insert.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file),"GB18030");
            BufferedReader bufferedReader = new BufferedReader(isr);
            //读出第一行没用的
            String now_Read = bufferedReader.readLine();
            while ((now_Read = bufferedReader.readLine()) != null){
                String key = now_Read;
                String now_Explain = bufferedReader.readLine();
                Node node = new Node(key,RED,now_Explain);
                insert(node);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //————————————————————————————————————————————————————————————搜索一个节点
    public Node search(String word,boolean isPrint){
        long begin = System.nanoTime();
            Node flag = this.root;
            while (!flag.getKey().equals(word)) {
                if (word.toLowerCase().compareTo(flag.getKey()) < 0) {
                    if (flag.getLeftChild() == nil) {
                        System.out.println("There isn't the word you want to search!");
                        return null;
                    }
                    flag = flag.getLeftChild();
                } else {
                    if (flag.getRightChild() == nil) {
                        System.out.println("There isn't the word you want to search!");
                        return null;
                    }
                    flag = flag.getRightChild();
                }
            }
            long end = System.nanoTime();
            long between = end - begin;
            if(isPrint)
            System.out.println("You search word: " + word + " means: " + flag.getValue()  + " use "+ between + "ns");
            return flag;
    }

    //————————————————————————————————————————————————————————从一个节点出发打印到另一个节点
    public void printOnRoad(String begin,String end){
        long beginTime = System.nanoTime();
        if(end.toLowerCase().compareTo(begin.toLowerCase()) < 0){
            System.out.println("Input error! fuck!");
            return;
        }
        else if(end.toLowerCase().compareTo(begin.toLowerCase()) == 0){
            search(begin,true);
        }
        Node flag = search(begin,true);
        Node next = successor(flag);

        if(next != null) {
            while (!next.getKey().equals(end)) {
                System.out.println("key=" + next.getKey() + " means: " + next.getValue());
                next = successor(next);
            }
            System.out.println("key=" + next.getKey() + " means: " + next.getValue());
        }
        long endTime = System.nanoTime();
        System.out.println("You totally use "+ (endTime-beginTime) + " ns");
    }

    public void delete(Node delNode){
        Node flag = delNode;
        String flag_original_color = flag.getColor();
        Node x = null;
        if(delNode.getLeftChild() == nil){
            x = delNode.getRightChild();
            transplant(delNode,delNode.getRightChild());
        }
        else if(delNode.getRightChild() == nil){
            x = delNode.getLeftChild();
            transplant(delNode,delNode.getLeftChild());
        }
        else {
            flag = minmun(delNode.getRightChild());
            flag_original_color = flag.getColor();
            x = flag.getRightChild();
            if(flag.getParent() == delNode){
                x.setParent(flag);
            }
            else {
                transplant(flag,flag.getRightChild());
                flag.setRightChild(delNode.getRightChild());
                flag.getRightChild().setParent(flag);
            }
            transplant(delNode,flag);
            flag.setLeftChild(delNode.getLeftChild());
            flag.getLeftChild().setParent(flag);
            flag.setColor(delNode.getColor());
            if(flag_original_color.equals(BLACK)){
                delete_fixup(x);
            }
        }
        System.out.println("The word you want to delete has been deleted!");
        numOfNode--;
        numDel++;
        //满足文档要求打印
        //Each time you operate on 100 pieces of data, please call your PREORDER_PRINT method (in 2.1.2) to
        // print the tree on the console if the total data size in tree is not larger than 500.
        if(numDel % 100 == 0 && numOfNode <= 500){
            System.out.println("we have deleted several hundred node, There are the nodes left: ");
            preorder_tree_walk(root,0,0);
        }
    }

    public void deleteFile(){
        File file = new File("/Users/L2595/IdeaProjects/Red_Black_Tree/src/com/company/2_delete.txt");
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(isr);
            //读出第一行没用的
            String now_Read = bufferedReader.readLine();
            while ((now_Read = bufferedReader.readLine()) != null){
                String key = now_Read;
                if(search(key,false) != null) {
                    delete(search(key, false));
                    System.out.println("Successful delete: ****************************" + key);
                }
                else {
                    System.out.println("Error delete! There isn't the word to delete! ___" + key);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void delete_fixup(Node x){
        while (x != nil && x.getParent() != nil && x.getColor().equals(BLACK)){
            if(x == x.getParent().getLeftChild()){
                Node w = x.getParent().getRightChild();
                if(w.getColor().equals(RED)){
                    w.setColor(BLACK);
                    x.getParent().setColor(RED);
                    left_rotate(x.getParent());
                    w = x.getParent().getRightChild();
                }
                if(w.getLeftChild().getColor().equals(BLACK) && w.getRightChild().getColor().equals(BLACK)){
                    w.setColor(RED);
                    x = x.getParent();
                }
                else {
                    if(w.getRightChild().getColor().equals(BLACK)){
                        w.getLeftChild().setColor(BLACK);
                        w.setColor(RED);
                        right_rotate(w);
                        w = x.getParent().getRightChild();
                    }
                    w.setColor(x.getParent().getColor());
                    x.getParent().setColor(BLACK);
                    w.getRightChild().setColor(BLACK);
                    left_rotate(x.getParent());
                    x = this.root;
                }
            }
            else {
                Node w = x.getParent().getLeftChild();
                if(w.getColor().equals(RED)){
                    w.setColor(BLACK);
                    x.getParent().setColor(RED);
                    right_rotate(x.getParent());
                    w = x.getParent().getLeftChild();
                }
                if(w.getRightChild().getColor().equals(BLACK) && w.getLeftChild().getColor().equals(BLACK)){
                    w.setColor(RED);
                    x = x.getParent();
                }
                else {
                    if(w.getLeftChild().getColor().equals(BLACK)){
                        w.getRightChild().setColor(BLACK);
                        w.setColor(RED);
                        left_rotate(w);
                        w = x.getParent().getLeftChild();
                    }
                    w.setColor(x.getParent().getColor());
                    x.getParent().setColor(BLACK);
                    w.getLeftChild().setColor(BLACK);
                    right_rotate(x.getParent());
                    x = this.root;
                }
            }
        }
        x.setColor(BLACK);
    }

    public void transplant(Node u,Node v){
        if(u.getParent() == nil){
            this.root = v;
        }
        else if(u == u.getParent().getLeftChild()){
            u.getParent().setLeftChild(v);
        }
        else {
            u.getParent().setRightChild(v);
        }
        v.setParent(u.getParent());
    }

    //先序遍历函数 ,容易栈溢出，还是不要用比较好
    public void preorder_tree_walk(Node rootNode,int level,int child){
        if(rootNode!=null){
            if(rootNode != nil && rootNode.getParent() != nil && rootNode == rootNode.getParent().getLeftChild()) {
                System.out.println("level=" + level + " child=0" + "; key=" + rootNode.getKey() + "; value=" + rootNode.getValue() + "(" + rootNode.getColor() + ")");
            }
            else {
                System.out.println("level=" + level + " child=1" + "; key=" + rootNode.getKey() + "; value=" + rootNode.getValue() + "(" + rootNode.getColor() + ")");
            }
            level++;
            preorder_tree_walk(rootNode.getLeftChild(),level,0);
            preorder_tree_walk(rootNode.getRightChild(),level,1);
        }
    }

    //——————————————————————————————————————————————————————————插入操作的修复
    public void insert_fixup(Node new_Node){
        //判断得到新插入的节点的父亲的颜色也是红色的时候，再判断新节点的父亲是新节点爷爷的左儿子还是右儿子
        //①如果是左儿子，那么首先获取爷爷的右儿子作为标记节点（也就是新节点的伯伯）
        //a.如果伯伯是红色，那么爸爸和伯伯变成合适，爷爷变成红色，接着改变需要修复的节点为爷爷节点
        //b.如果伯伯是黑色，并且新节点是爸爸的右儿子，改变新节点做表为爸爸，以爸爸为中心左旋预旋转。->使他进入c情况
        // 这样爸爸与儿子的身份互换了！！于是，自然是对新的爸爸（原先是儿子）的节点与它的现在的爸爸互换颜色再进行右旋了！
        //c.如果伯伯是黑色，并且新节点是爸爸的左儿子，只需要直接对自己的爸爸换色后右旋即可
        //②如果是右儿子，同理
        while (new_Node.getParent().getColor().equals(RED)){
            if(new_Node.getParent() == new_Node.getParent().getParent().getLeftChild()){
                Node flag_Node = new_Node.getParent().getParent().getRightChild();
                if(flag_Node.getColor().equals(RED)){
                    new_Node.getParent().setColor(BLACK);
                    flag_Node.setColor(BLACK);
                    new_Node.getParent().getParent().setColor(RED);
                    new_Node = new_Node.getParent().getParent();
                }
                else {
                    if (new_Node == new_Node.getParent().getRightChild()) {
                        new_Node = new_Node.getParent();
                        left_rotate(new_Node);

                    }
                    new_Node.getParent().setColor(BLACK);
                    //可能出现对于root节点找爷爷的情况，这样会出错，所以加一个判断
                    if(new_Node.getParent().getParent().getKey() != null) {
                        new_Node.getParent().getParent().setColor(RED);
                        right_rotate(new_Node.getParent().getParent());
                    }
                }

            }
            else {
                Node flag_Node = new_Node.getParent().getParent().getLeftChild();
                if(flag_Node.getColor().equals(RED)){
                    new_Node.getParent().setColor(BLACK);
                    flag_Node.setColor(BLACK);
                    new_Node.getParent().getParent().setColor(RED);
                    new_Node = new_Node.getParent().getParent();
                }
                else{
                    if (new_Node == new_Node.getParent().getLeftChild()){
                        new_Node = new_Node.getParent();
                        right_rotate(new_Node);
                    }
                    new_Node.getParent().setColor(BLACK);
                    if(new_Node.getParent().getParent().getKey() != null) {
                        new_Node.getParent().getParent().setColor(RED);
                        left_rotate(new_Node.getParent().getParent());
                    }
                }

            }
        }
        //最后，设置根节点为黑
        this.root.setColor(BLACK);
    }

    //——————————————————————————————————————————————————————————————左旋函数
    public void left_rotate(Node node){
        Node flag_Node = node.getRightChild();
        node.setRightChild(flag_Node.getLeftChild()) ;
        if(flag_Node.getLeftChild()!= null && flag_Node.getLeftChild().getKey() != null){
            flag_Node.getLeftChild().setParent(node);
        }
        flag_Node.setParent(node.getParent());
        if(node.getParent() == nil){
            this.root = flag_Node;
        }
        else if(node == node.getParent().getLeftChild()){
            node.getParent().setLeftChild(flag_Node);
        }
        else {
            node.getParent().setRightChild(flag_Node);
        }
        //如果flag是nil，那么nil就有了子节点，回到了root，这是会造成死循环的
        if(flag_Node != nil) {
            flag_Node.setLeftChild(node);
            node.setParent(flag_Node);
        }

        //最后如果左旋之后被放下来的node没有子节点，那么要给他nil子节点
        if(node.getLeftChild()==null){
            node.setLeftChild(nil);
        }
        if(node.getRightChild() == null){
            node.setRightChild(nil);
        }
    }

    //——————————————————————————————————————————————————————————右旋函数
    public void right_rotate(Node node){
        Node flag_Node = node.getLeftChild();
        node.setLeftChild(flag_Node.getRightChild()) ;
        if(flag_Node.getRightChild() != null && flag_Node.getRightChild().getKey() != null){
            flag_Node.getRightChild().setParent(node);
        }
        flag_Node.setParent(node.getParent());
        if(node.getParent() == nil){
            this.root = flag_Node;
        }
        else if(node == node.getParent().getRightChild()){
            node.getParent().setRightChild(flag_Node);
        }
        else {
            node.getParent().setLeftChild(flag_Node);
        }
        //如果flag是nil，那么nil就有了子节点，回到了root，这是会造成死循环的
        if(flag_Node != nil) {
            flag_Node.setRightChild(node);
            node.setParent(flag_Node);
        }
        //最后如果右旋之后被放下来的node没有子节点，那么要给他nil子节点
        if(node.getLeftChild()==null){
            node.setLeftChild(nil);
        }
        if(node.getRightChild() == null){
            node.setRightChild(nil);
        }
    }

    public Node minmun(Node node){
        while (node.getLeftChild() != nil){
            node = node.getLeftChild();
        }
        return node;
    }

    public Node maxmun(Node node){
        while (node.getRightChild() != nil){
            node = node.getRightChild();
        }
        return node;
    }

    public Node successor(Node nodex){
        if(nodex != null && nodex.getRightChild()!=nil){
            return minmun(nodex.getRightChild());
        }
        Node nodey = null;
        if(nodex != null && nodex.getParent()!=null) {
            nodey = nodex.getParent();
            while (nodey != null && nodex == nodey.getRightChild()) {
                nodex = nodey;
                nodey = nodey.getParent();
            }
        }
        return nodey;
    }

    public Node predecessor(Node nodex){
        if(nodex != null && nodex.getLeftChild()!= nil){
            return maxmun(nodex.getLeftChild());
        }
        Node nodey = nodex.getParent();
        while (nodey != null && nodex == nodey.getLeftChild()){
            nodex = nodey;
            nodey = nodey.getParent();
        }
        return nodey;
    }
}

//    public void preorder_use_loop(Node node){
//        Node p;
//        Stack<Node> stack = new Stack<Node>();
//        stack.push(node);
//        node.setLevel(0);
//        int level = 0;
//        while (!stack.empty()) {
//            p = stack.pop();
//            if (p.getParent() != nil) {
//                p.setLevel(p.getParent().getLevel() + 1);
//            }
//            System.out.print("level=" + p.getLevel() + " ");
//            if (p == p.getParent().getLeftChild()) {
//                System.out.print("child=0");
//            } else {
//                System.out.print("child=1");
//            }
//            System.out.println(" " + p.getKey() + " " + p.getValue() + "(" + p.getColor() + ")");
//            if (p.getRightChild() != null) {
//                stack.push(p.getRightChild());
//            }
//            if (p.getLeftChild() != null) {
//                stack.push(p.getLeftChild());
//            }
//        }
//    }
//
//    public void postorder_tree_walk(Node rootNode){
//            if(rootNode!=null){
//                postorder_tree_walk(rootNode.getLeftChild());
//                postorder_tree_walk(rootNode.getRightChild());
//                System.out.print(rootNode.getKey() + " ");
//            }
//    }
