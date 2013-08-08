/**
 * created since 2012-6-26
 */
package com.alipay.mile.test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.annotation.Priority;
import com.alipay.ats.annotation.Subject;
import com.alipay.ats.annotation.Tester;
import com.alipay.ats.enums.PriorityLevel;
import com.alipay.ats.junit.SpecRunner;
import com.alipay.mile.client.result.MileExportResult;
import com.alipay.mile.client.result.MileInsertResult;

/**
 * 导出与导入用例
 * 
 * @author xiaoju.luo
 * @version $Id: SR921140.java,v 0.1 2012-6-26 下午07:33:27 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("导出与导入")
public class SR621040 extends DocdbTestTools {
    int timeOut = 5000;

    @Before
    public void SetUp() {
        MileInsertResult insertResult = null;
        stepInfo("插入数据");
        // 一般正常数据
        try {
            String sql = "insert into TEST_DAILY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=?";
            Object[] params = new Object[4];
            params[0] = "12354";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[3] = Long.valueOf(100);
            for (int i = 0; i < 29; i++) {
                insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            }
            // Logger.info("docid: " + insertResult.getDocId());
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }
    }

    @Test
    @Subject("导出")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC621040() {
        MileExportResult exportResult = null;
        stepInfo("导出数据");
        // 一般正常数据
        try {
            String sql = "export to ? from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST=?";
            Object[] params = new Object[3];
            params[0] = "/home/admin/su-test/ali00497425_Other_20121023_ctumile/mile_docdb/export.txt";
            params[1] = "127.0.0.2";
            params[2] = Long.valueOf(100);
            exportResult = getApplationClientImpl().preExport(sql, params, timeOut);
            Logger.infoText("aaa", exportResult.getDocState().toString());
        } catch (Exception e) {
            Logger.error("错误" + e);
            Assert.isFalse(true, "插入失败");
        }
    }

    // @Test
    // @Subject("导出异常")
    // @Priority(PriorityLevel.HIGHEST)
    // @Tester("xiaoju.luo")
    // public void TC621041() {
    // MileExportResult exportResult = null;
    // stepInfo("导出数据");
    // // 一般正常数据
    // try {
    // String sql =
    // "export to path from TEST_DAILY indexwhere TEST_IP=? where GMT_TEST=?";
    // Object[] params = new Object[3];
    // params[0] =
    // "/home/admin/mile_chenqun/ctumile_leveldb_import_branch/mile_docdb/export.txt";
    // params[1] = "127.0.0.2";
    // params[2] = Long.valueOf(100);
    // exportResult = getApplationClientImpl().preExport(sql, params, timeOut);
    // Logger.infoText("aaa", exportResult.getDocState().toString());
    // } catch (Exception e) {
    // Logger.error("错误"+e);
    // Assert.isFalse(true, "插入失败");
    // }
    // }

    @After
    public void tearDown() {
        try {
            stepInfo("删除数据");
            String sql = "delete from TEST_DAILY indexwhere TEST_IP=?";
            Object[] params = new Object[1];
            params[0] = "127.0.0.2";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
    }
}