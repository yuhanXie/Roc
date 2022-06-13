package com.roc.code.contain;

/**
 * @author xiegang
 */
public class L310 {


    public static void main(String[] args) {
        L310 l310 = new L310();
//        System.out.println(l310.bulbSwitch(0));
//        System.out.println(l310.bulbSwitch(1));
//        System.out.println(l310.bulbSwitch(2));
//        System.out.println(l310.bulbSwitch(3));
//        System.out.println(l310.bulbSwitch(4));

        System.out.println(l310.maxProduct(new String[]{"abcw","baz","foo","bar","xtfn","abcdef"}));
        System.out.println(l310.maxProduct(new String[]{"a","ab","abc","d","cd","bcd","abcd"}));
        System.out.println(l310.maxProduct(new String[]{"a","aa","aaa","aaaa"}));
    }


    /**
     * L318
     *
     * @param words
     * @return
     */
    public int maxProduct(String[] words) {
        //暴力法：1. 对每个数组遍历其他元素，看是否包含，不包含则计算其长度的乘积。这个没有什么规律性，应该优化不了
        //2. 比较两个单词是否存在重复的字母。使用位运算来比较两个单词是否存在
        int[] ids = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            //假设是26位的二进制数，对应字母出现的话，当前位置上置为1
            int cur = 0;
            for (char ch : word.toCharArray()) {
                int bit = 1 << (ch - 'a');
                cur |= bit;
            }
            ids[i] = cur;
        }

        int result = 0;
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if ((ids[i] & ids[j]) == 0) {
                    //说明两个单词不存在字母相同的
                    result = Math.max(result, words[i].length() * words[j].length());
                }
            }
        }
        return result;
    }


    public int bulbSwitch(int n) {
        //暴力法，时间O(n*n)---> 超时了
        int[] source = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((j + 1) % (i + 1) == 0) {
                    source[j] = source[j] == 1 ? 0 : 1;
                }
            }
        }
        int count = 0;
        for (int num : source) {
            if (num == 1) {
                count++;
            }
        }
        return count;
    }

    /**
     * L319. 灯泡开关
     *
     * 初始时有 n 个灯泡处于关闭状态。第一轮，你将会打开所有灯泡。接下来的第二轮，你将会每两个灯泡关闭一个。
     *
     * 第三轮，你每三个灯泡就切换一个灯泡的开关（即，打开变关闭，关闭变打开）。第 i 轮，你每 i 个灯泡就切换一个灯泡的开关。直到第 n 轮，你只需要切换最后一个灯泡的开关。
     *
     * 找出并返回 n 轮后有多少个亮着的灯泡。
     *
     * @param n
     * @return
     */
    public int bulbSwitchV2(int n) {
        //第i轮改变的是所有i的倍数的开关
        //对于位置为k的灯，它会在 i*x=k的轮次里切换->如果在i轮会切换，那么在x轮也会切换，那就还原了。
        //举个例子：99  3，9，11，33，它就是一个偶数个约数，那么就无变化。
        //那什么时候是奇数个呢？完全平方数的时候 16: 2,4,8;100:2,4,5,10,20,25,50
        //那也就是在n以内的完全平方数的个数
        return (int) Math.sqrt(n);
    }
}
