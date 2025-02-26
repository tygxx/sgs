package com.sgs.busi.utils;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.sgs.busi.model.SgsFileInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * @Author: tengYong
 * @date: 2025/2/25 20:58
 * @description: 阿里-百炼
 */
@Slf4j
public class DashscopeUtils {

    public static final String API_KEY = System.getenv("A_LI_YUN_API_KEY");
    public static final String MODEL = "qwen-plus";

    /**
     * 检测文件内容是否合规
     *
     * {
     * 	"requestId": "c6cffe1f-8719-95ee-ab37-56c9c90c5e84",
     * 	"usage": {
     * 		"input_tokens": 588,
     * 		"output_tokens": 367,
     * 		"total_tokens": 955
     * 	    },
     * 	"output": {
     * 		"choices": [{
     * 			"finish_reason": "stop",
     * 			"message": {
     * 				"role": "assistant",
     * 				"content": "根据提供的规则，以下是审核结果和不合规的原因：\n\n1. **客户名称**：\n   - 合规。客户名称为“武汉市柒核科技有限公司”，符合公司名称的要求。\n\n2. **客户地址**：\n   - 不完全合规。客户地址仅填写了“湖北省武汉市”，过于简略。建议提供更详细的地址信息，例如具体的街道、门牌号等，以确保地址的准确性和完整性。\n\n3. **样品名称**：\n   - 不合规。样品名称“最牛逼水性网印油墨”中包含夸张词汇“最牛逼”，这不符合规则中的要求（不能出现夸张的成品名称）。建议修改为更客观的描述，例如“水性网印油墨”。\n\n4. **型号**：\n   - 不合规。型号“ABC系列”中包含了“系列”字眼，这不符合规则中的要求（不要出现系列字眼）。建议修改为具体的型号名称，例如“ABC-123”。\n\n5. **料号**：\n   - 合规。料号为空，但没有违反规则中关于数量的要求。\n\n6. **客户参考信息**：\n   - 合规。客户参考信息为“null”，虽然为空，但没有违反规则中的任何限制条件。\n\n### 建议修改后的信息：\n- **客户名称**：武汉市柒核科技有限公司\n- **客户地址**：湖北省武汉市XX区XX街道XX号（请补充详细地址）\n- **样品名称**：水性网印油墨\n- **型号**：ABC-123（或具体型号）\n- **料号**：（保持不变）\n- **客户参考信息**：null\n\n请根据以上建议进行修改，以确保所有内容符合审核规则。"
     *            }
     *        }]
     *    }
     * }
     * @param systemContent 系统内容
     * @param filePath 文件路径
     */
    public static void checkFile(String systemContent, String filePath) {
        try {
            SgsFileInfo sgsFileInfo = SgsFileParserUtils.parseSgsFile(filePath);
            String userContent = "客户名称：" + sgsFileInfo.getCustomerName() + "\n"
                    + "客户地址：" + sgsFileInfo.getCustomerAddress() + "\n"
                    + "样品名称：" + sgsFileInfo.getSampleName() + "\n"
                    + "型号：" + sgsFileInfo.getModelNumber() + "\n"
                    + "料号：" + sgsFileInfo.getMaterialNumber() + "\n"
                    + "客户参考信息：" + sgsFileInfo.getCustomerReference() + "\n"
                    + "样品类型：" + sgsFileInfo.getSampleType();
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(systemContent)
                    .build();
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userContent)
                    .build();
            GenerationParam param = GenerationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    .apiKey(API_KEY)
                    // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                    .model(MODEL)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();
            GenerationResult result = gen.call(param);
            System.out.println(JsonUtils.toJson(result));
        } catch (Exception e) {
            // 使用日志框架记录异常信息
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
    }

    public static GenerationResult callWithMessage() throws ApiException, NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("你是谁？")
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(API_KEY)
                // 此处以qwen-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model(MODEL)
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

    public static void main(String[] args) {
        // try {
        //     GenerationResult result = callWithMessage();
        //     System.out.println(JsonUtils.toJson(result));
        // } catch (ApiException | NoApiKeyException | InputRequiredException e) {
        //     // 使用日志框架记录异常信息
        //     System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        // }
        checkFile(SgsFileInfo.SYSTEM_CONTENT, "/Users/tengyong/Downloads/报告.docx");
    }
}
