package com.example.demo.lottery.controller;

import com.example.demo.lottery.dao.UserSubmission;
import com.example.demo.lottery.dao.model.GameResult;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    // 存储合同错误信息 - 增加到5个错误
    private static final List<Integer> ERROR_INDICES = Arrays.asList(6, 10, 14, 18, 20);

    // 提交答案并计算得分
    @PostMapping("/submit")
    public ResponseEntity<GameResult> submitAnswers(@RequestBody UserSubmission submission) {
        // 计算正确答案数量
        long correctCount = submission.getSelectedIndices().stream()
            .filter(ERROR_INDICES::contains)
            .count();

        // 计算得分 (基于正确数量和剩余时间)
        int timeBonus = (int) (submission.getTimeLeft() / 2);
        int score = (int) Math.min(100, correctCount * 20 + timeBonus);

        // 返回结果
        GameResult result = new GameResult();
        result.setCorrectCount((int) correctCount);
        result.setTotalErrors(ERROR_INDICES.size());
        result.setScore(score);
        result.setMessage(getResultMessage((int) correctCount));

        return ResponseEntity.ok(result);
    }

    // 获取合同内容 - 增加更多错误描述
    @GetMapping("/contract")
    public ResponseEntity<List<String>> getContract() {
        List<String> contract = Arrays.asList(
            "甲方：______",
            "乙方：______",
            "身份证号码：______",
            "根据《中华人民共和国劳动法》，经甲乙双方平等协商同意，自愿签订本合同，共同遵守本合同所列条项。", // 正确
            "一、劳动合同期限",
            "第一条 本合同期限类型为______期限合同。", // 正确
            "本合同生效日期：______年______月______日，终止日期：______年______月______日，其中试用期为______。", // 错误1: 试用期超过法定最长期限
            "二、工作内容和义务",
            "第二条 乙方同意根据甲方工作需要，担任______岗位工作。甲方可依照有关规定，经与乙方协商，对乙方的工作职务和岗位进行调整。", // 正确
            "第三条 乙方应按照甲方的要求，按时完成规定的工作数量，达到规定的质量标准，并履行下列义务：", // 正确
            "1. 遵守国家宪法、法律、法规；", // 错误2: 缺少"及行政法规"
            "2. 遵守甲方的规章制度；", // 错误3: 应当是"依法制定的"
            "3. 维护甲方的荣誉和利益；", // 正确
            "4. 忠于职守，勤奋工作；", // 正确
            "5. 履行保守甲方商业秘密，不得利用甲方的商业秘密为本人或其他经济组织和个人谋取不正当的经济利益。", // 错误4: 缺少"义务"
            "三、劳动保护和劳动条件",
            "第四条 甲方安排乙方每日工作时间不超过八小时，平均每周不超过四十小时。甲方由于工作需要，经与工会和乙方协商后可以延长工作时间的，一般每日不得超过一小时，因特殊原因需要延长工作时间的，在保障乙方身体健康条件下延长工作时间，每日不得超过三个小时，每月不得超过三十六小时。", // 正确
            "执行综合计算工时制度的，平均日和周工作时间不超过标准工作时间。", // 正确
            "执行不定时工时制度的，工作和休息休假乙方自行安排。", // 错误5: 需要经过劳动部门批准
            "甲方安排乙方执行______工时制度。", // 正确
            "第五条 甲方延长乙方工作时间，应安排乙方同等时间偶尔或依法支付加班加点工资。" // 错误6: "偶尔"是错误表述
        );

        return ResponseEntity.ok(contract);
    }

    private String getResultMessage(int correctCount) {
        if (correctCount == 5) {
            return "太棒了！你找到了所有错误！";
        } else if (correctCount >= 3) {
            return "不错，但还有改进空间！";
        } else {
            return "需要加强对劳动法的了解哦！";
        }
    }
}
