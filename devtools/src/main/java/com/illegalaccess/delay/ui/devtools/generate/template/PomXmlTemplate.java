package com.illegalaccess.delay.ui.devtools.generate.template;

import com.illegalaccess.delay.ui.devtools.generate.utils.FileUtil;
import com.illegalaccess.delay.ui.devtools.generate.utils.GenerateUtil;
import com.illegalaccess.delay.ui.devtools.generate.domain.Generate;
import com.illegalaccess.delay.ui.devtools.generate.utils.parser.XmlParseUtil;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

/**
 * @author 小懒虫
 * @date 2018/10/25
 */
public class PomXmlTemplate {

    /**
     * 生成页面
     */
    private static String genHtmlBody(Generate generate) throws IOException {
        // 构建数据
        String module = generate.getBasic().getGenModule();
        String path = FileUtil.templatePath(PomXmlTemplate.class);

        // 获取Jsoup文档对象
        Document document = XmlParseUtil.document(path);

        // 替换基本数据
        String html = XmlParseUtil.html(document);
        return html.replace("#{module}", module);
    }

    /**
     * 生成列表页面模板
     */
    public static String generate(Generate generate, String filePath) {
        try {
            String content = PomXmlTemplate.genHtmlBody(generate);
            GenerateUtil.generateFile(filePath, content);
        } catch (FileAlreadyExistsException e) {
            return GenerateUtil.fileExist(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
