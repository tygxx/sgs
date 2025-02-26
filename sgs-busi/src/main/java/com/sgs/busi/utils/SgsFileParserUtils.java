package com.sgs.busi.utils;

import com.alibaba.dashscope.utils.JsonUtils;
import com.sgs.busi.model.SgsFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SGS文件解析工具类
 */
@Slf4j
public class SgsFileParserUtils {

    /**
     * 解析SGS文件（支持Word和PDF格式）
     *
     * @param filePath 文件完整路径
     * @return 解析后的SGS文件信息
     * @throws IOException IO异常
     */
    public static SgsFileInfo parseSgsFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("文件不存在：" + filePath);
        }

        String extension = FilenameUtils.getExtension(filePath).toLowerCase();
        String content;

        // 根据文件扩展名选择不同的解析方式
        if ("docx".equals(extension)) {
            content = parseWordDocument(file);
        } else if ("pdf".equals(extension)) {
            content = parsePdfDocument(file);
        } else {
            throw new IllegalArgumentException("不支持的文件格式，仅支持.docx和.pdf文件");
        }

        // 解析提取到的文本内容
        return parseContent(content);
    }

    /**
     * 解析Word文档
     */
    private static String parseWordDocument(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {
            StringBuilder contentBuilder = new StringBuilder();

            // 读取页眉内容
            // for (XWPFHeader header : document.getHeaderList()) {
            //     contentBuilder.append(header.getText()).append("\n");
            // }

            // 读取段落内容
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText();
                if (text != null && !text.trim().isEmpty()) {
                    contentBuilder.append(text).append("\n");
                }
            }

            // 读取表格内容
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    List<String> cellTexts = new ArrayList<>();
                    for (XWPFTableCell cell : row.getTableCells()) {
                        String cellText = cell.getText().trim();
                        if (!cellText.isEmpty()) {
                            cellTexts.add(cellText);
                        }
                    }
                    if (!cellTexts.isEmpty()) {
                        contentBuilder.append(String.join(" | ", cellTexts)).append("\n");
                    }
                }
            }

            // 读取页脚内容
            // for (XWPFFooter footer : document.getFooterList()) {
            //     contentBuilder.append(footer.getText()).append("\n");
            // }

            return contentBuilder.toString().trim();
        }
    }

    /**
     * 解析PDF文档
     */
    private static String parsePdfDocument(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * 解析提取到的文本内容
     */
    private static SgsFileInfo parseContent(String content) {
        SgsFileInfo sgsFileInfo = new SgsFileInfo();

        // 定义提取信息的正则表达式模式
        Pattern pattern = Pattern.compile("(?:客户名称：\\s*[|]?\\s*([^\\n|]+))|" +
                "(?:客户地址：\\s*[|]?\\s*([^\\n|]+))|" +
                "(?:样品名称：\\s*[|]?\\s*([^\\n|]+))|" +
                "(?:型号：\\s*[|]?\\s*([^\\n|]+))|" +
                "(?:料号：\\s*[|]?\\s*([^\\n|]*))|" +
                "(?:客户参考信息：\\s*[|]?\\s*([^\\n|]*))|" +
                "(?:样品类型：\\s*[|]?\\s*([^\\n|]+))");

        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (matcher.group(1) != null) sgsFileInfo.setCustomerName(matcher.group(1).trim());
            if (matcher.group(2) != null) sgsFileInfo.setCustomerAddress(matcher.group(2).trim());
            if (matcher.group(3) != null) sgsFileInfo.setSampleName(matcher.group(3).trim());
            if (matcher.group(4) != null) sgsFileInfo.setModelNumber(matcher.group(4).trim());
            if (matcher.group(5) != null) sgsFileInfo.setMaterialNumber(matcher.group(5).trim());
            if (matcher.group(6) != null) sgsFileInfo.setCustomerReference(matcher.group(6).trim());
            if (matcher.group(7) != null) sgsFileInfo.setSampleType(matcher.group(7).trim());
        }

        return sgsFileInfo;
    }

    public static void main(String[] args) throws IOException {
        SgsFileInfo sgsFileInfo = parseSgsFile("/Users/tengyong/Downloads/报告.docx");
        System.out.println(JsonUtils.toJson(sgsFileInfo));
    }
}
