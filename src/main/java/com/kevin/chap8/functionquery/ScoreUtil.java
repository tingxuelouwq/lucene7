package com.kevin.chap8.functionquery;

import java.io.IOException;

/**
 * 类名: ScoreUtil<br/>
 * 包名：com.kevin.chap8.functionquery<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/8/21 10:04<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class ScoreUtil {

    /** 衰减因子数组 **/
    private static final float[] daysDampingFactor = new float[120];
    /** 衰减系数 **/
    private static float dampingFactor = 0.9f;
    /** 一天的毫秒数 */
    private static final long DAY_MILLIS = 24*3600*1000;
    /** 一天的秒数 */
    private static final long DAY_SECONDS = 24*3600;
    /** 一分钟的毫秒数 */
    private static final int MINUTE_MILLIS = 60*1000;
    /** 半小时的毫秒数 */
    private static final int HALF_HOUR_MILLIS = 30*60*1000;
    /** 一分钟的秒数 */
    private static final int MINUTE_SECONDS = 60;

    static {
        daysDampingFactor[0] = 1;
        // 第一周
        for (int i = 1; i < 7; i++) {
            daysDampingFactor[i] = daysDampingFactor[i - 1] * dampingFactor;
        }
        // 第一月
        for (int i = 7; i < 31; i++) {
            daysDampingFactor[i] = daysDampingFactor[i / 7 * 7 - 1] * dampingFactor;
        }
        // 第一月之后
        for (int i = 31; i < daysDampingFactor.length; i++) {
            daysDampingFactor[i] = daysDampingFactor[i / 31 * 31 - 1] * dampingFactor;
        }
    }

    /**
     * 根据相差天数获取衰减因子
     * @param delta
     * @return
     */
    private static float dayDamping(int delta) {
        float factor = delta < daysDampingFactor.length ? daysDampingFactor[delta]
                : daysDampingFactor[daysDampingFactor.length - 1];
        System.out.println("delta: " + delta + "-->" + "factor: " + factor);
        return factor;
    }

    /**
     * 根据提供的日期与当前日期，获取新得分<br/>
     * 如果提供的日期比当前日期小，则计算相差天数，并计算衰减因子；<br/>
     * 如果提供的日期比当前日期大，则衰减因子为<code>Float.MIN_VALUE</code>；<br/>
     * 如果提供的日期与当前日期相同，并且相差在半小时之内，则衰减因子为2.0f；<br/>
     * 否则，衰减因子为1.0f<br/>
     * @param now
     * @param currentTime
     * @return
     * @throws IOException
     */
    public static float getNewScoreFactor(long now, long currentTime)
            throws IOException {
        int day = (int) (currentTime / DAY_MILLIS);
        int currentDay = (int) (now / DAY_MILLIS);
        System.out.println("day: " + day + ":" + (currentDay - day));

        float factor = 1.0f;
        if (day < currentDay) {
            factor = dayDamping(currentDay - day);
        } else if (day > currentDay) {
            factor = Float.MIN_VALUE;
        } else if (now >= currentTime && (now - currentTime) <= HALF_HOUR_MILLIS) {
            factor = 2.0f;
        }
        return factor;
    }
}
