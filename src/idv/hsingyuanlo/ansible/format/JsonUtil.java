package idv.hsingyuanlo.ansible.format;

import idv.hsingyuanlo.ansible.core.AnsibleIni;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonUtil {
    
    private JsonUtil() {
        
    }
    
    public static String loadJsonFile(String jsonFileName) {
        BufferedReader jsonReader = null;
        StringBuilder builder = new StringBuilder();
        try {
            jsonReader = new BufferedReader(new FileReader(jsonFileName));
            
            String line = null;
            while ((line = jsonReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jsonReader != null) {
                try {
                    jsonReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
    
    public static String convertAnsibleIniStringToJsonString(String ansibleIniText) {
        //
        AnsibleIni ansibleIni = new AnsibleIni();
        ansibleIni.readFromString(ansibleIniText);
        return convertAnsibleIniToJsonString(ansibleIni);
    }
    
    public static String convertAnsibleIniFileToJsonString(String ansibleIniFileName) {
        //
        AnsibleIni ansibleIni = new AnsibleIni();
        ansibleIni.readFromFile(ansibleIniFileName);
        return convertAnsibleIniToJsonString(ansibleIni);
    }
    
    public static String convertAnsibleIniToJsonString(AnsibleIni ansibleIni) {
        //
        JSONObject jsonobj = new JSONObject();
        //List data
        Set<String> hostKeys = ansibleIni.getHostKeys();
        for (String hostKey : hostKeys) {
            JSONArray jarr = new JSONArray();
            List<String> hosts = ansibleIni.getHosts(hostKey);
            for (String host : hosts) {
                jarr.put(host);
            }
            jsonobj.put(hostKey, jarr);
        }
        // Map data
        Set<String> sectionKeys = ansibleIni.getSectionKeys();
        for (String sectionKey : sectionKeys) {
            JSONObject jobj = new JSONObject();
            Set<String> itemKeys = ansibleIni.getSectionItemKeys(sectionKey);
            for (String itemKey: itemKeys) {
                jobj.put(itemKey, ansibleIni.getSectionItem(sectionKey, itemKey));
            }
            jsonobj.put(sectionKey, jobj);
        }
        
        return jsonobj.toString(4);
    }
    
    public static String convertJsonFileToAnsibleIniString(String jsonFileName) {
        return convertJsonStringToAnsibleIniString(loadJsonFile(jsonFileName));
    }
    
    public static String convertJsonStringToAnsibleIniString(String jsonText) {
        JSONObject jsonobj = new JSONObject(jsonText);
        
        StringBuilder builder = new StringBuilder();
        
        Set<String> keys = jsonobj.keySet();
        for (String key : keys) {
            builder.append("["+key+"]\n");
            
            Object obj = jsonobj.get(key);
            if (obj instanceof JSONArray) { // List data
                JSONArray jarr = (JSONArray)obj;
                for (int i=0; i< jarr.length(); i++) {
                    builder.append(jarr.get(i)+"\n");
                }
            } else if (obj instanceof JSONObject) { // Map data
                JSONObject jobj = (JSONObject)obj;
                Set<String> ikeys = jobj.keySet();
                for (String ikey : ikeys) {
                    builder.append(ikey+"="+jobj.get(ikey)+"\n");
                }
            }
            builder.append("\n");
        }
        
        return builder.toString();
    }
}
