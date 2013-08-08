/*
 * Copyright 2010 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.alipay.mile.benchmark;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.alipay.mile.benchmark.ctu.CtuMiniSearchProvider;
import com.alipay.mile.benchmark.util.Props;

public class Workload {
    //操作选择器
    private DiscreteGenerator operationChooser;
    //操作列表
    Map<String, CmdProvider>  operations;

    
    public Workload(){
        this.operationChooser = new DiscreteGenerator();
        this.operations = new HashMap<String, CmdProvider>();
    }
    
    
    /**
     * Initialize the workload. Called once, in the main client thread, before
     * any operations are started.
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void init(Props props) throws IOException {
        CmdProvider cmdProvider;
        int readPercent = props.getInt(Benchmark.SELECT, 0);
        int writePercent = props.getInt(Benchmark.INSERT, 0);

        double readProportion = (double) readPercent / (double) 100;
        double writeProportion = (double) writePercent / (double) 100;

        if (readProportion + writeProportion > 0) {
            double sum = readProportion + writeProportion;
            readProportion = readProportion/sum;
            writeProportion = writeProportion/sum;
        }

        if (readProportion > 0) {
            cmdProvider = new QueryProvider(props);
            operationChooser.addValue(readProportion, cmdProvider.getName());
            operations.put(cmdProvider.getName(), cmdProvider);
        }
        if (writeProportion > 0) {
            cmdProvider = new InsertProvider(props);
            operationChooser.addValue(writeProportion, cmdProvider.getName());
            operations.put(cmdProvider.getName(), cmdProvider);
        }
        if (props.containsKey(Benchmark.RECORD_FILE)) {
            String cmd = props.getString(Benchmark.CMD_PROVIDER, "");
            if (StringUtils.startsWith(cmd, "ctu_minisearch")) {
                cmdProvider = new CtuMiniSearchProvider(props);
            }else{
                cmdProvider = new SqlFileProvider(props); 
            }
            operationChooser.addValue(1.0, cmdProvider.getName());
            operations.put(cmdProvider.getName(), cmdProvider);   
        }

    }

    public boolean doTransaction(ClientWrapper db) {
        String op = operationChooser.nextString();
        boolean result = false;
        for (Entry<String, CmdProvider> entry : operations.entrySet()) {
            if (StringUtils.equals(entry.getKey(), op)) {
                result = db.execute(entry.getValue());
                break;
            }
        }
        return result;
    }

}
