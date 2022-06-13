package com.roc.java.word;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gang.xie
 */
public class WordUtils {

    public static void main(String[] args) throws Exception {
//        WordUtils wordUtils = new WordUtils();
//        wordUtils.exportWord();
        String accountCode = "1111.11";
        String accountCode2 = "111111";
        int lastIndex = accountCode.lastIndexOf(".");
        int lastIndex2 = accountCode2.lastIndexOf(".");
        System.out.println(lastIndex2);
        System.out.println(accountCode.substring(0, lastIndex));
        System.out.println(accountCode2.substring(0, lastIndex2));

    }

    public String exportWord() throws Exception {
        String filePath = "/home/yuhan/Documents/project/zsfund/output/";
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
        configuration.setClassForTemplateLoading(this.getClass(), "/");
        Template template = configuration.getTemplate("基金经理报告.ftl", "UTF-8");
        String fileName = "测试.doc";
        String path = filePath + fileName;

        // 输出文档路径及名称
        File outFile = new File(path);
        FileOutputStream fos = new FileOutputStream(outFile);
        OutputStreamWriter oWriter = new OutputStreamWriter(fos, "UTF-8");
        Writer out = new BufferedWriter(oWriter);

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("managerName", "王宗全");
        template.process(dataMap, out);
        out.close();
        fos.close();

        return path;
    }

}
