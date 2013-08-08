package com.alipay.mile.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.alipay.mile.message.TypeCode;

/**
 * 数值转换工具类
 *
 * @author jin.qian
 * @version $Id: ByteConveror.java, v 0.1 2011-5-9 下午01:37:01 jin.qian Exp $
 */
public class ByteConveror {

    /**
     * long 2 byte
     *
     * @param value
     * @return
     */
    public static byte[] toByte(long value) {
        byte[] rtv = new byte[8];
        rtv[7] = (byte) (0xff & value);
        rtv[6] = (byte) (0xff & (value >> 8));
        rtv[5] = (byte) (0xff & (value >> 16));
        rtv[4] = (byte) (0xff & (value >> 24));
        rtv[3] = (byte) (0xff & (value >> 32));
        rtv[2] = (byte) (0xff & (value >> 40));
        rtv[1] = (byte) (0xff & (value >> 48));
        rtv[0] = (byte) (0xff & (value >> 56));
        return rtv;
    }

    /**
     * byte to long
     *
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public final static long getLong(byte[] buf, int start, int end) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (end - start > 8) {
            throw new IllegalArgumentException("byte array size > 8 !");
        }
        long r = 0;
        for (int i = start; i < end; i++) {
            r <<= 8;
            r |= (buf[i] & 0x00000000000000ff);
        }
        return r;
    }

    /**
     * int to byte
     *
     * @param value
     * @return
     */
    public static byte[] toByte(int value) {
        byte[] rtv = new byte[4];
        rtv[3] = (byte) (0xff & value);
        rtv[2] = (byte) ((0xff00 & value) >> 8);
        rtv[1] = (byte) ((0xff0000 & value) >> 16);
        rtv[0] = (byte) ((0xff000000 & value) >> 24);
        return rtv;
    }

    /**
     * byte to int
     *
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public final static int getInt(byte[] buf, int start, int end) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (end - start > 4) {
            throw new IllegalArgumentException("byte array size > 4 !");
        }
        int r = 0;
        for (int i = start; i < end; i++) {
            r <<= 8;
            r |= (buf[i] & 0x000000ff);
        }
        return r;
    }

    /**
     * short to byte
     *
     * @param value
     * @return
     */
    public static byte[] toByte(short value) {
        byte[] rtv = new byte[2];

        rtv[1] = (byte) (0xff & value);
        rtv[0] = (byte) ((0xff00 & value) >> 8);
        return rtv;
    }

    /**
     * byte to short
     *
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public final static short getShort(byte[] buf, int start, int end) {
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (end - start > 2) {
            throw new IllegalArgumentException("byte array size > 2 !");
        }
        short r = 0;
        for (int i = start; i < end; i++) {
            r <<= 8;
            r |= (buf[i] & 0x00ff);
        }

        return r;
    }

    /**
     * float to byte
     *
     * @param value
     * @return
     */
    public static byte[] toByte(float value) {
        return toByte(Float.floatToIntBits(value));
    }

    /**
     * byte to float
     *
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public final static float getFloat(byte[] buf, int start, int end) {
        return Float.intBitsToFloat(getInt(buf, start, end));
    }

    /**
     * double to byte
     *
     * @param value
     * @return
     */
    public static byte[] toByte(double value) {
        return toByte(Double.doubleToLongBits(value));
    }

    /**
     * byte to double
     *
     * @param buf
     * @param start
     * @param end
     * @return
     */
    public final static double getDouble(byte[] buf, int start, int end) {
        return Double.longBitsToDouble(getLong(buf, start, end));
    }

    public final static String readString(DataInput is) throws IOException {
        int len = is.readInt();
        byte[] value = new byte[len];
        is.readFully(value, 0, len);
        return new String(value, 0, len, "utf-8");
    }

    public final static void writeString(DataOutput os, String str) throws IOException {
        os.writeInt(str.length());
        os.write(str.getBytes("utf-8"));
    }

    /**
     * 处理输入的byte流 成object 值
     *
     * @param tc
     *            值类型
     * @param is
     *            输入流
     * @return obj Value
     * @throws IOException
     */
    public final static Object getData(DataInput is) throws IOException {
        Object value;
        byte tc;
        int len;

        tc = is.readByte();
        if (tc == TypeCode.TC_NULL) {
            return null;
        }
        if(tc == TypeCode.TC_NOTCOMP){
            return null;
        }

        len = is.readInt();
        switch (tc) {
            case TypeCode.TC_INT_8:
                value = is.readByte();
                break;
            case TypeCode.TC_INT_16:
                value = is.readShort();
                break;
            case TypeCode.TC_INT_32:
                value = is.readInt();
                break;
            case TypeCode.TC_INT_64:
                value = is.readLong();
                break;
            case TypeCode.TC_FLOAT:
                value = is.readFloat();
                break;
            case TypeCode.TC_DOUBLE:
                value = is.readDouble();
                break;
            case TypeCode.TC_BYTES:
                value = new byte[len];
                is.readFully((byte[]) value, 0, len);
                break;
            case TypeCode.TC_STRING:
                value = new byte[len];
                is.readFully((byte[]) value, 0, len);
                value = new String((byte[]) value, 0, len, "utf-8");
                break;
            case TypeCode.TC_SET:
                Set<Object> set = new HashSet<Object>();
                for (int i = 0; i < len; i++) {
                    set.add(getData(is));
                }
                value = set;
                break;
            case TypeCode.TC_ARRAY:
                List<Object> array = new ArrayList<Object>();
                for (int i = 0; i < len; i++) {
                    array.add(getData(is));
                }
                value = array;
                break;
            default:
                throw new IllegalArgumentException("TC type is error");
        }
        return value;
    }

    /**
     * 将数据写到输出流中
     *
     * @param os
     *            输出流
     * @param value
     *            数据
     * @throws IOException
     */
    public final static void outPutData(DataOutput os, Object value) throws IOException {
        byte[] data;

        if (null == value) {
            os.writeByte(TypeCode.TC_NULL);
            return;
        }

        if (value instanceof Byte) {
            os.writeByte(TypeCode.TC_INT_8);
            os.writeInt(1);
            os.writeByte((Byte) value & 0x000000ff);
        } else if (value instanceof Short) {
            os.writeByte(TypeCode.TC_INT_16);
            os.writeInt(2);
            os.writeShort((Short) value & 0x0000ffff);
        } else if (value instanceof Integer) {
            os.writeByte(TypeCode.TC_INT_32);
            os.writeInt(4);
            os.writeInt((Integer) value);
        } else if (value instanceof Long) {
            os.writeByte(TypeCode.TC_INT_64);
            os.writeInt(8);
            os.writeLong((Long) value);
        } else if (value instanceof Float) {
            os.writeByte(TypeCode.TC_FLOAT);
            os.writeInt(4);
            os.writeFloat((Float) value);
        } else if (value instanceof Double) {
            os.writeByte(TypeCode.TC_DOUBLE);
            os.writeInt(8);
            os.writeDouble((Double) value);
        } else if (value instanceof byte[]) {
            data = (byte[]) value;
            os.writeByte(TypeCode.TC_BYTES);
            os.writeInt(data.length);
            os.write(data);
        } else if (value instanceof String) {
            data = ((String) value).getBytes("utf-8");
            os.writeByte(TypeCode.TC_STRING);
            os.writeInt(data.length);
            os.write(data);
        } else if (value instanceof List<?>) {
            List<?> valueList = (List<?>) value;
            os.writeByte(TypeCode.TC_ARRAY);
            os.writeInt(valueList.size());
            for(Object obj : valueList){
                outPutData(os, obj);
            }
        } else {
            throw new IllegalArgumentException("dynValue is error");
        }

    }

    /**
     * 预编译sql值转换
     *
     * @param tc
     * @param valueDesc
     * @return
     */
    public final static Object preString2value(byte tc, String valueDesc) {
        Object value = null;
        if (null == valueDesc || StringUtils.equalsIgnoreCase(valueDesc, "null")) {
            return null;
        }
        if (tc == TypeCode.TC_INT_8) {
            value = Byte.decode(valueDesc);
        } else if (tc == TypeCode.TC_INT_16) {
            value = Short.decode(valueDesc);
        } else if (tc == TypeCode.TC_INT_32) {
            value = Integer.decode(valueDesc);
        } else if (tc == TypeCode.TC_INT_64) {
            value = Long.decode(valueDesc);
        } else if (tc == TypeCode.TC_FLOAT) {
            value = Float.valueOf(valueDesc);
        } else if (tc == TypeCode.TC_DOUBLE) {
            value = Double.valueOf(valueDesc);
        } else if (tc == TypeCode.TC_STRING) {
            value = valueDesc;
        }
        return value;
    }

    /**
     *
     * @param tc
     * @param leftData
     * @param rightData
     * @return
     */
    public final static long dataCompare(byte tc, Object leftData, Object rightData) {
        long value = 0;
        if (tc == TypeCode.TC_INT_8) {
            value = (Byte) leftData - (Byte) rightData;
        } else if (tc == TypeCode.TC_INT_16) {
            value = (Short) leftData - (Short) rightData;
        } else if (tc == TypeCode.TC_INT_32) {
            value = (Integer) leftData - (Integer) rightData;
        } else if (tc == TypeCode.TC_INT_64) {
            value = (Long) leftData - (Long) rightData;
        } else if (tc == TypeCode.TC_FLOAT) {
            value = ((Float) leftData).compareTo((Float) rightData);
        } else if (tc == TypeCode.TC_DOUBLE) {
            value = ((Double) leftData).compareTo((Double) rightData);
        } else if (tc == TypeCode.TC_STRING) {
            value = ((String) leftData).compareTo((String) rightData);
        }
        return value;
    }

}
