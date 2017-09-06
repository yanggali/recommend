package main;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Yangjiali on 2017/9/6 0006.
 * Version 1.0
 */
public class Test {
    public static double splitRate = 0.2;
    public static String ratingFileName = "src\\main\\resources\\datasets\\movielens\\user_ratedmovies-timestamps.dat";
    public static String testFileName = "src\\main\\resources\\datasets\\movielens\\sample_test_data.dat";
    public static void main(String[] args) {
        Path_Rec pr = new Path_Rec(testFileName,"\t");
        System.out.println("initial data finished");
        //测试集
        Map<String, Map<String, Double>> testUserItems = pr.splitTrainTest(splitRate);
        System.out.println("训练集测试集划分结束");
        //预估集
        Map<String, Map<String, Double>> predUserItems = pr.getRecListByPath();
        System.out.println("一共有用户:"+testUserItems.size());
        evaluatePre(testUserItems,predUserItems,10);
    }

    //评估准确度
    public static void evaluatePre(Map<String,Map<String,Double>> testMap,Map<String,Map<String,Double>> predMap,int recNum){
        double avgPrecision = 0;
        for (Map.Entry<String, Map<String, Double>> entry : predMap.entrySet()) {
            Map<String, Double> predItemMap = sortByValue(entry.getValue(), 1);
            Map<String,Double> testItemMap = testMap.get(entry.getKey());
            Stream<Map.Entry<String,Double>> itemMapStream = predItemMap.entrySet().stream();
            Set<String> recSet = itemMapStream.limit(recNum).map(e->e.getKey()).collect(Collectors.toSet());
            int N = recSet.size();
            recSet.retainAll(testMap.get(entry.getKey()).keySet());
            int hitNum = recSet.size();
            if (N != 0){
                avgPrecision += (double) hitNum / (double) N;
                System.out.println("用户"+entry.getKey()+"的准确率为:"+(double) hitNum / (double) N);
            }
            else {
                continue;
            }
        }
        System.out.println("平均准确率为：" +avgPrecision/predMap.size());
    }
    //对map根据value排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map ,int type) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                if (type == 1){
                    return -(o1.getValue()).compareTo(o2.getValue());
                }
                else
                    return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
