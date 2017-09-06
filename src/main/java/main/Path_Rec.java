package main;

import utils.FileIO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Yangjiali on 2017/9/6 0006.
 * Version 1.0
 */
public class Path_Rec {
    Map<String,Map<String,Double>> user_items_map;
    Map<String,Map<String,Double>> item_users_map;

    public Map<String, Map<String, Double>> getUser_items_map() {
        return user_items_map;
    }

    public Map<String, Map<String, Double>> getItem_users_map() {
        return item_users_map;
    }

    public Path_Rec(String filename, String sep) {
        user_items_map = new HashMap<String, Map<String, Double>>();
        item_users_map = new HashMap<String, Map<String, Double>>();
        List<String> triples = FileIO.readFileByLines(filename);
        for (final String triple : triples) {
            final String[] tripleList = triple.split(sep);
            if (!user_items_map.containsKey(tripleList[0])){
                Map<String,Double> item_rating = new HashMap<String, Double>(){{
                    put(tripleList[1],Double.valueOf(tripleList[2]));
                }};
                user_items_map.put(tripleList[0],item_rating);
            }
            else {
                user_items_map.get(tripleList[0]).put(tripleList[1],Double.valueOf(tripleList[2]));
            }
            if (!item_users_map.containsKey(tripleList[1])){
                Map<String,Double> user_rating = new HashMap<String, Double>(){{
                    put(tripleList[0],Double.valueOf(tripleList[1]));
                }};
                item_users_map.put(tripleList[1],user_rating);
            }
            else {
                item_users_map.get(tripleList[1]).put(tripleList[0],Double.valueOf(tripleList[2]));
            }
        }
    }
    //划分训练集和测试集
    public Map<String,Map<String,Double>> splitTrainTest(double splitrate){
        Map<String, Map<String, Double>> testUserItemMap = new HashMap<String, Map<String, Double>>();
        for (Map.Entry<String, Map<String, Double>> userItemRating : user_items_map.entrySet()) {
            Map<String,Double> testItemMap = new HashMap<String, Double>();
            int testLen = (int)(userItemRating.getValue().size()*splitrate);
            int i = 0;
            String[] itemArray = userItemRating.getValue().keySet().toArray(new String[0]);
            while (i++ < testLen){
                Random ranNum = new Random();
                String randomItem = itemArray[ranNum.nextInt(itemArray.length)];
                testItemMap.put(randomItem,userItemRating.getValue().get(randomItem));

                //将user_items_map和item_users_map中测试集去除
                user_items_map.get(userItemRating.getKey()).remove(randomItem);
                item_users_map.get(randomItem).remove(userItemRating.getKey());
            }
            testUserItemMap.put(userItemRating.getKey(),testItemMap);
        }
        return testUserItemMap;
    }
    //根据短路径获取评分
    public Map<String,Map<String,Double>> getRecListByPath(){
        Map<String,Double> itemPosRate = getPosRate();
        Map<String,Map<String,Double>> recMap = new HashMap<String, Map<String, Double>>();
        for (Map.Entry<String, Map<String, Double>> user_items_entry : user_items_map.entrySet()) {
            Map<String,Double> rankMap = new HashMap<String, Double>();
            for (Map.Entry<String, Double> items_entry : user_items_entry.getValue().entrySet()) {
                //该用户物品的正面率
                double selfItemRate = itemPosRate.get(items_entry.getKey());
                for (Map.Entry<String, Double> neiUserItem : item_users_map.get(items_entry.getKey()).entrySet()) {
                    for (Map.Entry<String, Double> targetItem : user_items_map.get(neiUserItem.getKey()).entrySet()) {
                        //邻居用户物品的正面率
                        double otherItemRate = itemPosRate.get(targetItem.getKey());
                        if (!rankMap.containsKey(targetItem.getKey())){
                            rankMap.put(targetItem.getKey(), otherItemRate * selfItemRate);
                        }
                        else {
                            rankMap.put(targetItem.getKey(), rankMap.get(targetItem.getKey()) + otherItemRate * selfItemRate);
                        }
                    }
                }
            }
            recMap.put(user_items_entry.getKey(),rankMap);
        }
        return recMap;
    }

    public Map<String, Double> getPosRate() {
        Map<String,Double> itemPosRating = new HashMap<>();
        double avg = 0;
        for (Map.Entry<String, Map<String, Double>> itemRating : item_users_map.entrySet()) {
            for (double rating : itemRating.getValue().values()) {
                avg += rating;
            }
            avg /= itemRating.getValue().size();
            int pos = 0, neg = 0;
            for (double rating : itemRating.getValue().values()) {
                if (rating >= avg) {
                    pos++;
                } else neg++;
            }
            itemPosRating.put(itemRating.getKey(),(double) (pos + 1) / (double) (pos + neg + 1));
        }

        return itemPosRating;
    }
}
