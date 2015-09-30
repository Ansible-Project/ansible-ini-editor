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
 * Handle INI schema for ansible hosts file
 * @author hylo
 *
 */
@Deprecated
public class AnsibleIni {
    
    public static String SUFFIX_VARS = ":vars";
    public static String SUFFIX_CHILD = ":children";    // not used
    
    public static enum SectionOp {
        Add,
        Remove,
        Modify
    }
    
    protected Map<String, List<String>> mHostMap;
    protected Map<String, Section> mSectionMap;
    
    public AnsibleIni() {
        mHostMap = new HashMap<String, List<String>>();
        mSectionMap = new HashMap<String, Section>();
    }
    
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
     * @param sectionKey
     * @param key
     * @param value
     */
    public void updateSection(SectionOp op, String sectionKey, String key, String value) {
        if (op == null) {
            return;
        }
        
        Section section = mSectionMap.get(sectionKey);
        if (section == null || key == null) {
            return;
        }
        
        if (SectionOp.Add.equals(op)) {
            if (section.containsKey(key)) {
                System.out.println("Key: "+key+" already exists, try to modify it");
                return;
            }
            if (value != null) {
                section.put(key, value);
            }
        } else if (SectionOp.Modify.equals(op)) {
            if (!section.containsKey(key)) {
                System.out.println("Key: "+key+" does bot exist, try to add it");
                return;
            }
            if (value != null) {
                section.put(key, value);
            }
        } else if (SectionOp.Remove.equals(op)) {
            section.remove(key);
        }
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public List<String> getHosts(String key) {
        return mHostMap.get(key);
    }
    
    /**
     * 
     * @param key
     * @return
     */
    public Section getSection(String key) {
        return mSectionMap.get(key);
    }
    
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
            if (key.endsWith(SUFFIX_VARS)) {
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
                if (key.endsWith(SUFFIX_VARS)) {
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
