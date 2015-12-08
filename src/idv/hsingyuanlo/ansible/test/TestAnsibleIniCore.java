package idv.hsingyuanlo.ansible.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import idv.hsingyuanlo.ansible.core.AnsibleIni;

public class TestAnsibleIniCore{

    private AnsibleIni mIni;
    
    @Before
    public void setup() {
        mIni = new AnsibleIni();
        mIni.read("hosts");
    }
    
    @Test
    public void testAnsibleIniReadHost() {
        List<String> list = mIni.getHosts("servers");
        Assert.assertTrue(list.contains("play"));
        Assert.assertFalse(list.contains("play-bug"));
    }

    @Test
    public void testAnsibleIniReadHostChildren() {
        List<String> list = mIni.getHosts("play:children");
        Assert.assertTrue(list.contains("frontend"));
        Assert.assertFalse(list.contains("frontend-bug"));
    }
    
    @Test
    public void testAnsibleIniReadSectionVars() {
        Assert.assertNotNull(mIni.getSectionItem("frontend:vars", "animation_enabled"));
        Assert.assertNull(mIni.getSectionItem("frontend:vars", "animation_enable"));
    }
}
