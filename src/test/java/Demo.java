import junit.framework.TestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @类名: Demo
 * @包名：PACKAGE_NAME
 * @作者：kevin[wangqi2017@xinhua.org]
 * @时间：2018/6/14 15:36
 * @版本：1.0
 * @描述：
 */
public class Demo extends TestCase {

    public void testReg() {
//        String text = "亚欧总分社";
//        String reg = "(?<!中东|亚欧总)分社";
        String text = "中东总分社";
        String reg = "^亚欧总分社|中东分社";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String group = matcher.group();
            int start = matcher.start();
            int end = matcher.end();
            System.out.println("[" + start + "," + end + "): " + group);
        }
    }
}
