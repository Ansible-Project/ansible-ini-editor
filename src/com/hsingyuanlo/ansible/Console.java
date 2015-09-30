package com.hsingyuanlo.ansible;

import com.hsingyuanlo.ansible.module.AnsibleIniUtil;

public class Console {
    public static void main (String[] args) {
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
            AnsibleIniUtil.selectFrontend2(inFilePath, outFilePath);
        } else if (service.equals("backend")) {
            AnsibleIniUtil.selectBackend2(inFilePath, outFilePath);
        } else if (service.equals("builder")) {
            AnsibleIniUtil.selectBuilder2(inFilePath, outFilePath);
        } else if (service.equals("database")) {
            AnsibleIniUtil.selectDatabase2(inFilePath, outFilePath);
        }
        System.out.println("["+service+"] "+outFilePath+" is generated !!");
    }
}
