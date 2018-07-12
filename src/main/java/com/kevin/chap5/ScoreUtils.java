package com.kevin.chap5;

import com.kevin.util.Constants;
import org.apache.lucene.index.NumericDocValues;

import java.io.IOException;

/**
 * @类名: ScoreUtils
 * @包名：com.kevin.chap5
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/7/12 16:33
 * @版本：1.0
 * @描述：计算衰减因子[按天为单位]
 */
public class ScoreUtils {
    /** 存储衰减因子，按天为单位 **/
    private  static float[] daysDampingFactor = new float[120];
    /** 降级阈值 **/
    private static float demoteboost = 0.9f;

    static {
        daysDampingFactor[0] = 1;
        // 第一周，每一天降权一次
        for (int i = 1; i < 7; i++) {
            daysDampingFactor[i] = daysDampingFactor[i - 1] * demoteboost;
        }
        // 第一月，每七天降权一次
        for (int i = 7; i < 31; i++) {
            daysDampingFactor[i] = daysDampingFactor[i / 7 * 7 - 1] * demoteboost;
        }
        // 第一月以后，每一月降权一次
        for (int i = 31; i < daysDampingFactor.length; i++) {
            daysDampingFactor[i] = daysDampingFactor[i / 31 * 31 - 1] * demoteboost;
        }
    }

    /**
     * 根据相差天数获取当前的权重衰减因子
     * @param delta
     * @return
     */
    private static float dayDamping(int delta) {
        float factor = delta < daysDampingFactor.length ? daysDampingFactor[delta]
                : daysDampingFactor[daysDampingFactor.length - 1];
        System.out.println("delta: " + delta + "-->factor: " + factor);
        return factor;
    }

    public static float getNewsScoreFactor(long now, NumericDocValues numericDocValues)
            throws IOException {
        long time = numericDocValues.longValue();
        float factor = 1;
        int day = (int) (time / Constants.DAY_MILLIS);
        int nowDay = (int) (now / Constants.DAY_MILLIS);
        System.out.println(day + ":" + nowDay + ":" + (nowDay - day));
        if (day < nowDay) {
            // 如果提供的日期比当前日期小，则计算相差天数，传入dayDamping计算日期衰减因子
            factor = dayDamping(nowDay - day);
        } else if (day > nowDay) {
            // 如果提供的日期比当前日期大
            factor = Float.MIN_VALUE;
        } else if (now >= time && now - time <= Constants.HALF_HOUR_MILLIS) {
            // 如果两者是同一天且提供的日期是过去半小时之内的，则权重因子乘以2
            factor = 2;
        }
        return factor;
    }
}
