package idv.hsingyuanlo.ansible;

import java.util.Set;

import idv.hsingyuanlo.ansible.core.AnsibleIni;
import idv.hsingyuanlo.ansible.core.AnsibleIniUtil;
import idv.hsingyuanlo.ansible.format.JsonUtil;

public class Console {
    public static void main (String[] args) {
        demoOperateAnsibleIni();
    }
    
    public static void demoOperateAnsibleIni() {
        AnsibleIni ini = new AnsibleIni();
        ini.read("hosts");
        
        AnsibleIniUtil.debug(ini);
        
        Set<String> sectionKeys = ini.getSectionKeys();
        for (String sectionKey : sectionKeys) {
            Set<String> sectionItemKeys = ini.getSectionItemKeys(sectionKey);
            for (String sectionItemKey : sectionItemKeys) {
                AnsibleIniUtil.modifySectionItem(ini, sectionKey, sectionItemKey, "true");
            }
        }
        
        AnsibleIniUtil.debug(ini);
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
