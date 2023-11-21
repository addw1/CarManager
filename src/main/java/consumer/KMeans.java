package consumer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class KMeans <T>{
    private List<T> dataArray;//待分类的原始值
    private int K = 3;//将要分成的类别个数
    private int maxClusterTimes = 500;//最大迭代次数
    private List<List<T>> clusterList;//聚类的结果
    private List<T> clusteringCenterT;//质心

    public int getK() {
        return K;
    }
    public void setK(int K) {
        if (K < 1) {
            throw new IllegalArgumentException("K must greater than 0");
        }
        this.K = K;
    }
    public int getMaxClusterTimes() {
        return maxClusterTimes;
    }
    public void setMaxClusterTimes(int maxClusterTimes) {
        if (maxClusterTimes < 10) {
            throw new IllegalArgumentException("maxClusterTimes must greater than 10");
        }
        this.maxClusterTimes = maxClusterTimes;
    }
    public List<T> getClusteringCenterT() {
        return clusteringCenterT;
    }

    public List<List<T>> clustering() {
        if (dataArray == null) {
            return null;
        }
        //初始K个点为数组中的前K个点
        int size = K > dataArray.size() ? dataArray.size() : K;
        List<T> centerT = new ArrayList<T>(size);
        //对数据进行打乱
        Collections.shuffle(dataArray);
        for (int i = 0; i < size; i++) {
            centerT.add(dataArray.get(i));
        }
        clustering(centerT, 0);
        return clusterList;
    }


    private void clustering(List<T> preCenter, int times) {
        if (preCenter == null || preCenter.size() < 2) {
            return;
        }

        Collections.shuffle(preCenter);
        List<List<T>> clusterList =  getListT(preCenter.size());
        for (T o1 : this.dataArray) {

            int max = 0;
            double maxScore = similarScore(o1, preCenter.get(0));
            for (int i = 1; i < preCenter.size(); i++) {
                if (maxScore < similarScore(o1, preCenter.get(i))) {
                    maxScore = similarScore(o1, preCenter.get(i));
                    max = i;
                }
            }
            clusterList.get(max).add(o1);
        }

        List<T> nowCenter = new ArrayList<T> ();
        for (List<T> list : clusterList) {
            nowCenter.add(getCenterT(list));
        }

        if (times >= this.maxClusterTimes || preCenter.size() < this.K) {
            this.clusterList = clusterList;
            return;
        }
        this.clusteringCenterT = nowCenter;
        if (isCenterChange(preCenter, nowCenter)) {
            clear(clusterList);
            clustering(nowCenter, times + 1);
        } else {
            this.clusterList = clusterList;
        }
    }


    private List<List<T>> getListT(int size) {
        List<List<T>> list = new ArrayList<List<T>>(size);
        for (int i = 0; i < size; i++) {
            list.add(new ArrayList<T>());
        }
        return list;
    }


    private void clear(List<List<T>> lists) {
        for (List<T> list : lists) {
            list.clear();
        }
        lists.clear();
    }


    public void addRecord(T value) {
        if (dataArray == null) {
            dataArray = new ArrayList<T>();
        }
        dataArray.add(value);
    }


    private boolean isCenterChange(List<T> preT, List<T> nowT) {
        if (preT == null || nowT == null) {
            return false;
        }
        for (T t1 : preT) {
            boolean bol = true;
            for (T t2 : nowT) {
                if (equals(t1, t2)) {//t1在t2中有相等的，认为该质心未移动
                    bol = false;
                    break;
                }
            }
            //有一个质心发生移动，认为需要进行下一次计算
            if (bol) {
                return bol;
            }
        }
        return false;
    }

    public abstract double similarScore(T o1, T o2);
    public abstract boolean equals(T o1, T o2);

    public abstract T getCenterT(List<T> list);
}