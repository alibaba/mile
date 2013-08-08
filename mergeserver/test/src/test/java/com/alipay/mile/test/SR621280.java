/**
 * 
 */
package com.alipay.mile.test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.alipay.ats.annotation.Feature;
import com.alipay.ats.junit.SpecRunner;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;

/**
 * @author xiaoju.luo
 * @version
 */
@RunWith(SpecRunner.class)
@Feature("查询截断的单列用例")
public class SR621280 extends DocdbTestTools {

	/** 超时 */
	private int timeOut = 5000;
	private int num = 3000;
	private long cut = 200;

	@Before
	public void setUp() {

		// 插入数据
		String sql = "insert into TEST_DAILY TEST_ID=?  GMT_TEST=?";
		Object[] params = new Object[2];
		params[0] = "127.0.0.9";
		params[1] = 100L;

		for (int i = 0; i < num; i++) {
			MileInsertResult insertResult;

			try {
				insertResult = getApplationClientImpl().preInsert(sql, params,
						timeOut);

				Logger.info("docid: " + insertResult.getDocId());

			} catch (Exception e) {
				Assert.isFalse(true, "插入数据异常");
			}
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Assert.isFalse(true, "插入数据异常");
		}
	}

	@After
	public void tearDown() {
		try {
			String sql = "delete from TEST_DAILY indexwhere TEST_ID=?";
			Object[] params = new Object[1];
			params[0] = "127.0.0.9";
			getApplationClientImpl().preDelete(sql, params, timeOut);

		} catch (Exception e) {
			Assert.isFalse(true, "插入数据异常");
		}
	}

	@Test
	public void testSelect() {
		String sql = "select GMT_TEST from TEST_DAILY indexwhere =TEST_ID?";
		Object[] params = new Object[1];
		params[0] = "127.0.0.9";
		MileQueryResult queryResult = null;
		try {
			queryResult = getApplationClientImpl().preQueryForList(sql, params,
					timeOut);
			List<Map<String, Object>> l = queryResult.getQueryResult();
			int i = 0;
			for (Map<String, Object> m : l) {
				if (i++ > cut)
					break;
				Assert.areEqual(100L, m.get("GMT_TEST"), "预期结果");
			}

		} catch (Exception e) {
			Assert.isFalse(true, "插入数据异常");
		}

	}
}
