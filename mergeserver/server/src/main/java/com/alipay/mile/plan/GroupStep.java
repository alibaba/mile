/**
 * Alipay.com Inc.
 * Copyright (c) 2005-2011 All Rights Reserved.
 */
package com.alipay.mile.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import com.alipay.mile.Constants;
import com.alipay.mile.FieldDesc;
import com.alipay.mile.Record;
import com.alipay.mile.SqlResultSet;
import com.alipay.mile.mileexception.IllegalSqlException;
import com.alipay.mile.mileexception.SqlExecuteException;

/**
 * 聚组步骤，对docserver的返回结果进行group及having
 *
 * @author yuzhong.zhao
 * @version $Id: GroupStep.java,v 0.1 2011-5-15 06:58:29 yuzhong.zhao Exp $
 */
public class GroupStep implements ExecuteStep {
    // 要进行聚组的列
    private List<FieldDesc> groupByFields;
    // 选择列
    private List<FieldDesc> selectFields;
    // 聚组列在选择列中的索引位置
    private List<Integer>   groupIndex;
    // 聚合列在选择列中的索引位置
    private List<Integer>   funcIndex;

    /**
     * 构造函数
     *
     * @param groupByFields                 聚组列
     * @param selectFields                  选择列
     * @throws SqlExecuteException
     * @throws Exception
     */
    public GroupStep(List<FieldDesc> groupByFields, List<FieldDesc> selectFields)
                                                                                 throws SqlExecuteException {
        int i, j;
        this.groupByFields = groupByFields;
        this.selectFields = selectFields;
        this.groupIndex = new ArrayList<Integer>(groupByFields.size());
        this.funcIndex = new ArrayList<Integer>();
        // 功能函数编号
        for (i = 0; i < selectFields.size(); i++) {
            FieldDesc fieldDesc = selectFields.get(i);
            if (fieldDesc.isComputeField()) {
                funcIndex.add(i);
            } else if (!groupByFields.contains(fieldDesc)) {
                throw new SqlExecuteException("在选择列中的列一定要是原始列或者聚合列, 列" + fieldDesc + "不满足条件");
            }
        }
        // group by 列编号
        for (i = 0; i < groupByFields.size(); i++) {
            j = selectFields.indexOf(groupByFields.get(i));
            if (j < 0) {
                throw new SqlExecuteException("在选择列中找不到group列" + groupByFields.get(i));
            }
            groupIndex.add(j);
        }

    }

    /**
     * 比较两个对象，返回两个对象的最大值
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static Object max(Object o1, Object o2) throws SqlExecuteException {
        if (o1 == null) {
            return o2;
        } else if (o2 == null) {
            return o1;
        } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
            Comparable<Object> comp1 = (Comparable<Object>) o1;
            Comparable<Object> comp2 = (Comparable<Object>) o2;
            if (comp1.compareTo(comp2) < 0) {
                return comp2;
            } else {
                return comp1;
            }
        } else {
            throw new SqlExecuteException("不能进行max运算的对象类型");
        }
    }

    /**
     * 比较两个对象，返回两个对象的最小值
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Object min(Object o1, Object o2) throws SqlExecuteException {
        if (o1 == null) {
            return o2;
        } else if (o2 == null) {
            return o1;
        } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
            Comparable<Object> comp1 = (Comparable<Object>) o1;
            Comparable<Object> comp2 = (Comparable<Object>) o2;
            if (comp1.compareTo(comp2) < 0) {
                return comp1;
            } else {
                return comp2;
            }
        } else {
            throw new SqlExecuteException("不能进行min运算的对象类型");
        }
    }

    /**
     * 对两个对象求和，返回两个对象的和
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    public Object sum(Object o1, Object o2) throws SqlExecuteException {
        if (o1 == null) {
            return o2;
        } else if (o2 == null) {
            return o1;
        } else if (o1 instanceof Long && o2 instanceof Long) {
            return (Long) o1 + (Long) o2;
        } else if (o1 instanceof Integer && o2 instanceof Integer) {
            return (Integer) o1 + (Integer) o2;
        } else if (o1 instanceof Short && o2 instanceof Short) {
            int sum = (Short) o1 + (Short) o2;
            return Short.valueOf((short) sum);
        } else if (o1 instanceof Float && o2 instanceof Float) {
            return (Float) o1 + (Float) o2;
        } else if (o1 instanceof Double && o2 instanceof Double) {
            return (Double) o1 + (Double) o2;
        } else {
            Double d1 = Double.valueOf(o1.toString());
            Double d2 = Double.valueOf(o2.toString());
            return Double.valueOf(d1 + d2);
        }
    }

    /**
     * 计数，计数对象必须为整型，返回两个对象的和
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    public Object count(Object o1, Object o2) throws SqlExecuteException {
        if (null == o1) {
            return o2;
        } else if (null == o2) {
            return o1;
        } else if (o1 instanceof Long && o2 instanceof Long) {
            return sum(o1, o2);
        } else {
            throw new SqlExecuteException("不能进行count运算的对象类型");
        }
    }

    /**
     * 计算平均值
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Object avg(Object o1, Object o2) throws SqlExecuteException {
        if (o1 == null) {
            return o2;
        } else if (o2 == null) {
            return o1;
        } else if (o1 instanceof List<?> && o2 instanceof List<?>) {
            List<Object> list1 = (List<Object>) o1;
            List<Object> list2 = (List<Object>) o2;
            //和
            list1.set(0, sum(list1.get(0), list2.get(0)));
            //count值
            list1.set(1, sum(list1.get(1), list2.get(1)));
            return list1;
        } else {
            throw new SqlExecuteException("不能进行平均值运算的对象类型");
        }
    }

    /**
     * 计算方差
     *
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Object var(Object o1, Object o2) throws SqlExecuteException {
        if (o1 == null) {
            return o2;
        } else if (o2 == null) {
            return o1;
        } else if (o1 instanceof List<?> && o2 instanceof List<?>) {
            List<Object> list1 = (List<Object>) o1;
            List<Object> list2 = (List<Object>) o2;
            //平方和
            list1.set(0, sum(list1.get(0), list2.get(0)));
            //和
            list1.set(1, sum(list1.get(1), list2.get(1)));
            //count值
            list1.set(2, sum(list1.get(2), list2.get(2)));
            return list1;
        } else {
            throw new SqlExecuteException("不能进行平均值运算的对象类型");
        }
    }

    /**
     * 
     * distinct计数，计数对象必须为set类型，返回两个集合的并集
     * 
     * @param o1
     * @param o2
     * @return
     * @throws SqlExecuteException
     */
    @SuppressWarnings("unchecked")
    public Object countDistinct(Object o1, Object o2) throws SqlExecuteException {
        if (null == o1) {
            return o2;
        } else if (null == o2) {
            return o1;
        } else if (o1 instanceof Set<?> && o2 instanceof Set<?>) {
            Set<Object> set1 = (Set<Object>) o1;
            Set<Object> set2 = (Set<Object>) o2;
            set1.addAll(set2);
            return set1;
        } else {
            throw new SqlExecuteException("不能进行count distinct运算的对象类型");
        }
    }

    /**
     * 计算聚合函数，聚合函数可能有多个，所以输入的两个参数是个链表
     *
     * @param baseValueList
     *            原始值的链表
     * @param newValueList
     *            新加的值的链表
     * @throws SqlExecuteException
     * @throws Exception
     */
    public void computeFunction(List<Object> baseValueList, List<Object> newValueList)
                                                                                      throws SqlExecuteException {
        for (int i = 0; i < funcIndex.size(); i++) {
            int index = funcIndex.get(i);
            // 获取聚合列的列描述
            FieldDesc fieldDesc = selectFields.get(index);
            // 获取当前聚合列的值
            Object baseValue = baseValueList.get(index);
            // 获取要被聚合的值
            Object newValue = newValueList.get(index);
            switch (fieldDesc.functionId) {
                case Constants.FUNC_COUNT:
                    baseValueList.set(index, count(baseValue, newValue));
                    break;
                case Constants.FUNC_MAX:
                    baseValueList.set(index, max(baseValue, newValue));
                    break;
                case Constants.FUNC_MIN:
                    baseValueList.set(index, min(baseValue, newValue));
                    break;
                case Constants.FUNC_SUM:
                    baseValueList.set(index, sum(baseValue, newValue));
                    break;
                case Constants.FUNC_SQUARE_SUM:
                    baseValueList.set(index, sum(baseValue, newValue));
                    break;
                case Constants.FUNC_AVG:
                    baseValueList.set(index, avg(baseValue, newValue));
                    break;
                case Constants.FUNC_VAR:
                    baseValueList.set(index, var(baseValue, newValue));
                    break;
                case Constants.FUNC_STD:
                    baseValueList.set(index, var(baseValue, newValue));
                    break;
                case Constants.FUNC_DISTINCT_COUNT:
                    baseValueList.set(index, countDistinct(baseValue, newValue));
                    break;
                default:
                    throw new SqlExecuteException("不支持的函数计算类型" + fieldDesc.functionId);
            }
        }

    }

    /**
     * 对查询结果进行聚组操作
     *
     * @param input         group步的输入是SqlResultSet
     * @param params        group步骤的动态绑定参数
     * @param timeOut       超时时间
     * @return              group步的输出是SqlResultSet，记录聚组并经过having过滤后的查询结果集 
     * @throws SqlExecuteException
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * @throws IllegalSqlException
     * @throws Exception
     * @see com.alipay.mile.plan.ExecuteStep#execute(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public Object execute(Object input, Map<Object, List<Object>> paramBindMap, int timeOut)
                                                                                            throws SqlExecuteException,
                                                                                            IllegalSqlException,
                                                                                            IOException,
                                                                                            InterruptedException,
                                                                                            ExecutionException {
        int i;
        SqlResultSet resultSet;

        // 用于进行group操作的hashmap
        Map<List<Object>, Record> map;
        // 聚组列就是map中key
        List<Object> key;
        // 每一行查询结果记录就是value
        Record value;

        if (null == input) {
            throw new SqlExecuteException("在执行group步时输入为空");
        }
        if (!(input instanceof SqlResultSet)) {
            throw new SqlExecuteException("在执行group步时输入不是SqlResultSet类型");
        }

        resultSet = (SqlResultSet) input;
        map = new HashMap<List<Object>, Record>();
        // 对docserver的每一行返回结果记录进行遍历
        for (Record record : resultSet.data) {
            // 找出聚组列
            key = new ArrayList<Object>(groupByFields.size());
            for (i = 0; i < groupByFields.size(); i++) {
                key.add(record.data.get(groupIndex.get(i)));
            }
            // 在map中根据聚组列查询相应的记录
            value = map.get(key);
            if (null == value) {
                map.put(key, record);
            } else {
                // 对记录进行聚组，计算聚组函数
                computeFunction(value.data, record.data);
            }
        }

        // 将聚组结果转化为结果集
        List<Record> groupResult = new ArrayList<Record>();
        Iterator<Entry<List<Object>, Record>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<List<Object>, Record> entry = (Map.Entry<List<Object>, Record>) iter.next();
            Record record = entry.getValue();
            for (i = 0; i < funcIndex.size(); i++) {
                int index = funcIndex.get(i);
                // 获取聚合列的列描述
                FieldDesc fieldDesc = selectFields.get(index);
                Object complexValue = record.data.get(index);
                if (fieldDesc.functionId == Constants.FUNC_COUNT) {
                    if (null == complexValue) {
                        record.data.set(index, Long.valueOf(0));
                    }
                } else if (fieldDesc.functionId == Constants.FUNC_DISTINCT_COUNT) {
                    if (null == complexValue) {
                        record.data.set(index, Long.valueOf(0));
                    } else {
                        Set<Object> set = (Set<Object>) record.data.get(index);
                        record.data.set(index, Long.valueOf(set.size()));
                    }
                } else if (fieldDesc.functionId == Constants.FUNC_AVG) {
                    if (complexValue != null) {
                        List<Object> list = (List<Object>) complexValue;
                        Double sum = (Double) list.get(0);
                        Long count = (Long) list.get(1);
                        record.data.set(index, new Double(sum / count));
                    } else {
                        record.data.set(index, null);
                    }
                } else if (fieldDesc.functionId == Constants.FUNC_VAR) {
                    if (complexValue != null) {
                        List<Object> list = (List<Object>) complexValue;
                        Double squaresum = (Double) list.get(0);
                        Double sum = (Double) list.get(1);
                        Long count = (Long) list.get(2);
                        record.data.set(index, new Double(squaresum / count - (sum / count)
                                                          * (sum / count)));
                    } else {
                        record.data.set(index, null);
                    }
                } else if (fieldDesc.functionId == Constants.FUNC_STD) {
                    if (complexValue != null) {
                        List<Object> list = (List<Object>) complexValue;
                        Double squaresum = (Double) list.get(0);
                        Double sum = (Double) list.get(1);
                        Long count = (Long) list.get(2);
                        record.data.set(index, new Double(Math
                            .sqrt(squaresum / count - (sum / count) * (sum / count))));
                    } else {
                        record.data.set(index, null);
                    }
                }
            }
            groupResult.add(record);
        }
        if (groupResult.isEmpty() && groupIndex.isEmpty()) {
            Record record = new Record();
            for (FieldDesc fieldDesc : selectFields) {
                switch (fieldDesc.functionId) {
                    case Constants.FUNC_COUNT:
                        record.data.add(Long.valueOf(0));
                        break;
                    case Constants.FUNC_DISTINCT_COUNT:
                        record.data.add(Long.valueOf(0));
                        break;
                    default:
                        record.data.add(null);
                }
            }
            groupResult.add(record);
        }

        resultSet.data = groupResult;
        return resultSet;
    }

    public List<FieldDesc> getGroupByFields() {
        return groupByFields;
    }

    public void setGroupByFields(List<FieldDesc> groupByFields) {
        this.groupByFields = groupByFields;
    }

    public List<FieldDesc> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<FieldDesc> selectFields) {
        this.selectFields = selectFields;
    }

    public List<Integer> getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(List<Integer> groupIndex) {
        this.groupIndex = groupIndex;
    }

    public List<Integer> getFuncIndex() {
        return funcIndex;
    }

    public void setFuncIndex(List<Integer> funcIndex) {
        this.funcIndex = funcIndex;
    }

}
