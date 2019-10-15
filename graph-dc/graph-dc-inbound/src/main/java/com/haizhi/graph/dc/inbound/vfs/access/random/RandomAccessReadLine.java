package com.haizhi.graph.dc.inbound.vfs.access.random;

import com.haizhi.graph.dc.inbound.vfs.access.ReadLine;
import org.apache.commons.vfs2.RandomAccessContent;

import java.io.IOException;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class RandomAccessReadLine implements ReadLine {

    private RandomAccessContent randomAccessContent;

    public RandomAccessReadLine(RandomAccessContent randomAccessContent){
        this.randomAccessContent = randomAccessContent;
    }

    @Override
    public String readLine() throws IOException {
        return randomAccessContent.readLine();
    }

    @Override
    public String firstLine() throws IOException {
        randomAccessContent.seek(0L);
        return randomAccessContent.readLine();
    }
}
