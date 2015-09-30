package com.hsingyuanlo.ansible.module;

import java.util.ArrayList;
import java.util.List;

import com.hsingyuanlo.ansible.module.AnsibleIni.SectionOp;

public class AnsibleIniUtil {
    
    public static void selectFrontend(String inFilePath, String outFilePath){
        AnsibleIni ini = new AnsibleIni();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(SectionOp.Remove, "frontend:vars", "ansible_ssh_private_key_file", null);
        ini.updateSection(SectionOp.Add, "frontend:vars", "ansible_connection", "local");
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("frontend");
        sections.add("frontend:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectBackend(String inFilePath, String outFilePath){
        AnsibleIni ini = new AnsibleIni();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(SectionOp.Remove, "backend:vars", "ansible_ssh_private_key_file", null);
        ini.updateSection(SectionOp.Add, "backend:vars", "ansible_connection", "local");
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("backend");
        sections.add("backend:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectBuilder(String inFilePath, String outFilePath){
        AnsibleIni ini = new AnsibleIni();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(SectionOp.Remove, "builder:vars", "ansible_ssh_private_key_file", null);
        ini.updateSection(SectionOp.Add, "builder:vars", "ansible_connection", "local");
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("builder");
        sections.add("builder:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectDatabase(String inFilePath, String outFilePath){
        AnsibleIni ini = new AnsibleIni();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(SectionOp.Remove, "database:vars", "ansible_ssh_private_key_file", null);
        ini.updateSection(SectionOp.Add, "database:vars", "ansible_connection", "local");
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("database");
        sections.add("database:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectFrontend2(String inFilePath, String outFilePath){
        AnsibleIni2 ini = new AnsibleIni2();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(new AnsibleIni2.Remove("frontend:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni2.Add("frontend:vars", "ansible_connection", "local"));
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("frontend");
        sections.add("frontend:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectBackend2(String inFilePath, String outFilePath){
        AnsibleIni2 ini = new AnsibleIni2();
        // Read
        ini.read(inFilePath);
        // Update section
        ini.updateSection(new AnsibleIni2.Remove("backend:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni2.Add("backend:vars", "ansible_connection", "local"));
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("backend");
        sections.add("backend:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectBuilder2(String inFilePath, String outFilePath){
        AnsibleIni2 ini = new AnsibleIni2();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(new AnsibleIni2.Remove("builder:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni2.Add("builder:vars", "ansible_connection", "local"));
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("builder");
        sections.add("builder:vars");
        ini.write(outFilePath, sections);
    }
    
    public static void selectDatabase2(String inFilePath, String outFilePath){
        AnsibleIni2 ini = new AnsibleIni2();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(new AnsibleIni2.Remove("datebase:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni2.Add("database:vars", "ansible_connection", "local"));
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("database");
        sections.add("database:vars");
        ini.write(outFilePath, sections);
    }
}

