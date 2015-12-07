package idv.hsingyuanlo.ansible;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import idv.hsingyuanlo.ansible.core.AnsibleIni;
import idv.hsingyuanlo.ansible.core.AnsibleIniUtil;
import idv.hsingyuanlo.ansible.format.JsonUtil;

public class Console {
    public static void main (String[] args) {
        // Create configuration file
        demoCreateConfigFile();
        // Use configuration file to retrieve dynamic item
        demoRetrieveOpsItems();
        // 
        demoLoadOpsConfig();
    }
    
    public static void demoCreateConfigFile() {
        String iniFile = "hosts";
        String cfgFile = "hosts-config";
        
        File file = new File(cfgFile);
        if (file.exists()) {
            System.out.println(cfgFile+" exists !!!");
            return;
        }
        
        AnsibleIni ini = new AnsibleIni();
        ini.read(iniFile);
        
        AnsibleIniUtil.debug(ini);
        Set<String> sectionKeys = ini.getSectionKeys();
        for (String sectionKey : sectionKeys) {
            Set<String> sectionItemKeys = ini.getSectionItemKeys(sectionKey);
            for (String sectionItemKey : sectionItemKeys) {
                AnsibleIniUtil.modifySectionItem(ini, sectionKey, sectionItemKey, "true"); // Default true, update manually
            }
        }
        AnsibleIniUtil.debug(ini);
        
        // Write All config
        List<String> sectionNames = new ArrayList<String>();
        sectionNames.addAll(ini.getHostKeys());
        sectionNames.addAll(ini.getSectionKeys());
        ini.write(cfgFile, sectionNames);
    }
    
    public static void demoRetrieveOpsItems() {
        String iniFile = "hosts";
        String cfgFile = "hosts-config";
        String opsFile = "hosts-ops";
        
        File file = new File(opsFile);
        if (file.exists()) {
            System.out.println(opsFile+" exists !!!");
            return;
        }
        
        AnsibleIni ini = new AnsibleIni();
        ini.read(iniFile);
        AnsibleIni config = new AnsibleIni();
        config.read(cfgFile);
        
        AnsibleIniUtil.debug(ini);
        Set<String> sectionKeys = config.getSectionKeys();
        for (String sectionKey : sectionKeys) {
            Set<String> sectionItemKeys = config.getSectionItemKeys(sectionKey);
            for (String sectionItemKey : sectionItemKeys) {
                String flag = config.getSectionItem(sectionKey, sectionItemKey);
                if ("false".equals(flag)) {
                    AnsibleIniUtil.removeSectionItem(ini, sectionKey, sectionItemKey);
                }
            }
        }
        AnsibleIniUtil.debug(ini);
        
        List<String> sectionNames = new ArrayList<String>();
        sectionNames.addAll(ini.getHostKeys());
        sectionNames.addAll(ini.getSectionKeys());
        ini.write(opsFile, sectionNames);
    }
    
    public static void demoLoadOpsConfig() {
        AnsibleIni ini = new AnsibleIni();
        ini.read("hosts-ops");
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
}
