package com.example.demo.lottery;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonToExcel {

    public static void main(String[] args) {
        String inputFilePath = "D:\\Users\\long_w\\Desktop\\场景信息0807生产.txt"; // JSON文件路径

        try {
            // 读取JSON文件内容
            String jsonString = new String(Files.readAllBytes(Paths.get(inputFilePath)), "UTF-8");

            // 解析JSON数据
            JSONArray jsonArray = new JSONArray(jsonString);
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Scenes");

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
//            String[] headers = {"sceneId",
//                "name",
//                "parentId",
//                "status",
//                "fixedRole",
//                "sceneRuleId",
//                "resourceType",
//                "targetTime",
//                "workStartTime",
//                "workEndTime",
//                "externalId",
//                "是否为3三级场景id"};

            String[] headers = {
                "sceneId",
                "name",
                "parentId",
                "status",
                "fixedRole",
                "sceneRuleId",
                "resourceType",
                "targetTime",
                "workStartTime",
                "workEndTime",
                "externalId",
                "是否为3三级场景id",
                "二级场景id",
                "二级场景名称",
                "一级场景id",
                "一级场景名称"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                rowNum = writeJsonObjectToSheet(sheet, jsonObject, rowNum);
//            }

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                rowNum = writeJsonObjectToSheet(sheet, jsonObject, rowNum, null, null);
            }

            // 获取输出文件路径
            File inputFile = new File(inputFilePath);
            String outputFilePath = inputFile.getParent() + File.separator + "场景信息生产_20250807.xlsx";

            // 保存Excel文件
            try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Excel文件已保存到: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static int writeJsonObjectToSheet(Sheet sheet, JSONObject jsonObject, int rowNum) {
//        Row row = sheet.createRow(rowNum++);
//        String[] keys = {"sceneId", "name", "parentId", "status", "fixedRole", "sceneRuleId", "resourceType", "targetTime", "workStartTime", "workEndTime", "externalId"};
//
//        for (int i = 0; i < keys.length; i++) {
//            Cell cell = row.createCell(i);
//            cell.setCellValue(jsonObject.optString(keys[i], ""));
//        }
//
//        // 检查是否为三级场景ID
//        boolean isThirdLevelSceneId = jsonObject.has("subConsultSceneInfoList") && jsonObject.getJSONArray("subConsultSceneInfoList").isEmpty();
//        Cell thirdLevelCell = row.createCell(keys.length);
//        thirdLevelCell.setCellValue(isThirdLevelSceneId ? "是" : "否");
//
//        if (jsonObject.has("subConsultSceneInfoList")) {
//            JSONArray subList = jsonObject.getJSONArray("subConsultSceneInfoList");
//            for (int i = 0; i < subList.length(); i++) {
//                rowNum = writeJsonObjectToSheet(sheet, subList.getJSONObject(i), rowNum);
//            }
//        }
//
//        return rowNum;
//    }

    private static int writeJsonObjectToSheet(Sheet sheet, JSONObject jsonObject, int rowNum, JSONObject parentScene, JSONObject grandParentScene) {
        Row row = sheet.createRow(rowNum++);
        String[] keys = {"sceneId", "name", "parentId", "status", "fixedRole", "sceneRuleId", "resourceType", "targetTime", "workStartTime", "workEndTime", "externalId"};

        // 写入基础字段
        for (int i = 0; i < keys.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(jsonObject.optString(keys[i], ""));
        }

        // 检查是否为三级场景ID
        boolean isThirdLevelSceneId = jsonObject.has("subConsultSceneInfoList") && jsonObject.getJSONArray("subConsultSceneInfoList").isEmpty();
        Cell thirdLevelCell = row.createCell(keys.length);
        thirdLevelCell.setCellValue(isThirdLevelSceneId ? "是" : "否");

        // 写入二级场景和一级场景信息
        String secondLevelId = parentScene != null ? parentScene.optString("sceneId", "") : "";
        String secondLevelName = parentScene != null ? parentScene.optString("name", "") : "";
        String firstLevelId = grandParentScene != null ? grandParentScene.optString("sceneId", "") : "";
        String firstLevelName = grandParentScene != null ? grandParentScene.optString("name", "") : "";

        Cell secondLevelIdCell = row.createCell(keys.length + 1);
        secondLevelIdCell.setCellValue(secondLevelId);

        Cell secondLevelNameCell = row.createCell(keys.length + 2);
        secondLevelNameCell.setCellValue(secondLevelName);

        Cell firstLevelIdCell = row.createCell(keys.length + 3);
        firstLevelIdCell.setCellValue(firstLevelId);

        Cell firstLevelNameCell = row.createCell(keys.length + 4);
        firstLevelNameCell.setCellValue(firstLevelName);

        // 递归处理子场景
        if (jsonObject.has("subConsultSceneInfoList")) {
            JSONArray subList = jsonObject.getJSONArray("subConsultSceneInfoList");
            for (int i = 0; i < subList.length(); i++) {
                rowNum = writeJsonObjectToSheet(sheet, subList.getJSONObject(i), rowNum, jsonObject, parentScene);
            }
        }

        return rowNum;
    }

}
