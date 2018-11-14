package com.company;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class B_Add_Node <K extends Comparable<K>, V> {

    /** 是否为叶子节点 */
    private boolean isLeaf;
    /** 是否为根节点*/
    private boolean isRoot;
    /** 父节点 */
    private B_Add_Node<K, V> parent;
    /** 叶节点的前节点*/
    private B_Add_Node<K, V> previous;
    /** 叶节点的后节点*/
    private B_Add_Node<K, V> next;
    /** 节点的关键字*/
    protected List<Entry<K, V>> keys;
    /** 子节点 */
    protected List<B_Add_Node<K, V>> children;
    private B_Add_Node(boolean isLeaf) {
        this.isLeaf = isLeaf;
        keys = new ArrayList<Entry<K, V>>();

        if (!isLeaf) {
            children = new ArrayList<B_Add_Node<K, V>>();
        }
    }
    B_Add_Node(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }



    //根据key获取value
    public V get(K key) {
        long begin = System.nanoTime();
        //如果是叶子节点
        if (isLeaf) {
            int low = 0, high = keys.size() - 1, mid;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;
                comp = keys.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return keys.get(mid).getValue();
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            //未找到所要查询的对象
            return null;
        }
        //如果不是叶子节点
        //如果key小于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(keys.get(0).getKey()) < 0) {
            return children.get(0).get(key);
            //如果key大于等于节点最右边的key，沿最后一个子节点继续搜索
        }else if (key.compareTo(keys.get(keys.size()-1).getKey()) >= 0) {
            return children.get(children.size()-1).get(key);
            //否则沿比key大的前一个子节点继续搜索
        }else {
            int low = 0, high = keys.size() - 1, mid= 0;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;
                comp = keys.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return children.get(mid+1).get(key);
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            long end = System.nanoTime();
            System.out.println("We use "+(end - begin) + "ns for search !");
            return children.get(low).get(key);
        }
    }

    //插入或者更新
    public void insertOrUpdate(K key, V value, B_Add_Tree<K,V> tree){
        //如果是叶子节点
        if (isLeaf){
            //还未达到上限，不需要分裂，直接插入或更新
            if (contains(key) != -1 || keys.size() < tree.getOrder()){
                //调用另一个同名方法直接插入或更新即终止
                insertOrUpdate(key, value);
                if(tree.getHeight() == 0){
                    tree.setHeight(1);
                }
                return ;
            }
            //已经达到上限，需要分裂
            //分裂成左右两个节点
            B_Add_Node<K,V> left = new B_Add_Node<K,V>(true);
            B_Add_Node<K,V> right = new B_Add_Node<K,V>(true);
            //设置链接 ，如果本叶子节点不是最左边那个，把自己前面那个的next指向自己
            if (previous != null){
                previous.next = left;
                left.previous = previous ;
            }
            //如果本叶子节点不是最右边那个，把自己前面那个的previous指向自己
            if (next != null) {
                next.previous = right;
                right.next = next;
            }
            //如果前面没有叶子了
            if (previous == null){
                tree.setHead(left);
            }
            //更新链接
            left.next = right;
            right.previous = left;
            previous = null;
            next = null;

            //复制原节点关键字到分裂出来的新节点
            copy2Nodes(key, value, left, right, tree);

            //如果不是根节点
            if (parent != null) {
                //调整父子节点关系 ，删除旧的叶子节点，将新的左右节点与父亲建立联系
                int index = parent.children.indexOf(this);
                parent.children.remove(this);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(index,left);
                parent.children.add(index + 1, right);
                //将right的第一个key复制给父亲，（这样，左边就是＜，右边就是>=）
                parent.keys.add(index,right.keys.get(0));
                keys = null; //删除当前节点的关键字信息
                children = null; //删除当前节点的孩子节点引用

                //父节点插入或更新关键字
                parent.updateInsert(tree);
                parent = null; //删除当前节点的父节点引用
                //如果是根节点
            }
            else {
                isRoot = false;
                B_Add_Node<K,V> parent = new B_Add_Node<K,V> (false, true);
                tree.setRoot(parent);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(left);
                parent.children.add(right);
                parent.keys.add(right.keys.get(0));
                keys = null;
                children = null;
            }
            return ;

        }
        //如果不是叶子节点
        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(keys.get(0).getKey()) < 0) {
            children.get(0).insertOrUpdate(key, value, tree);
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        }else if (key.compareTo(keys.get(keys.size()-1).getKey()) >= 0) {
            children.get(children.size()-1).insertOrUpdate(key, value, tree);
            //否则沿比key大的前一个子节点继续搜索
        }else {
            int low = 0, high = keys.size() - 1, mid= 0;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;
                comp = keys.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    children.get(mid+1).insertOrUpdate(key, value, tree);
                    break;
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            if(low>high){
                children.get(low).insertOrUpdate(key, value, tree);
            }
        }
    }

    private void copy2Nodes(K key, V value, B_Add_Node<K,V> left,
                            B_Add_Node<K,V> right,B_Add_Tree<K,V> tree) {
        //左右两个节点关键字长度                     考虑order为偶数的时候
        int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
        boolean b = false;//用于记录新元素是否已经被插入
        for (int i = 0; i < keys.size(); i++) {
            //保证目前遍历的key被放入了新建的left叶子上
            if(leftSize !=0){
                leftSize --;
                //如果能够找到比它大的，就创建一个simpleentry，将这个心key加入到左边叶子上，并且将i复位，继续遍历原来旧的叶子节点的元素并复制直至结束
                if(!b&&keys.get(i).getKey().compareTo(key) > 0){
                    left.keys.add(new SimpleEntry<K, V>(key, value));
                    b = true;
                    i--;
                }
                //如果没有找到比它大的，先把原本节点中比它小的复制过来
                else {
                    left.keys.add(keys.get(i));
                }
            }
            else {
                if(!b&&keys.get(i).getKey().compareTo(key) > 0){
                    right.keys.add(new SimpleEntry<K, V>(key, value));
                    b = true;
                    i--;
                }else {
                    right.keys.add(keys.get(i));
                }
            }
        }
        if(!b){
            right.keys.add(new SimpleEntry<K, V>(key, value));
        }
    }

    //插入到当前节点的关键字中
    private void insertOrUpdate(K key, V value){
        //利用二分法
        int low = 0, high = keys.size() - 1, mid;
        int comp ;
        while (low <= high) {
            mid = (low + high) / 2;
            comp = keys.get(mid).getKey().compareTo(key);
            //更新
            if (comp == 0) {
                keys.get(mid).setValue(value);
                break;
            } else if (comp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        //原先不存在，你们在low和low的右侧插入新的key_Value
        if(low>high){
            keys.add(low, new SimpleEntry<K, V>(key, value));
        }
    }

    //插入节点后中间节点的更新
    private void updateInsert(B_Add_Tree<K, V> tree){

        //如果子节点数超出阶数，则需要分裂该节点
        if (children.size() > tree.getOrder()) {
            //分裂成左右两个节点
            B_Add_Node<K, V> left = new B_Add_Node<K, V>(false);
            B_Add_Node<K, V> right = new B_Add_Node<K, V>(false);
            //左右两个节点子节点的长度
            int leftSize = (tree.getOrder() + 1) / 2 + (tree.getOrder() + 1) % 2;
            int rightSize = (tree.getOrder() + 1) / 2;
            //复制子节点到分裂出来的新节点，并更新关键字
            for (int i = 0; i < leftSize; i++){
                left.children.add(children.get(i));
                children.get(i).parent = left;
            }
            for (int i = 0; i < rightSize; i++){
                right.children.add(children.get(leftSize + i));
                children.get(leftSize + i).parent = right;
            }
            for (int i = 0; i < leftSize - 1; i++) {
                left.keys.add(keys.get(i));
            }
            for (int i = 0; i < rightSize - 1; i++) {
                right.keys.add(keys.get(leftSize + i));
            }

            //如果不是根节点
            if (parent != null) {
                //调整父子节点关系
                int index = parent.children.indexOf(this);
                parent.children.remove(this);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(index,left);
                parent.children.add(index + 1, right);
                parent.keys.add(index,keys.get(leftSize - 1));
                keys = null;
                children = null;

                //父节点更新关键字
                parent.updateInsert(tree);
                parent = null;
                //如果是根节点
            }else {
                isRoot = false;
                B_Add_Node<K, V> parent = new B_Add_Node<K, V>(false, true);
                tree.setRoot(parent);
                tree.setHeight(tree.getHeight() + 1);
                left.parent = parent;
                right.parent = parent;
                parent.children.add(left);
                parent.children.add(right);
                parent.keys.add(keys.get(leftSize - 1));
                keys = null;
                children = null;
            }
        }
    }


    //删除节点后中间节点的更新
    private void updateRemove(B_Add_Tree<K, V> tree) {

        // 如果子节点数小于M / 2或者小于2，则需要合并节点
        if (children.size() < tree.getOrder() / 2 || children.size() < 2) {
            if (isRoot) {
                // 如果是根节点并且子节点数大于等于2，OK
                if (children.size() >= 2) return;
                // 否则与子节点合并
                B_Add_Node<K, V> root = children.get(0);
                tree.setRoot(root);
                tree.setHeight(tree.getHeight() - 1);
                root.parent = null;
                root.isRoot = true;
                keys = null;
                children = null;
                return ;
            }
            //计算前后节点
            int currIdx = parent.children.indexOf(this);
            int prevIdx = currIdx - 1;
            int nextIdx = currIdx + 1;
            B_Add_Node<K, V> previous = null, next = null;
            if (prevIdx >= 0) {
                previous = parent.children.get(prevIdx);
            }
            if (nextIdx < parent.children.size()) {
                next = parent.children.get(nextIdx);
            }

            // 如果前节点子节点数大于M / 2并且大于2，则从其处借补
            if (previous != null
                    && previous.children.size() > tree.getOrder() / 2
                    && previous.children.size() > 2) {
                //前叶子节点末尾节点添加到首位
                int idx = previous.children.size() - 1;
                B_Add_Node<K, V> borrow = previous.children.get(idx);
                previous.children.remove(idx);
                borrow.parent = this;
                children.add(0, borrow);
                int preIndex = parent.children.indexOf(previous);

                keys.add(0,parent.keys.get(preIndex));
                parent.keys.set(preIndex, previous.keys.remove(idx - 1));
                return ;
            }

            // 如果后节点子节点数大于M / 2并且大于2，则从其处借补
            if (next != null
                    && next.children.size() > tree.getOrder() / 2
                    && next.children.size() > 2) {
                //后叶子节点首位添加到末尾
                B_Add_Node<K, V> borrow = next.children.get(0);
                next.children.remove(0);
                borrow.parent = this;
                children.add(borrow);
                int preIndex = parent.children.indexOf(this);
                keys.add(parent.keys.get(preIndex));
                parent.keys.set(preIndex, next.keys.remove(0));
                return ;
            }

            // 同前面节点合并
            if (previous != null
                    && (previous.children.size() <= tree.getOrder() / 2
                    || previous.children.size() <= 2)) {
                for (int i = 0; i < children.size(); i++) {
                    previous.children.add(children.get(i));
                }
                for(int i = 0; i < previous.children.size();i++){
                    previous.children.get(i).parent = this;
                }
                int indexPre = parent.children.indexOf(previous);
                previous.keys.add(parent.keys.get(indexPre));
                for (int i = 0; i < keys.size(); i++) {
                    previous.keys.add(keys.get(i));
                }
                children = previous.children;
                keys = previous.keys;

                //更新父节点的关键字列表
                parent.children.remove(previous);
                previous.parent = null;
                previous.children = null;
                previous.keys = null;
                parent.keys.remove(parent.children.indexOf(this));
                if((!parent.isRoot
                        && (parent.children.size() >= tree.getOrder() / 2
                        && parent.children.size() >= 2))
                        ||parent.isRoot && parent.children.size() >= 2){
                    return ;
                }
                parent.updateRemove(tree);
                return ;
            }

            // 同后面节点合并
            if (next != null
                    && (next.children.size() <= tree.getOrder() / 2
                    || next.children.size() <= 2)) {
                for (int i = 0; i < next.children.size(); i++) {
                    B_Add_Node<K, V> child = next.children.get(i);
                    children.add(child);
                    child.parent = this;
                }
                int index = parent.children.indexOf(this);
                keys.add(parent.keys.get(index));
                for (int i = 0; i < next.keys.size(); i++) {
                    keys.add(next.keys.get(i));
                }
                parent.children.remove(next);
                next.parent = null;
                next.children = null;
                next.keys = null;
                parent.keys.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                        && parent.children.size() >= 2))
                        ||parent.isRoot && parent.children.size() >= 2){
                    return ;
                }
                parent.updateRemove(tree);
                return ;
            }
        }
    }

    public V remove(K key, B_Add_Tree<K,V> tree){
        //如果是叶子节点
        if (isLeaf){
            //如果不包含该关键字，则直接返回
            if (contains(key) == -1){
                return null;
            }
            //如果既是叶子节点又是根节点，直接删除
            if (isRoot) {
                if(keys.size() == 1){
                    tree.setHeight(0);
                }
                return remove(key);
            }
            //如果关键字数大于M / 2，直接删除
            if (keys.size() > tree.getOrder() / 2 && keys.size() > 2) {
                return remove(key);
            }
            //如果自身关键字数小于M / 2，并且前节点关键字数大于M / 2，则从其处借补
            if (previous != null &&
                    previous.parent == parent
                    && previous.keys.size() > tree.getOrder() / 2
                    && previous.keys.size() > 2 ) {
                //添加到首位
                int size = previous.keys.size();
                keys.add(0, previous.keys.remove(size - 1));
                int index = parent.children.indexOf(previous);
                parent.keys.set(index, keys.get(0));
                return remove(key);
            }
            //如果自身关键字数小于M / 2，并且后节点关键字数大于M / 2，则从其处借补
            if (next != null
                    && next.parent == parent
                    && next.keys.size() > tree.getOrder() / 2
                    && next.keys.size() > 2) {
                keys.add(next.keys.remove(0));
                int index = parent.children.indexOf(this);
                parent.keys.set(index, next.keys.get(0));
                return remove(key);
            }

            //同前面节点合并
            if (previous != null
                    && previous.parent == parent
                    && (previous.keys.size() <= tree.getOrder() / 2
                    || previous.keys.size() <= 2)) {
                V returnValue =  remove(key);
                for (int i = 0; i < keys.size(); i++) {
                    //将当前节点的关键字添加到前节点的末尾
                    previous.keys.add(keys.get(i));
                }
                keys = previous.keys;
                parent.children.remove(previous);
                previous.parent = null;
                previous.keys = null;
                //更新链表
                if (previous.previous != null) {
                    B_Add_Node<K, V> temp = previous;
                    temp.previous.next = this;
                    previous = temp.previous;
                    temp.previous = null;
                    temp.next = null;
                }else {
                    tree.setHead(this);
                    previous.next = null;
                    previous = null;
                }
                parent.keys.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                        && parent.children.size() >= 2))
                        ||parent.isRoot && parent.children.size() >= 2){
                    return returnValue;
                }
                parent.updateRemove(tree);
                return returnValue;
            }
            //同后面节点合并
            if(next != null
                    && next.parent == parent
                    && (next.keys.size() <= tree.getOrder() / 2
                    || next.keys.size() <= 2)) {
                V returnValue = remove(key);
                for (int i = 0; i < next.keys.size(); i++) {
                    //从首位开始添加到末尾
                    keys.add(next.keys.get(i));
                }
                next.parent = null;
                next.keys = null;
                parent.children.remove(next);
                //更新链表
                if (next.next != null) {
                    B_Add_Node<K, V> temp = next;
                    temp.next.previous = this;
                    next = temp.next;
                    temp.previous = null;
                    temp.next = null;
                }else {
                    next.previous = null;
                    next = null;
                }
                //更新父节点的关键字列表
                parent.keys.remove(parent.children.indexOf(this));
                if((!parent.isRoot && (parent.children.size() >= tree.getOrder() / 2
                        && parent.children.size() >= 2))
                        ||parent.isRoot && parent.children.size() >= 2){
                    return returnValue;
                }
                parent.updateRemove(tree);
                return returnValue;
            }
        }
        /*如果不是叶子节点*/

        //如果key小于等于节点最左边的key，沿第一个子节点继续搜索
        if (key.compareTo(keys.get(0).getKey()) < 0) {
            return children.get(0).remove(key, tree);
            //如果key大于节点最右边的key，沿最后一个子节点继续搜索
        }else if (key.compareTo(keys.get(keys.size()-1).getKey()) >= 0) {
            return children.get(children.size()-1).remove(key, tree);
            //否则沿比key大的前一个子节点继续搜索
        }else {
            int low = 0, high = keys.size() - 1, mid= 0;
            int comp ;
            while (low <= high) {
                mid = (low + high) / 2;
                comp = keys.get(mid).getKey().compareTo(key);
                if (comp == 0) {
                    return children.get(mid + 1).remove(key, tree);
                } else if (comp < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
            return children.get(low).remove(key, tree);
        }
    }

    //判断当前节点是否包含该关键字
    private int contains(K key) {
        int low = 0, high = keys.size() - 1, mid;
        int comp ;
        while (low <= high) {
            mid = (low + high) / 2;
            comp = keys.get(mid).getKey().compareTo(key);
            if (comp == 0) {
                return mid;
            } else if (comp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    //删除节点
    private V remove(K key){
        int low = 0,high = keys.size() -1,mid;
        int comp;
        while(low<= high){
            mid  = (low+high)/2;
            comp = keys.get(mid).getKey().compareTo(key);
            if(comp == 0){
                return keys.remove(mid).getValue();
            }else if(comp < 0){
                low = mid + 1;
            }else {
                high = mid - 1;
            }
        }
        return null;
    }

    //重写toString方法，使得按照要求打印结果
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("isRoot: ");
        sb.append(isRoot);
        sb.append(", ");
        sb.append("isLeaf: ");
        sb.append(isLeaf);
        sb.append(", ");
        sb.append("keys: ");
        for (Entry<K,V> entry : keys){
            sb.append(entry.getKey());
            sb.append(", ");
        }
        sb.append(", ");
        return sb.toString();

    }

    //获取叶子节点的next
    public B_Add_Node getNext(){
        return next;
    }

} 

