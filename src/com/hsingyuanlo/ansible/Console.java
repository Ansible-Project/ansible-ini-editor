package com.hsingyuanlo.ansible;

import com.hsingyuanlo.ansible.format.JsonUtil;

public class Console {
    public static void main (String[] args) {
        demoJsonConversion();
    }
    
    public static void demoJsonConversion() {
        // Json file => AnsibleIni string
        System.out.println("==================");
        String ini1 = JsonUtil.convertJsonFileToAnsibleIniString("hosts.json");
        System.out.println(ini1);
        
        // AnsibleIni file => Json string
        System.out.println("==================");
        String json1 = JsonUtil.convertAnsibleIniFileToJsonString("hosts");
        System.out.println(json1);
        
        // Json string => AnsibleIni string
        System.out.println("==================");
        String ini2 = JsonUtil.convertJsonStringToAnsibleIniString(json1);
        System.out.println(ini2);
        
        // AnsibleIni string => Json string
        System.out.println("==================");
        String json2 = JsonUtil.convertAnsibleIniStringToJsonString(ini1);
        System.out.println(json2);
    }
    
    public static void demoModifyAndSplitAnsibleIni (String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java -jar ansible.jar [service] [input] [output]");
            return;
        }
        String service = args[0];
        String inFilePath = args[1];
        String outFilePath = args[2];
        
        if (inFilePath.equals(outFilePath)) {
            System.out.println("Error: input and output are the same file");
            return;
        }
        
        if (service.equals("frontend")) {
            AnsibleIniUtil.selectFrontend(inFilePath, outFilePath);
        } else if (service.equals("backend")) {
            AnsibleIniUtil.selectBackend(inFilePath, outFilePath);
        } else if (service.equals("builder")) {
            AnsibleIniUtil.selectBuilder(inFilePath, outFilePath);
        } else if (service.equals("database")) {
            AnsibleIniUtil.selectDatabase(inFilePath, outFilePath);
        }
        System.out.println("["+service+"] "+outFilePath+" is generated !!");
        
    }
}
