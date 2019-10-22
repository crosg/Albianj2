package org.albianj.argument;

import org.albianj.verify.Validate;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数解析类，
 * 只要给参数的单个char就可以了
 */
public class ArgOpt {

    /**
     * 解析argument的参数
     * @param args args参数
     * @param valArgName  一定会带有值的参数，每个参数一个char,并且带有:，比如a和b参数，记为a:b:
     * @param flagsArgName 不带有值，只是一个指示作用的参数，每个参数一个char
     * @return key为参数命令，value为参数值的map，指示参数value为true
     */
    public static Map parser(String[] args,String valArgName ,String flagsArgName){
        if(null == args || 0 == args.length) {
            return  null;
        }

        String argName = null;
        if(!Validate.isNullOrEmptyOrAllSpace(valArgName)) {
            argName = valArgName;
        }
        if(!Validate.isNullOrEmptyOrAllSpace(flagsArgName)) {
            argName = null == argName ? flagsArgName : argName + flagsArgName;
        }

        if(Validate.isNullOrEmptyOrAllSpace(argName)) {
            return null;
        }

        int c;
        Map<String,Object> map = new HashMap<>();
        GetOpt getOpt = new GetOpt(args, argName);
        while ((c = getOpt.getNextOption()) != -1) {
            char ck = (char) c;
            String key = new String(new char[] {ck});
            Object val = null;
            if(-1 !=  valArgName.indexOf(c)) {
                val = getOpt.getOptionArg();
            } else {
                val = true;
            }
            map.put(key,val);
        }
        return map;
    }
}
