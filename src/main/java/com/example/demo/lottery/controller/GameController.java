package com.example.demo.lottery.controller;

import com.example.demo.lottery.dao.UserSubmission;
import com.example.demo.lottery.dao.model.GameResult;
import com.example.demo.lottery.service.ContractDataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private ContractDataService contractDataService;

    // 重新加载合同数据（用于热更新）
    @PostMapping("/reload-contracts")
    public ResponseEntity<Map<String, Object>> reloadContracts() {
        try {
            boolean success = contractDataService.reloadContracts();
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            if (success) {
                response.put("message", "合同数据重新加载成功");
                response.put("total", contractDataService.getContractCount());
            } else {
                response.put("message", "合同数据重新加载失败");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "合同数据重新加载失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取可选择的合同列表
    @GetMapping("/contracts")
    public ResponseEntity<Map<String, Object>> getAvailableContracts() {
        Map<String, Object> response = new HashMap<>();
        response.put("total", contractDataService.getContractCount());
        response.put("contracts", contractDataService.getAllContractIds());
        return ResponseEntity.ok(response);
    }

    // 获取指定合同内容
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<Map<String, Object>> getContract(@PathVariable Integer contractId) {
        if (!contractDataService.contractExists(contractId)) {
            return ResponseEntity.badRequest().body(Map.of("error", "合同不存在"));
        }
        
        ContractData contract = contractDataService.getContractById(contractId);
        Map<String, Object> response = new HashMap<>();
        response.put("id", contractId);
        response.put("title", contract.getTitle());
        response.put("content", contract.getContent());
        response.put("totalErrors", contract.getErrorIndices().size());
        
        return ResponseEntity.ok(response);
    }

    // 提交答案并计算得分
    @PostMapping("/submit")
    public ResponseEntity<GameResult> submitAnswers(@RequestBody UserSubmission submission) {
        if (!contractDataService.contractExists(submission.getContractId())) {
            return ResponseEntity.badRequest().body(null);
        }
        
        ContractData contract = contractDataService.getContractById(submission.getContractId());
        
        // 计算正确答案数量
        long correctCount = submission.getSelectedIndices().stream()
            .filter(contract.getErrorIndices()::contains)
            .count();

        // 计算得分 (基于正确数量和剩余时间)
        int timeBonus = (int) (submission.getTimeLeft() / 2);
        int score = (int) Math.min(100, correctCount * 20 + timeBonus);

        // 返回结果
        GameResult result = new GameResult();
        result.setCorrectCount((int) correctCount);
        result.setTotalErrors(contract.getErrorIndices().size());
        result.setScore(score);
        result.setMessage(getResultMessage((int) correctCount, contract.getErrorIndices().size()));

        return ResponseEntity.ok(result);
    }

    // 获取合同内容 - 保持向后兼容
    @GetMapping("/contract")
    public ResponseEntity<List<String>> getContract() {
        // 默认返回第一套合同
        if (contractDataService.contractExists(1)) {
            return ResponseEntity.ok(contractDataService.getContractById(1).getContent());
        }
        return ResponseEntity.ok(List.of("合同数据未加载"));
    }

    private String getResultMessage(int correctCount, int totalErrors) {
        if (correctCount == totalErrors) {
            return "太棒了！你找到了所有错误！";
        } else if (correctCount >= totalErrors * 0.6) {
            return "不错，但还有改进空间！";
        } else {
            return "需要加强对相关法律的了解哦！";
        }
    }

    // 合同数据类
    public static class ContractData {
        private Integer id;
        private String title;
        private String description;
        private Integer totalErrors;
        private List<Integer> errorIndices;
        private List<String> content;
        private Map<String, String> errorExplanations;

        // 构造函数
        public ContractData() {}
        
        // 带参数的构造函数
        public ContractData(Integer id, String title, String description, Integer totalErrors, 
                          List<Integer> errorIndices, List<String> content, Map<String, String> errorExplanations) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.totalErrors = totalErrors;
            this.errorIndices = errorIndices;
            this.content = content;
            this.errorExplanations = errorExplanations;
        }

        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Integer getTotalErrors() { return totalErrors; }
        public void setTotalErrors(Integer totalErrors) { this.totalErrors = totalErrors; }

        public List<Integer> getErrorIndices() { return errorIndices; }
        public void setErrorIndices(List<Integer> errorIndices) { this.errorIndices = errorIndices; }

        public List<String> getContent() { return content; }
        public void setContent(List<String> content) { this.content = content; }

        public Map<String, String> getErrorExplanations() { return errorExplanations; }
        public void setErrorExplanations(Map<String, String> errorExplanations) { this.errorExplanations = errorExplanations; }
    }
}
