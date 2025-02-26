package com.sgs.busi.utils;


import com.sgs.busi.model.SgsFileInfo;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import io.reactivex.schedulers.Schedulers;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: tengYong
 * @date: 2025/2/25
 * @description: 字节-火山引擎工具类
 */
public class VolcengineUtils {

    public static final String API_KEY = System.getenv("HUO_SHAN_API_KEY");
    public static final String MODEL = "deepseek-r1-250120";

    /**
     * 检测文件内容合规性，打印结果示例
     * 根据提供的规则，以下内容存在不合规情况，需进行修改：
     *
     * 1. 样品名称违规：
     * - 违规原因：出现夸张表述"最牛逼"，违反规则「2.原材料的检测，品名不能出现"夸张"的成品名称」
     * - 建议修改：删除夸张表述，例如改为"水性网印油墨"
     *
     * 2. 型号违规：
     * - 违规原因：出现"系列"字眼，违反规则「1.不要出现系列字眼」
     * - 建议修改：删除"系列"字样，例如改为"ABC"
     *
     * 合规部分核查：
     * √ 客户名称：中文名称完整且无个人名义
     * √ 客户地址：中文地址完整
     * √ 料号：未发现多品名问题
     * √ 客户参考信息：null视为未填写，不违反规则（需确认系统是否允许空值）
     * √ 样品类型：描述清晰明确
     *
     * 特殊注意事项：
     * 若报告为英文版本，需同步将客户名称和地址翻译为英文版本，并确保翻译准确性。当前中文版本的信息格式符合基本要求（除上述违规项外）。
     *
     * @param systemContent 系统内容
     * @param filePath      文件路径
     */
    public static void checkFile(String systemContent, String filePath) throws IOException {
        SgsFileInfo sgsFileInfo = SgsFileParserUtils.parseSgsFile(filePath);
        String userContent = "客户名称：" + sgsFileInfo.getCustomerName() + "\n"
                + "客户地址：" + sgsFileInfo.getCustomerAddress() + "\n"
                + "样品名称：" + sgsFileInfo.getSampleName() + "\n"
                + "型号：" + sgsFileInfo.getModelNumber() + "\n"
                + "料号：" + sgsFileInfo.getMaterialNumber() + "\n"
                + "客户参考信息：" + sgsFileInfo.getCustomerReference() + "\n"
                + "样品类型：" + sgsFileInfo.getSampleType();
        ArkService service = ArkService.builder()
                .apiKey(API_KEY)
                // .baseUrl("https://ark.cn-beijing.volces.com/api/v3/chat/completions") // 如不填默认访问华北 2 (北京)
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(2)
                .build();
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content(systemContent).build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content(userContent).build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .build();

        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));

        // shutdown service
        service.shutdownExecutor();
    }

    /**
     * 单轮对话，打印示例如下
     * 十字花科（Brassicaceae，旧称Cruciferae）植物以花朵呈十字形排列（四瓣花瓣）为特征，包含许多常见的蔬菜、油料作物和观赏植物。以下是一些常见的十字花科植物：
     * <p>
     * ---
     * <p>
     * ### 🌱 **常见蔬菜类**
     * 1. **白菜**（大白菜、小白菜）
     * 2. **甘蓝类**：卷心菜（包菜）、紫甘蓝、羽衣甘蓝、西兰花（绿花椰菜）、花椰菜（白花菜）、芥蓝、苤蓝（球茎甘蓝）。
     * 3. **萝卜**（白萝卜、红萝卜、樱桃萝卜）
     * 4. **芥菜类**：叶用芥菜、雪里蕻、榨菜（茎用芥菜）、大头菜（根用芥菜）。
     * 5. **油菜**（菜心、红菜薹）
     * 6. **芝麻菜**（火箭菜，常用于沙拉）
     * 7. **西洋菜**（水田芥）
     * 8. **荠菜**（野菜，种子为“荠菜籽”）
     * <p>
     * ---
     * <p>
     * ### 🌾 **油料/经济作物**
     * 1. **油菜**（种子榨油为菜籽油）
     * 2. **芥菜籽**（用于制作芥末）
     * <p>
     * ---
     * <p>
     * ### 🌸 **观赏植物**
     * 1. **紫罗兰**（花朵艳丽，园艺品种多）
     * 2. **香雪球**（常用于花坛装饰）
     * 3. **桂竹香**（花色丰富，香气浓郁）
     * <p>
     * ---
     * <p>
     * ### 🧂 **调味料植物**
     * 1. **辣根**（根茎磨碎后制辣根酱）
     * 2. **山葵**（日本料理中常用，制作“wasabi”）
     * <p>
     * ---
     * <p>
     * ### 🧬 **营养特点**
     * 十字花科蔬菜富含硫代葡萄糖苷（glucosinolates），在人体内可转化为具有抗氧化、抗炎和抗癌潜力的活性物质（如萝卜硫素），因此常被视为健康饮食的重要组成。
     * <p>
     * ---
     * <p>
     * 如果对具体植物或用途感兴趣，可以进一步探讨哦！ 😊
     */
    public static void singleChat() {
        ArkService service = ArkService.builder()
                .apiKey(API_KEY)
                // .baseUrl("https://ark.cn-beijing.volces.com/api/v3/chat/completions") // 如不填默认访问华北 2 (北京)
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(2)
                .build();
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        messages.add(systemMessage);
        messages.add(userMessage);

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .build();

        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));

        // shutdown service
        service.shutdownExecutor();
    }

    /**
     * 多轮对话，打印示例如下
     * <p>
     * 花椰菜（学名：**Brassica oleracea var. botrytis**）是十字花科芸薹属的一种蔬菜，属于甘蓝（卷心菜）的变种。以下是关于花椰菜的详细解析：
     * <p>
     * ---
     * <p>
     * ### **1. 植物学特征**
     * - **外观**：花椰菜的可食用部分是由未发育的**花芽**和**花梗**组成的紧密花序（俗称“花球”），通常呈白色、乳白色或浅黄色。近年也培育出紫色、橙色等彩色品种。
     * - **生长习性**：喜冷凉气候，适宜在温和湿润的环境中生长，对土壤和光照有一定要求。
     * <p>
     * ---
     * <p>
     * ### **2. 营养价值**
     * - **维生素**：富含维生素C（增强免疫力）、维生素K（促进凝血）、B族维生素（如叶酸）等。
     * - **矿物质**：含钙、钾、镁、磷、铁等，是低热量、高纤维的蔬菜。
     * - **抗氧化物质**：含硫代葡萄糖苷（抗癌潜力）、类黄酮等。
     * - **膳食纤维**：促进消化，改善肠道健康。
     * <p>
     * ---
     * <p>
     * ### **3. 常见品种**
     * - **白花菜**：最常见的品种，花球紧密洁白。
     * - **西兰花（绿花菜）**：与花椰菜近亲，但花球呈绿色，营养更丰富。
     * - **紫色花椰菜**：含花青素，抗氧化能力更强。
     * - **罗马花椰菜**：外形独特，呈螺旋状分形结构。
     * <p>
     * ---
     * <p>
     * ### **4. 烹饪与食用**
     * - **预处理**：去除外层叶片，切块后建议用盐水浸泡去除虫卵或杂质。
     * - **烹饪方式**：可焯水、蒸煮、炒制、烤制或打成泥（如替代米饭的“花椰菜米饭”）。
     * - **搭配**：常与肉类、奶酪、咖喱等搭配，也可凉拌或腌制。
     * <p>
     * ---
     * <p>
     * ### **5. 健康益处**
     * - **抗癌作用**：十字花科蔬菜含异硫氰酸酯，可能抑制癌细胞生长。
     * - **心血管健康**：膳食纤维和抗氧化成分有助于降低胆固醇。
     * - **控制体重**：低热量、高饱腹感，适合减脂饮食。
     * <p>
     * ---
     * <p>
     * ### **6. 选购与保存**
     * - **选购**：花球紧实、颜色均匀、无黑斑或发黄。
     * - **保存**：冷藏可存放1周左右，长时间保存可焯水后冷冻。
     * <p>
     * ---
     * <p>
     * ### **7. 文化与小知识**
     * - **起源**：原产地中海地区，约19世纪传入中国。
     * - **名称差异**：北方多称“菜花”，南方称“花椰菜”，英文为**Cauliflower**。
     * - **趣味用途**：因质地类似肉类，常被用作素食替代品（如“花椰菜牛排”）。
     * <p>
     * ---
     * <p>
     * 花椰菜不仅营养丰富，还因其多样的烹饪方式成为健康饮食的明星食材！你对它的哪个方面最感兴趣呢？ 😊
     */
    public static void multiChat() {
        ArkService service = ArkService.builder()
                .apiKey(API_KEY)
                // .baseUrl("https://ark.cn-beijing.volces.com/api/v3/chat/completions") // 如不填默认访问华北 2 (北京)
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(2)
                .build();
        final List<ChatMessage> messages = Arrays.asList(
                ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build(),
                ChatMessage.builder().role(ChatMessageRole.USER).content("花椰菜是什么？").build(),
                ChatMessage.builder().role(ChatMessageRole.ASSISTANT).content("花椰菜又称菜花、花菜，是一种常见的蔬菜。").build(),
                ChatMessage.builder().role(ChatMessageRole.USER).content("再详细点").build()
        );

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .build();

        service.createChatCompletion(chatCompletionRequest).getChoices().forEach(choice -> System.out.println(choice.getMessage().getContent()));

        // shutdown service
        service.shutdownExecutor();
    }

    /**
     * 流式对话，打印示例如下（将结果逐字打印）
     * <p>
     * 十字花科（Brassicaceae，旧称Cruciferae）是一个包含多种重要经济作物的植物科，其典型特征是四片花瓣呈十字形排列，果实为角果。以下是常见的十字花科植物分类及举例：
     * <p>
     * ---
     * <p>
     * ### **1. 常见蔬菜类**
     * - **白菜类**
     * - **大白菜**（Brassica rapa subsp. pekinensis）
     * - **小白菜**（Brassica rapa subsp. chinensis）
     * - **油菜**（Brassica rapa var. oleifera，嫩叶可食用）
     * - **甘蓝类**
     * - **卷心菜/包菜**（Brassica oleracea var. capitata）
     * - **花椰菜**（Brassica oleracea var. botrytis）
     * - **西兰花**（Brassica oleracea var. italica）
     * - **羽衣甘蓝**（Brassica oleracea var. sabellica）
     * - **萝卜类**
     * - **白萝卜**（Raphanus sativus var. longipinnatus）
     * - **樱桃萝卜**（Raphanus sativus var. radicula）
     * - **其他**
     * - **芥菜**（Brassica juncea，包括雪里蕻、榨菜等）
     * - **芝麻菜**（Eruca sativa）
     * - **荠菜**（Capsella bursa-pastoris，野生常见野菜）
     * <p>
     * ---
     * <p>
     * ### **2. 油料作物**
     * - **油菜（菜籽油来源）**
     * - **欧洲油菜**（Brassica napus）
     * - **芥菜型油菜**（Brassica juncea）
     * <p>
     * ---
     * <p>
     * ### **3. 药用/香料植物**
     * - **板蓝根**（Isatis indigotica，根入药）
     * - **辣根**（Armoracia rusticana，根茎制调味料）
     * - **芥末**（Sinapis alba 或 Brassica nigra，种子制芥末酱）
     * <p>
     * ---
     * <p>
     * ### **4. 观赏花卉**
     * - **紫罗兰**（Matthiola incana）
     * - **香雪球**（Lobularia maritima）
     * - **桂竹香**（Cheiranthus cheiri）
     * <p>
     * ---
     * <p>
     * ### **特点补充**
     * 十字花科植物普遍含有硫代葡萄糖苷（glucosinolates），具有辛辣味和一定的抗氧化作用，但过量摄入可能影响甲状腺功能。许多种类是重要的经济作物，在农业和园艺中广泛栽培。
     */
    public static void streamChat() {
        ArkService service = ArkService.builder()
                .apiKey(API_KEY)
                // .baseUrl("https://ark.cn-beijing.volces.com/api/v3/chat/completions") // 如不填默认访问华北 2 (北京)
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(2)
                .build();
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);

        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(streamMessages)
                .build();

        service.streamChatCompletion(streamChatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .blockingForEach(
                        choice -> {
                            if (choice.getChoices().size() > 0) {
                                System.out.print(choice.getChoices().get(0).getMessage().getContent());
                            }
                        }
                );

        // shutdown service
        service.shutdownExecutor();
    }

    /**
     * 异步流式对话（将结果逐字打印），主线程未结束，结果已经打印
     */
    public static void asyncStreamChat() {
        ArkService service = ArkService.builder()
                .apiKey(API_KEY)
                // .baseUrl("https://ark.cn-beijing.volces.com/api/v3/chat/completions") // 如不填默认访问华北 2 (北京)
                .timeout(Duration.ofSeconds(120))
                .connectTimeout(Duration.ofSeconds(20))
                .retryTimes(2)
                .build();
        final List<ChatMessage> streamMessages = new ArrayList<>();
        final ChatMessage streamSystemMessage = ChatMessage.builder().role(ChatMessageRole.SYSTEM).content("你是豆包，是由字节跳动开发的 AI 人工智能助手").build();
        final ChatMessage streamUserMessage = ChatMessage.builder().role(ChatMessageRole.USER).content("常见的十字花科植物有哪些？").build();
        streamMessages.add(streamSystemMessage);
        streamMessages.add(streamUserMessage);
        ChatCompletionRequest streamChatCompletionRequest = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(streamMessages)
                .build();

        service.streamChatCompletion(streamChatCompletionRequest)
                .doOnError(Throwable::printStackTrace)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                        choice -> {
                            if (choice.getChoices().size() > 0) {
                                System.out.print(choice.getChoices().get(0).getMessage().getContent());
                            }
                        }
                );

        // just wait for result
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // shutdown service
        service.shutdownExecutor();
    }

    public static void main(String[] args) throws IOException {
        // 单轮对话
        // singleChat();
        // 多轮对话
        // multiChat();
        // 流式对话
        // streamChat();
        // 异步流式对话
        // asyncStreamChat();
        checkFile(SgsFileInfo.SYSTEM_CONTENT, "/Users/ty/Downloads/报告.docx");
    }
}
