import index.BplusNode;
import index.BplusTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhy on 2018/4/17.
 */
public class TestBPlusTree {
    public static void main(String[] args) {
        Map<String, Integer> insertData = new HashMap<>();
        for (int i = 102; i < 1000; i++) {
            if (i % 3 != 0)
                insertData.put("zhy" + i, i);
        }
        testInsert(23, insertData);
    }

    private static void testInsert(int order, Map<String, Integer> insertData) {
        BplusTree<String, Integer> tree = new BplusTree<>(order);
        long current = System.currentTimeMillis();
        for (Map.Entry<String, Integer> insertEntry : insertData.entrySet()) {
            tree.insertOrUpdate(insertEntry.getKey(), insertEntry.getValue());
        }
        System.out.println("show all...");
        showAll(tree);
        System.out.println("show range...");
        rangeShow(tree, "zhy111", "zhy132");
//        if(!res.get(0).getKey().equals("zhy151") || !res.get(1).getKey().equals("zhy182"))
//            System.out.println("error");
        long duration = System.currentTimeMillis() - current;
        System.out.println("time elpsed for duration: " + duration);
    }

    public static void showAll(BplusTree<String, Integer> tree) {
        BplusNode<String, Integer> head = tree.getHead();
        System.out.println(head);
        BplusNode<String, Integer> next = head.getNext();
        while (next != null) {
            System.out.println(next);
            next = next.getNext();
        }
    }

    public static void rangeShow(BplusTree<String, Integer> tree, String start, String end) {
        System.out.println("start----end");
        List<Map.Entry<String, Integer>> res = tree.getRange(start, end);
        System.out.println(res);
        System.out.println("start----");
        res = tree.getRangeStart(start);
        System.out.println(res);
        System.out.println("----end");
        res = tree.getRangeEnd(end);
        System.out.println(res);
    }
}
