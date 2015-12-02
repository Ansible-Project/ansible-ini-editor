package com.hsingyuanlo.ansible;

import java.util.ArrayList;
import java.util.List;

import com.hsingyuanlo.ansible.core.AnsibleIni;

public class AnsibleIniUtil {
    
    public static void selectFrontend(String inFilePath, String outFilePath){
        AnsibleIni ini = new AnsibleIni();
        // Read
        ini.read(inFilePath);
        // Update
        ini.updateSection(new AnsibleIni.Remove("frontend:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni.Add("frontend:vars", "ansible_connection", "local"));
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
        // Update section
        ini.updateSection(new AnsibleIni.Remove("backend:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni.Add("backend:vars", "ansible_connection", "local"));
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
        ini.updateSection(new AnsibleIni.Remove("builder:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni.Add("builder:vars", "ansible_connection", "local"));
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
        ini.updateSection(new AnsibleIni.Remove("datebase:vars", "ansible_ssh_private_key_file"));
        ini.updateSection(new AnsibleIni.Add("database:vars", "ansible_connection", "local"));
        // Write
        List<String> sections = new ArrayList<String>();
        sections.add("database");
        sections.add("database:vars");
        ini.write(outFilePath, sections);
    }
}

