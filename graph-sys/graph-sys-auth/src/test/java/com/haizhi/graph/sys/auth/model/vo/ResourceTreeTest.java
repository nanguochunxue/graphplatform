package com.haizhi.graph.sys.auth.model.vo;

import com.haizhi.graph.common.model.BaseTreeNodeVo;
import com.haizhi.graph.common.model.VoTreeBuilder;
import com.haizhi.graph.common.log.GLog;
import com.haizhi.graph.common.log.LogFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanghaiyang on 2019/1/8.
 */
public class ResourceTreeTest {
    private static final GLog LOG = LogFactory.getLogger(ResourceTreeTest.class);


    @Test
    @SuppressWarnings("all")
    public void buildTreeList() throws Exception{

        SysResourceVo node5 = new SysResourceVo(15L, 14L, "ddddfff", "四级资源", "http://xxxx","P","code1");
        SysResourceVo node3 = new SysResourceVo(13L, 12L, "ddddg5juj", "三级资源", "http://xxxx","P","code2");
        SysResourceVo node2 = new SysResourceVo(12L, 11L, "ddddloo", "二级资源", "http://xxxx","P","code3");
        SysResourceVo node4 = new SysResourceVo(14L, 21L, "dddde4tg5", "二级资源", "http://xxxx","P","code4");
        SysResourceVo node1 = new SysResourceVo(11L, 0L, "dddddsdf", "一级资源", "http://xxxx","P","code5");
        SysResourceVo node6 = new SysResourceVo(21L, 0L, "dddd11", "一级资源", "http://xxxx","P","code6");

        List<SysResourceVo> list = new ArrayList<>();
        list.add(node1);
        list.add(node2);
        list.add(node3);
        list.add(node4);
        list.add(node5);
        list.add(node6);

        BaseTreeNodeVo retTree = VoTreeBuilder.createTree(list, SysResourceVo.class);

        List<SysResourceVo> listTree = VoTreeBuilder.createTreeList(list, SysResourceVo.class);

        System.out.println("+++++++++++ retTree +++++++++++++++++++++");
        LOG.info(retTree);
        System.out.println("++++++++++++ listTree ++++++++++++++++++++");
        LOG.info(listTree);

    }

}
