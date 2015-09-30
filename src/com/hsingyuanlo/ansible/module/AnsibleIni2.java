package com.hsingyuanlo.ansible.module;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

/**
 * AnsibleIni basic information<br>
 * [Name]          This is a list section, we treat it as a list<br> 
 * [Name:children] This is a list section, we treat it as a list<br>
 * [Name:vars]     This is a map section, we treat it as a map<br>
 * <br>
 * Provide IO commands for ansible ini files<br>
 * 1. read  (file_full_path)<br>
 * 2. write (file_full_path)<br>
 * <br>
 * Provides updateHost command<br>
 * updateHost (section_key, list_of_hosts)<br>
 * <br>
 * Provides updateSection command handling 3 operations<br>
 * updateSection(operation)
 * 1. Add    (section_key, key, value)<br>
 * 2. Modify (section_key, key, value)<br>
 * 3. Remove (section_key, key)<br>
 * <br>
 * @author hylo
 *
 */
public class AnsibleIni2 {
    
    /** Interface Operation*/
    public static interface IAnsibleSectionOperation {
        void update(Map<String, Section> map);
    }
    
    /** Operation Add */
    public static class Add implements IAnsibleSectionOperation {
        private String mSectionKey;
        private String mKey;
        private String mValue;
        
        public Add(String sectionKey, String key, String value) {
            mSectionKey = sectionKey;
            mKey = key;
            mValue = value;
        }
        
        @Override
        public void update(Map<String, Section> map) {
            Section section = map.get(mSectionKey);
            if (section == null || mKey == null || mValue == null) {
                return;
            }
            
            if (section.containsKey(mKey)) {
                System.out.println("Key: "+mKey+" already exists, try to modify it");
                return;
            }
            section.put(mKey, mValue);
        }
    }
    
    /** Operation Modify */
    public static class Modify implements IAnsibleSectionOperation {
        private String mSectionKey;
        private String mKey;
        private String mValue;
        
        public Modify(String sectionKey, String key, String value) {
            mSectionKey = sectionKey;
            mKey = key;
            mValue = value;
        }
        
        @Override
        public void update(Map<String, Section> map) {
            Section section = map.get(mSectionKey);
            if (section == null || mKey == null || mValue == null) {
                return;
            }
            if (!section.containsKey(mKey)) {
                System.out.println("Key: "+mKey+" does bot exist, try to add it");
                return;
            }
            section.put(mKey, mValue);
        }
    }
    
    /** Operation Remove*/
    public static class Remove implements IAnsibleSectionOperation {
        private String mSectionKey;
        private String mKey;
        
        public Remove(String sectionKey, String key) {
            mSectionKey = sectionKey;
            mKey = key;
        }
        
        @Override
        public void update(Map<String, Section> map) {
            Section section = map.get(mSectionKey);
            if (section == null || mKey == null) {
                return;
            }
            section.remove(mKey);
        }
    }
    
    /** Rule for distinguishing map section and list section*/
    public static interface AnsibleIniRule {
        boolean isMapSection(String sectionKey);
    }
    
    /** Default rule*/
    public static class DefaultRule implements AnsibleIniRule {
        public static String SUFFIX_VARS = ":vars";
        public static String SUFFIX_CHILD = ":children";
        
        @Override
        public boolean isMapSection(String sectionKey) {
            if (sectionKey.endsWith(SUFFIX_VARS)) {
                return true;
            } else if (sectionKey.endsWith(SUFFIX_CHILD)) {
                return false;
            }
            return false;
        }
    }
    
    /** Define data structure*/
    private Map<String, List<String>> mHostMap = new HashMap<String, List<String>>();
    private Map<String, Section> mSectionMap = new HashMap<String, Section>();
    private AnsibleIniRule mRule = new DefaultRule();
    
    /**
     * 
     * @param inFilePath
     */
    public void read(String inFilePath) {
        read_inner(inFilePath);
    }
    
    /**
     * 
     * @param outFilePath
     * @param sections
     */
    public void write(String outFilePath, List<String> sections) {
        write_inner(outFilePath, sections);
    }
    
    /**
     * 
     * @param sectionKey
     * @param hosts
     */
    public void updateHost(String sectionKey, List<String> hosts) {
        mHostMap.remove(sectionKey);
        if (hosts != null) {
            mHostMap.put(sectionKey, hosts);
        }
    }
    
    /**
     * 
     * @param op
     */
    public void updateSection(IAnsibleSectionOperation op) {
        if (op == null) {
            return;
        }
        op.update(mSectionMap);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public List<String> getHosts(String sectionKey) {
        return mHostMap.get(sectionKey);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public Section getSection(String sectionKey) {
        return mSectionMap.get(sectionKey);
    }
    
    /**
     * 
     * @param sectionKey
     * @param key
     * @return
     */
    public String getSectionItem(String sectionKey, String key) {
        Section section = mSectionMap.get(sectionKey);
        if (section != null && section.containsKey(key)) {
            return section.get(key);
        }
        return null;
    }
    
    /**
     * Reader
     * @param inFilePath
     */
    private void read_inner(String inFilePath) {
        mHostMap.clear();
        mSectionMap.clear();
        
        Map<String, String> blockMap = new HashMap<String, String>();
        BufferedReader bufReader = null;
        try {
            String line = null;
            String section = null;
            String value = null;
            String pattern = "^ *\\[.*\\] *$";
            
            bufReader = new BufferedReader(new FileReader(inFilePath));
            while ((line = bufReader.readLine()) != null) {
                
                if (line.matches(pattern)) {
                    if (section != null && value != null) {
                        blockMap.put(section, value);
                    }
                    section = line.substring(line.indexOf("[")+1, line.lastIndexOf("]")).trim();
                    value = line.trim();
                    value += "\n";
                } else {
                    if (value != null) {
                        value += line;
                        value += "\n";
                    }
                }
            }
            
            if (section != null && value != null) {
                blockMap.put(section, value);
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            blockMap.clear();
        } catch (IOException e) {
            e.printStackTrace();
            blockMap.clear();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        for (String key : blockMap.keySet()) {
            if (mRule.isMapSection(key)) {
                Section section = parseStringToSection(key, blockMap.get(key));
                if (section != null) {
                    mSectionMap.put(key, section);
                }
            } else {
                List<String> list = parseStringToList(key, blockMap.get(key));
                mHostMap.put(key, list);
            }
        }
    }
    
    /**
     * Parser
     * @param key
     * @param block
     * @return
     */
    private List<String> parseStringToList(String key, String block) {
        List<String> list = new ArrayList<String>();
        
        String pattern = "^\\[ *"+key+" *\\]$";
        StringTokenizer tokenizer = new StringTokenizer(block, "\n");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (!token.matches(pattern)) {
                list.add(token);
            }
        }
        return list;
    }
    
    /**
     * Parser
     * @param key
     * @param block
     * @return
     */
    private Section parseStringToSection(String key, String block) {
        InputStream is = new ByteArrayInputStream(block.getBytes());
        
        try {
            Ini ini = new Ini(is);
            Ini.Section section = ini.get(key);
            return section;
        } catch (InvalidFileFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Writer
     * @param outFilePath
     * @param sections
     */
    private void write_inner(String outFilePath, List<String> sections) {
        BufferedWriter bufWriter = null;
        try {
            bufWriter = new BufferedWriter(new FileWriter(outFilePath));
            for (String key : sections) {
                if (mRule.isMapSection(key)) {
                    Section section = mSectionMap.get(key);
                    if (section != null) {
                        bufWriter.write("["+key+"]\n");
                        for (String sectionKey : section.keySet()) {
                            bufWriter.write(sectionKey+"="+section.get(sectionKey)+"\n");
                        }
                        bufWriter.write("\n");
                    }
                } else {
                    List<String> list = mHostMap.get(key);
                    if (list != null) {
                        bufWriter.write("["+key+"]\n");
                        for (String host : list) {
                            bufWriter.write(""+host+"\n");
                        }
                        bufWriter.write("\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}