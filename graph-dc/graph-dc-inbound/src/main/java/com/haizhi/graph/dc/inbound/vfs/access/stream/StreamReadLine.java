package com.haizhi.graph.dc.inbound.vfs.access.stream;

import com.haizhi.graph.dc.inbound.vfs.access.ReadLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by chengangxiong on 2019/01/28
 */
public class StreamReadLine implements ReadLine {

    private BufferedReader br;

    public StreamReadLine(InputStream inputStream) {
        br = new BufferedReader(new InputStreamReader(inputStream));
    }

    @Override
    public String readLine() throws IOException {
        return br.readLine();
    }

    @Override
    public String firstLine() throws IOException {
        return readLine();
    }
}
