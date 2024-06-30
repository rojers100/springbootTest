package com.luojie.controller;

import com.luojie.moudle.RojerCSVModel;
import com.luojie.util.CsvUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CSVTestController {

    @GetMapping("/csv/test")
    public void testCsv() throws Exception {
        RojerCSVModel csvModel1 = new RojerCSVModel("John", "30", "New York");
        RojerCSVModel csvModel2 = new RojerCSVModel("Alice", "25", "London");
        RojerCSVModel csvModel3 = new RojerCSVModel("Rojer", "27", "Shang Hai");
        List<RojerCSVModel> list = new ArrayList<>();
        list.add(csvModel1);
        list.add(csvModel2);
        list.add(csvModel3);
        // 测试写一个的情况
        CsvUtil.writeToFile("D:\\tmp\\test1.csv", csvModel1);
        CsvUtil.writeToFile("D:\\tmp\\test1.csv",list);
    }

    @GetMapping("/csv/test/get")
    public void testCsvGet() throws Exception {
        List<RojerCSVModel> list = new ArrayList<>();
        list = CsvUtil.CSVToBean("D:\\tmp\\test1.csv", RojerCSVModel.class, 0);
        System.out.println(list.size());
    }
}
