/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2012 All Rights Reserved.
 */
package com.alipay.mile.benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.alipay.mile.benchmark.util.FileLine;
import com.alipay.mile.benchmark.util.Props;

/**
 * 
 * @author yuzhong.zhao
 * @version $Id: FileProvider.java, v 0.1 2012-11-6 下午09:43:02 yuzhong.zhao Exp $
 */
public abstract class FileProvider implements CmdProvider {

    private static final Logger  logger = Logger.getLogger(FileProvider.class.getName());

    private final FileReader     fr;
    
    private final BufferedReader br;

    private String               fileName;

    private String               name;

    private long                 lineNum;

    public FileProvider(Props props) throws IOException {
        //创建文件
        this.fileName = props.getString(Benchmark.RECORD_FILE);
        this.fr = new FileReader(fileName);
        this.br = new BufferedReader(fr);
        this.name = props.getString(Benchmark.CMD_PROVIDER, "default");
        this.lineNum = 0;
    }

    synchronized public FileLine next() {
        FileLine line = null;
        try {
            line = new FileLine();
            line.setText(br.readLine());
            line.setLineNum(lineNum++);
            line.setFile(fileName);
        } catch (IOException e) {
            logger.error("从文件" + fileName + "的第" + lineNum + "行读取数据出错", e);
            return null;
        } 
        return line;
    }

    public BufferedReader getBr() {
        return br;
    }

    @Override
    public String getName() {
        return name;
    }

}
