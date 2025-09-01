package com.example.demo.lottery.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.lottery.controller.GameController.ContractData;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class ContractDataService {
    
    private static final Map<Integer, ContractData> CONTRACTS = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @PostConstruct
    public void init() {
        loadContractsFromFiles();
    }
    
    /**
     * 从文件加载合同数据
     */
    public void loadContractsFromFiles() {
        try {
            // 清空现有数据
            CONTRACTS.clear();
            
            // 加载所有合同文件
            String[] contractFiles = {
                "contracts/labor-contract.json",
                "contracts/housing-lease-contract.json", 
                "contracts/purchase-sale-contract.json",
                "contracts/tech-development-contract.json",
                "contracts/service-contract.json"
            };
            
            for (String filePath : contractFiles) {
                try (InputStream inputStream = new ClassPathResource(filePath).getInputStream()) {
                    ContractData contract = objectMapper.readValue(inputStream, ContractData.class);
                    CONTRACTS.put(contract.getId(), contract);
                } catch (IOException e) {
                    System.err.println("加载合同文件失败: " + filePath + ", 错误: " + e.getMessage());
                }
            }
            
            System.out.println("成功加载 " + CONTRACTS.size() + " 个合同文件");
            
        } catch (Exception e) {
            System.err.println("加载合同文件时发生错误: " + e.getMessage());
            // 如果文件加载失败，使用默认数据
            loadDefaultContracts();
        }
    }
    
    /**
     * 加载默认合同数据（备用方案）
     */
    private void loadDefaultContracts() {
        System.out.println("使用默认合同数据");
        
        // 第一套合同：劳动合同
        CONTRACTS.put(1, new ContractData(
            1, "劳动合同", "根据《中华人民共和国劳动法》制定的标准劳动合同", 5,
            Arrays.asList(6, 10, 14, 18, 20),
            Arrays.asList(
                "甲方：______",
                "乙方：______",
                "身份证号码：______",
                "根据《中华人民共和国劳动法》，经甲乙双方平等协商同意，自愿签订本合同，共同遵守本合同所列条项。",
                "一、劳动合同期限",
                "第一条 本合同期限类型为______期限合同。",
                "本合同生效日期：______年______月______日，终止日期：______年______月______日，其中试用期为______。",
                "二、工作内容和义务",
                "第二条 乙方同意根据甲方工作需要，担任______岗位工作。甲方可依照有关规定，经与乙方协商，对乙方的工作职务和岗位进行调整。",
                "第三条 乙方应按照甲方的要求，按时完成规定的工作数量，达到规定的质量标准，并履行下列义务：",
                "1. 遵守国家宪法、法律、法规；",
                "2. 遵守甲方的规章制度；",
                "3. 维护甲方的荣誉和利益；",
                "4. 忠于职守，勤奋工作；",
                "5. 履行保守甲方商业秘密，不得利用甲方的商业秘密为本人或其他经济组织和个人谋取不正当的经济利益。",
                "三、劳动保护和劳动条件",
                "第四条 甲方安排乙方每日工作时间不超过八小时，平均每周不超过四十小时。甲方由于工作需要，经与工会和乙方协商后可以延长工作时间的，一般每日不得超过一小时，因特殊原因需要延长工作时间的，在保障乙方身体健康条件下延长工作时间，每日不得超过三个小时，每月不得超过三十六小时。",
                "执行综合计算工时制度的，平均日和周工作时间不超过标准工作时间。",
                "执行不定时工时制度的，工作和休息休假乙方自行安排。",
                "甲方安排乙方执行______工时制度。",
                "第五条 甲方延长乙方工作时间，应安排乙方同等时间偶尔或依法支付加班加点工资。"
            ),
            Map.of(
                "6", "试用期超过法定最长期限",
                "10", "缺少'及行政法规'",
                "14", "应当是'依法制定的'",
                "18", "缺少'义务'",
                "20", "需要经过劳动部门批准"
            )
        ));

        // 第二套合同：房屋租赁合同
        CONTRACTS.put(2, new ContractData(
            2, "房屋租赁合同", "标准房屋租赁合同模板", 5,
            Arrays.asList(5, 12, 16, 19, 22),
            Arrays.asList(
                "出租方（甲方）：______",
                "承租方（乙方）：______",
                "房屋地址：______",
                "房屋面积：______平方米",
                "租赁期限：______年，自______年______月______日起至______年______月______日止。",
                "一、房屋用途",
                "乙方承租该房屋用于居住，不得用于商业经营。",
                "二、租金及支付方式",
                "月租金：______元",
                "支付方式：按月支付",
                "三、押金",
                "押金：______元，租赁期满后无息退还。",
                "四、房屋维修",
                "房屋主体结构维修由甲方负责，日常维修由乙方负责。",
                "五、违约责任",
                "任何一方违约，应向对方支付违约金______元。",
                "六、合同解除",
                "租赁期间，任何一方不得无故解除合同。",
                "七、争议解决",
                "发生争议时，双方应友好协商解决。",
                "八、其他约定",
                "本合同未尽事宜，双方可另行协商。",
                "九、合同生效",
                "本合同自双方签字之日起生效。",
                "十、补充条款",
                "本合同一式两份，甲乙双方各执一份。",
                "甲方签字：______",
                "乙方签字：______",
                "签订日期：______年______月______日"
            ),
            Map.of(
                "5", "租赁期限超过20年",
                "12", "押金超过月租金3倍",
                "16", "违约金过高",
                "19", "缺少书面形式要求",
                "22", "缺少公证要求"
            )
        ));

        // 第三套合同：购销合同
        CONTRACTS.put(3, new ContractData(
            3, "购销合同", "标准购销合同模板", 5,
            Arrays.asList(4, 9, 15, 18, 21),
            Arrays.asList(
                "供方（甲方）：______",
                "需方（乙方）：______",
                "签订地点：______",
                "签订时间：______年______月______日",
                "一、产品名称、规格、数量、单价",
                "产品名称：______",
                "规格型号：______",
                "数量：______件",
                "单价：______元/件",
                "二、质量标准",
                "产品质量按国家标准执行。",
                "三、交货方式",
                "交货地点：______",
                "交货时间：______年______月______日前",
                "四、运输方式",
                "运输方式：______",
                "运费承担：______",
                "五、验收标准",
                "验收标准：按合同约定执行。",
                "六、付款方式",
                "付款方式：______",
                "付款时间：______",
                "七、违约责任",
                "违约方承担违约责任。",
                "八、争议解决",
                "发生争议时，双方协商解决。",
                "九、合同生效",
                "本合同自双方签字之日起生效。",
                "十、其他约定",
                "本合同未尽事宜，双方另行协商。",
                "甲方（盖章）：______",
                "乙方（盖章）：______"
            ),
            Map.of(
                "4", "缺少具体时间",
                "9", "缺少货币单位",
                "15", "运费承担不明确",
                "18", "付款时间不明确",
                "21", "缺少盖章要求"
            )
        ));

        // 第四套合同：技术开发合同
        CONTRACTS.put(4, new ContractData(
            4, "技术开发合同", "技术开发项目合同模板", 5,
            Arrays.asList(7, 13, 17, 20, 24),
            Arrays.asList(
                "委托方（甲方）：______",
                "开发方（乙方）：______",
                "项目名称：______",
                "签订地点：______",
                "签订时间：______年______月______日",
                "一、项目内容",
                "开发内容：______",
                "二、技术指标",
                "技术指标：______",
                "三、开发进度",
                "开发周期：______个月",
                "四、费用及支付",
                "开发费用：______元",
                "支付方式：______",
                "五、知识产权",
                "知识产权归属：______",
                "六、保密条款",
                "保密期限：______年",
                "七、验收标准",
                "验收标准：______",
                "八、违约责任",
                "违约责任：______",
                "九、争议解决",
                "争议解决方式：______",
                "十、合同生效",
                "本合同自双方签字之日起生效。",
                "十一、其他约定",
                "本合同未尽事宜，双方另行协商。",
                "十二、附件",
                "本合同附件：______",
                "十三、合同份数",
                "本合同一式两份，双方各执一份。",
                "十四、签字盖章",
                "甲方（盖章）：______",
                "乙方（盖章）：______",
                "签订日期：______年______月______日"
            ),
            Map.of(
                "7", "开发内容描述不具体",
                "13", "知识产权归属不明确",
                "17", "违约责任不具体",
                "20", "附件清单不完整",
                "24", "缺少法定代表人签字"
            )
        ));

        // 第五套合同：服务合同
        CONTRACTS.put(5, new ContractData(
            5, "服务合同", "服务项目合同模板", 5,
            Arrays.asList(6, 11, 16, 19, 23),
            Arrays.asList(
                "服务方（甲方）：______",
                "委托方（乙方）：______",
                "服务项目：______",
                "签订地点：______",
                "签订时间：______年______月______日",
                "一、服务内容",
                "服务内容：______",
                "二、服务标准",
                "服务标准：______",
                "三、服务期限",
                "服务期限：______个月",
                "四、服务费用",
                "服务费用：______元",
                "五、支付方式",
                "支付方式：______",
                "六、服务地点",
                "服务地点：______",
                "七、服务质量",
                "服务质量要求：______",
                "八、违约责任",
                "违约责任：______",
                "九、争议解决",
                "争议解决方式：______",
                "十、合同变更",
                "合同变更：______",
                "十一、合同解除",
                "合同解除条件：______",
                "十二、保密条款",
                "保密期限：______年",
                "十三、其他约定",
                "其他约定：______",
                "十四、合同生效",
                "本合同自双方签字之日起生效。",
                "十五、合同份数",
                "本合同一式两份，双方各执一份。",
                "十六、签字盖章",
                "甲方（盖章）：______",
                "乙方（盖章）：______",
                "签订日期：______年______月______日"
            ),
            Map.of(
                "6", "服务内容描述不详细",
                "11", "支付方式不明确",
                "16", "违约责任不具体",
                "19", "解除条件不明确",
                "23", "缺少联系方式"
            )
        ));
        
        System.out.println("默认合同数据加载完成，共 " + CONTRACTS.size() + " 个合同");
    }
    
    /**
     * 重新加载合同数据
     */
    public boolean reloadContracts() {
        try {
            loadContractsFromFiles();
            return true;
        } catch (Exception e) {
            System.err.println("重新加载合同数据失败: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取所有合同ID
     */
    public List<Integer> getAllContractIds() {
        return List.copyOf(CONTRACTS.keySet());
    }
    
    /**
     * 根据ID获取合同
     */
    public ContractData getContractById(Integer id) {
        return CONTRACTS.get(id);
    }
    
    /**
     * 获取合同总数
     */
    public int getContractCount() {
        return CONTRACTS.size();
    }
    
    /**
     * 检查合同是否存在
     */
    public boolean contractExists(Integer id) {
        return CONTRACTS.containsKey(id);
    }
}
