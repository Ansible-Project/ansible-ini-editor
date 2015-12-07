package idv.hsingyuanlo.ansible.core;

import java.util.Set;

import idv.hsingyuanlo.ansible.core.AnsibleIni.Add;
import idv.hsingyuanlo.ansible.core.AnsibleIni.Modify;
import idv.hsingyuanlo.ansible.core.AnsibleIni.Remove;

public class AnsibleIniUtil {
    
    private AnsibleIniUtil() {
        
    }
    
    /**
     * 
     * @param section
     * @param item
     * @param value
     */
    public static void addSectionItem(AnsibleIni ini, String section, String item, String value) {
        ini.updateSection(new Add(section, item, value));
    }
    
    /**
     * 
     * @param section
     * @param item
     * @param value
     */
    public static void modifySectionItem(AnsibleIni ini, String section, String item, String value) {
        ini.updateSection(new Modify(section, item, value));
    }
    
    /**
     * 
     * @param section
     * @param item
     */
    public static void removeSectionItem(AnsibleIni ini, String section, String item) {
        ini.updateSection(new Remove(section, item));
    }
    
    /**
     * 
     * @param ini
     */
    public static void debug(AnsibleIni ini) {
        System.out.println("=================");
        Set<String> sectionKeys = ini.getSectionKeys();
        for (String sectionKey : sectionKeys) {
            System.out.println(">>> "+sectionKey);
            Set<String> sectionItemKeys = ini.getSectionItemKeys(sectionKey);
            for (String sectionItemKey: sectionItemKeys) {
                System.out.println(sectionItemKey+" => "+ini.getSectionItem(sectionKey, sectionItemKey));
            }
        }
    }
}

