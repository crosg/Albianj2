package org.albianj.persistence.impl.storage;

/**
 * project : com.yuewen.nrzx.albianj
 *
 * @ccversion 新建 - liyuqi 2018-07-23 15:55</br>
 */
public class Coster {

    public final int[] costs;
    private int index = 0;

    public Coster(int length) {
        this.costs = new int[length];
    }

    public void doCost(long cost) {
        if (index >= costs.length) {
            throw new ArrayIndexOutOfBoundsException("计数器错误,超出限制");
        }
        costs[index++] = (int)cost;
    }

    @Override
    public String toString() {
        if (index == 0) {
            return "";
        }
        StringBuilder pretty = new StringBuilder();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int total = 0;
        pretty.append("Cost Array:[");
        for (int v : costs) {
            if (v > max) {
                max = v;
            }
            if (v < min) {
                min = v;
            }
            total += v;

            pretty.append(v).append(",");
        }
        pretty.append("]");

        StringBuilder res = new StringBuilder();
        res.append("Coster: count=").append(index).append("\t min=").append(min).append("\t avg=")
            .append((total / index)).append("\t max=").append(max).append("\n").append(pretty);
        return res.toString();
    }
}
