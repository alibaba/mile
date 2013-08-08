/**
 * 
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
 * @author xiaoju.luo
 * @version $Id: SR622040.java,v 0.1 2012-11-9 下午02:02:04 xiaoju.luo Exp $
 */

@RunWith(SpecRunner.class)
@Feature("导出与导入")
public class SR622040 extends LevdbTestTools {
    /**超时*/
    private int timeOut = 5000;

    @Before
    public void setUp() {

        MileInsertResult insertResult = null;
        stepInfo("插入数据");
        // 一般正常数据
        try {
            String sql = "insert into TEST_VELOCITY TEST_ID=? TEST_NAME=? TEST_IP=? GMT_TEST=? ROWKEY=? GMT_CTEST=?";
            Object[] params = new Object[6];
            params[0] = "12345";
            params[1] = "milemac";
            params[2] = "127.0.0.2";
            params[3] = Long.valueOf(100);
            params[4] = "rowkey";
            params[5] = Long.valueOf(100);
            insertResult = getApplationClientImpl().preInsert(sql, params, timeOut);
            //	Logger.info("docid: " + insertResult.getDocId());
        } catch (Exception e) {
            Assert.isFalse(true, "插入失败");
        }
    }

    @Test
    @Subject("导出")
    @Priority(PriorityLevel.HIGHEST)
    @Tester("xiaoju.luo")
    public void TC622040() {
        MileExportResult exportResult = null;
        stepInfo("导出数据");
        // 一般正常数据
        try {
            String sql = "export to ? from TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST=?";
            Object[] params = new Object[3];
            params[0] = "/home/admin/susu/mile_levdb/export.txt";
            params[1] = "rowkey";
            params[2] = Long.valueOf(100);
            exportResult = getApplationClientImpl().preExport(sql, params, timeOut);
            Logger.infoText("aaa", exportResult.getDocState().toString());
        } catch (Exception e) {
            Logger.error("错误" + e);
            Assert.isFalse(true, "插入失败");
        }
    }

    //	@Test
    //	@Subject("导出异常")
    //	@Priority(PriorityLevel.HIGHEST)
    //	@Tester("xiaoju.luo")
    //	public void TC621041() {
    //		MileExportResult exportResult = null;
    //		stepInfo("导出数据");
    //		// 一般正常数据
    //		try {
    //			String sql = "export to path TEST_VELOCITY indexwhere ROWKEY=? where GMT_TEST=?";
    //			Object[] params = new Object[3];
    //			params[0] = "/home/admin/susu/mile_levdb/export.txt";
    //			params[1] = "rowkey";
    //			params[2] = Long.valueOf(100);
    //			exportResult = getApplationClientImpl().preExport(sql, params, timeOut);
    //		    Logger.infoText("aaa", exportResult.getDocState().toString());
    //			} catch (Exception e) {
    //				Logger.error("错误"+e);
    //				Assert.isFalse(true, "插入失败");
    //			}
    //	}

    @After
    public void tearDown() {
        try {
            stepInfo("删除数据");
            String sql = "delete from TEST_VELOCITY indexwhere ROWKEY=?";
            Object[] params = new Object[1];
            params[0] = "rowkey";
            getApplationClientImpl().preDelete(sql, params, timeOut);
        } catch (Exception e) {
            Assert.isFalse(true, "查询异常");
        }
    }
}
