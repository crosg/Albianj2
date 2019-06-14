/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.loader;

import org.albianj.loader.entry.AlbianBootAttribute;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class AlbianBootService {

    public final static  String RootBundleName = "root";

    @SuppressWarnings("resource")
    private static ArrayList<byte[]> unpack(FileInputStream fis) {
        ArrayList<byte[]> list = null;
        try {
            list = new ArrayList<byte[]>();
//            byte[] bsize = new byte[4];
//            fis.read(bsize);
//            long size = MemoryToIOStream.netStreamToInt(bsize, 0);
//            for (int i = 0; i < size; i++) {
//                byte[] blength = new byte[8];
//                fis.read(blength);
//                long length = MemoryToIOStream.netStreamToLong(blength, 0);
//                byte[] ebytes = new byte[(int) length];
//                fis.read(ebytes);
//                Base64 b64 = new Base64();
//                byte[] stream = b64.decode(ebytes);
//                list.add(stream);
//            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean start(String classpath, String kernelPath, String configPath) {
//        String sVersion = null;
//        String epath = System.getProperty("java.ext.dirs");
//        File dir = new File(classpath);
//        if (!dir.isDirectory()) {
//            return false;
//        }
//        File jarf = null;
//        File[] files = dir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return (name.endsWith(".spx"));
//            }
//        });
//        if (0 != files.length) {
//            jarf = files[0];
//        } else {
//            dir = new File(epath);
//            files = dir.listFiles(new FilenameFilter() {
//                @Override
//                public boolean accept(File dir, String name) {
//                    return (name.endsWith(".spx"));
//                }
//            });
//            if (0 != files.length) {
//                jarf = files[0];
//            }
//        }
//
//        if (null == jarf) {
//            System.out.println("not found Albian's spx file.please put spx file to classpath or exts-path.");
//            return false;
//        }
//        String fname = jarf.getName();
//        int begin = fname.indexOf("Albianj_");
//        int end = fname.indexOf(".spx");
//        sVersion = fname.substring(begin + "Albianj_".length(), end);
//
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(jarf);
//            byte[] bVersion = new byte[14];
//            fis.read(bVersion);
//            String sFVersion = bVersion.toString();
//            if (!sFVersion.equalsIgnoreCase(sVersion)) {
//
//            }
//
//            ArrayList<byte[]> list = unpack(fis);
//            if (Validate.isNullOrEmpty(list)) {
//                System.err.println("unzip the jars is null. ");
//                return false;
//            }
//            for (byte[] bs : list) {
//                AlbianClassLoader.getInstance().regeditPlugin(bs);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            if (null != fis) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        try {
//            Class<?> clss = AlbianClassLoader.getInstance()
//                    .loadClass("org.albianj.kernel.impl.AlbianTransmitterService");
//            IAlbianTransmitterService abs = (IAlbianTransmitterService) clss.newInstance();
//            if (!Validate.isNullOrEmptyOrAllSpace(kernelPath) && !Validate.isNullOrEmptyOrAllSpace(configPath)) {
//                abs.start(kernelPath, configPath);
//            } else if (Validate.isNullOrEmptyOrAllSpace(kernelPath) && !Validate.isNullOrEmptyOrAllSpace(configPath)) {
//                abs.start(configPath);
//            } else {
//                abs.start();
//            }
//            if (AlbianState.Running != abs.getLifeState()) {
                return false;
//            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
//        return true;
    }

    public static boolean start(String classpath, String configPath) {
        return start(classpath, configPath);
    }

    public static boolean start(String configPath) {
        return start(null, configPath, configPath);
    }

    /**
     * 使用bundle模式启动albianj，默认的bundleName为root
     * @param workPath  项目工程路径
     * @return
     */
    public static boolean attachBundle(String workPath){
        return attachBundle(RootBundleName,workPath);
    }

    /**
     * 指定bundleName从而使用bundle模式启动albianj，
     * @param  bundleName bundle的名字
     * @param workPath 项目工程路径
     * @return
     */
    public static boolean attachBundle(String bundleName,String workPath){

        AlbianBootContext bootContext = AlbianBootContext.Instance;
        boolean needLoadBoot = bootContext.needBootStart(); // 只有当第一次启动的时候，才需要加载bootloader
        if(needLoadBoot) {
            AlbianBootAttribute bootAttr = bootContext.findBootAttrOrNewIfNotExist();
            String system = System.getProperty("os.name");
            boolean isWin = system.toLowerCase().contains("windows");
            bootAttr.setWindows(isWin);
        }

        if(null == bundleName || bundleName.isEmpty() || bundleName.trim().isEmpty()) {
            bundleName = RootBundleName;
        }

        String tranServiceClass = "org.albianj.kernel.impl.FinalAlbianBootStartupService";
        AlbianBundleContext bundleContext = AlbianBootContext.Instance.findBundleContextOrNewIfNotExit(bundleName,workPath);
        try {
            Class<?> clzz = bundleContext.getClassLoader().loadClass(tranServiceClass);
            Object bundleTransmitterService = clzz.newInstance();
            Method m = clzz.getMethod("init",AlbianBundleContext.class);
            Object rc = m.invoke(bundleTransmitterService,bundleContext,needLoadBoot);
            return null == rc ? false : Boolean.parseBoolean(rc.toString());
        }catch (Exception e){
            System.out.println("Start Bundle -> " + bundleName + ",WorkPath -> " + workPath + " throw exception " + e.getMessage());
        }
        return false;
    }

    public static void detachBundle(String bundleName){
        AlbianBootContext.Instance.deleteBundle(bundleName);
    }

}