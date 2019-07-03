package com.yang.serialport.utils;

import java.util.Map;

public class ConvertUtils {
    
    /**
    * 把输入的byte数组拆分，每个传感器的数据由byte数组转换为16进制以字符串形式存在cache数组里。
    * 每次更新cache数组，并拼装输出output数组。
    *
    * @param input
    * 输入数组
    *
    * @param cache
    * 存储所有address数据
    *
    * @return byte[address][第几位]
    */
    
    public static byte[][] convert(byte[] input, String[] cache) {
        if (input == null) {
            return null;
        }
        
        int index = 2; // 用来遍历整个输入数组
        int totalSensor = input[2];
        byte[][] output = new byte[totalSensor][18]; // 一共有totalSensor个数据，每个有18位(按你的那个数的位数)
        
        for(int i = 0; i < totalSensor; i++) {
            int sum = 0;
            int address = input[index++];
            sum += address;
            byte[] AI1 = new byte[2];
            AI1[0] = input[index++];
            sum += AI1[0];
            AI1[1] = input[index++];
            sum += AI1[1];
            byte[] AI2 = new byte[2];
            AI2[0] = input[index++];
            sum += AI2[0];
            AI2[1] = input[index++];
            sum += AI2[1];
            byte DI = input[index++];
            sum += DI;
            byte checkSum = input[index++];;
            
            if(sum % 10 == checkSum) { // 不知道除以10对吗
                byte[] data = ArrayUtils.concat(AI1, AI2);
                data = ArrayUtils.concat(data, new byte[]{DI});
                cache[address] = ByteUtils.byteArrayToHexString(data);
            } else {
                System.out.println("校验值错误，address: " + address);
                break;
            }
            
            /* 构建要输出的byte[] */
            StringBuilder sb = new StringBuilder();
            int checkSum2 = (address + 1 + 177 + 11 + 1 + AI1[0] + AI1[1] + AI2[0] + AI2[1] + 2 + DI) % 10; // 不知道除以10对吗
            sb.append("AE");
            sb.append(address);
            sb.append("01B10B01");
            sb.append(AI1[0]);
            sb.append(AI1[1]);
            sb.append(AI2[0]);
            sb.append(AI2[0]);
            sb.append("0000000002");
            sb.append(DI);
            sb.append(checkSum2);
            sb.append("AF");
            
            output[address] = ByteUtils.hexStr2Byte(sb.toString());
        }
        
        return output;
    }
}
