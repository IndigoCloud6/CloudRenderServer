package com.xudri.cloudrenderserver.util;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProcessManagerByPoweShell
 * @Description TODO
 * @Author MaxYun
 * @Date 2024/5/14 8:59
 * @Version 1.0
 */
public class ProcessManagerByPoweShell {

    public static String killProcessListByPid(String pid) {
        String command = "Stop-Process -Id "+pid+" -Force";
        return executeCommand(command);
    }


    public static void queryProcessListByName(String processName) {
        String command = "Get-WmiObject Win32_Process | " + "Where-Object { $_.Name -like '"
                + processName + "*' } |" + "Select-Object ProcessId, CommandLine, Name | "
                + "ConvertTo-Json -Depth 10";
        String result = executeCommand(command);
        System.out.println(result);
    }

    public static String queryProcessListByCmdKey(String cmdKey) {
        String command = "Get-WmiObject Win32_Process | " + "Where-Object { $_.CommandLine -match  '"
                + cmdKey + "' } |" + "Select-Object ProcessId,Name | "
                + "ConvertTo-Json -Depth 10";
        return executeCommand(command);
    }

    public static JSONArray queryProcessListByCmdKeyAndProcessName(String processName, String cmdKey) {
        String command = "Get-WmiObject Win32_Process | " + "Where-Object { $_.CommandLine -match  '"
                + cmdKey + "' -and $_.Name -like '" + processName + "*'} |" + "Select-Object ProcessId,Name | "
                + "ConvertTo-Json -Depth 10";
        String result = executeCommand(command);
        Object resultObject = JSON.parse(result);
        JSONArray returnArray = new JSONArray();
        if (resultObject instanceof JSONObject) {
            returnArray.add(resultObject);
        }
        if (resultObject instanceof JSONArray) {
            returnArray.addAll((JSONArray) resultObject);
        }
        return returnArray;
    }

    public static String executeCommand(String command) {
        // 创建执行PowerShell命令的列表
        List<String> commands = new ArrayList<>();
        commands.add("powershell.exe");
        commands.add("-Command");
        commands.add(command); // 添加完整的PowerShell命令
        // 使用ProcessBuilder来启动进程
        try {
            ProcessBuilder builder = new ProcessBuilder(commands);
            builder.redirectErrorStream(true); // 将错误输出和标准输出合并
            Process process = builder.start(); // 启动Process实例
            // 读取命令的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = "";
            String line;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
            process.waitFor(); // 等待进程结束并获取退出代码
            return result;
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {
        JSONArray processList = ProcessManagerByPoweShell.queryProcessListByCmdKeyAndProcessName("XDTC","664339b4cc843b99adc672ff");
        System.out.println(processList);
        for (int i = 0; i < processList.size(); i++) {
            JSONObject process = processList.getJSONObject(i);
            String processId = process.getString("ProcessId");
            ProcessManagerByPoweShell.killProcessListByPid(processId);
        }
    }
}
