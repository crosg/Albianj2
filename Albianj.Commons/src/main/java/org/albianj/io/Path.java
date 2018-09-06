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
package org.albianj.io;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Path {

    public static ClassLoader getClassLoader() {

        return Path.class.getClassLoader();
    }

    public static ClassLoader getClassLoader(
            @SuppressWarnings("rawtypes") Class cla) {

        return cla.getClassLoader();// Path.class.getClassLoader();
    }

    public static String getAbsolutePathOfClassLoaderClassPath() {
        return getClassLoader().getResource("").toString();
    }

    public static String getAbsolutePathOfClassLoaderClassPath(
            @SuppressWarnings("rawtypes") Class cla) {
        return getClassLoader(cla).getResource("").toString();
    }

    public static String getExtendResourcePath(String relativePath)
            throws MalformedURLException, URISyntaxException {
        return getExtendResourcePath(Path.class, relativePath);
    }

    public static String getExtendResourcePath(
            @SuppressWarnings("rawtypes") Class cla, String relativePath)
            throws MalformedURLException, URISyntaxException {
        URL resourceAbsoluteURL = null;

        String path = null;
        if (relativePath.startsWith("http://")) {
//			if(relativePath.endsWith("/")){
//				return relativePath;
//			} else {
            return relativePath;
//			}
        } else {
            if (relativePath.startsWith("../")) {
                String classPathAbsolutePath = getAbsolutePathOfClassLoaderClassPath(cla);
                if (relativePath.substring(0, 1).equals("/")) {
                    relativePath = relativePath.substring(1);
                }
                String wildcardString = relativePath.substring(0,
                        relativePath.lastIndexOf("../") + 3);
                relativePath = relativePath.substring(relativePath
                        .lastIndexOf("../") + 3);
                int containSum = containSum(wildcardString, "../");
                classPathAbsolutePath = cutLastString(classPathAbsolutePath, "/",
                        containSum);
                String resourceAbsolutePath = classPathAbsolutePath + relativePath;
                resourceAbsoluteURL = new URL(resourceAbsolutePath);
                path = resourceAbsoluteURL.toURI().getPath();
            } else {
                path = relativePath;
            }
        }
        return path;
    }

    private static int containSum(String source, String dest) {
        int containSum = 0;
        int destLength = dest.length();
        while (source.contains(dest)) {
            containSum = containSum + 1;
            source = source.substring(destLength);

        }
        return containSum;
    }

    private static String cutLastString(String source, String dest, int num) {
        for (int i = 0; i < num; i++) {
            source = source.substring(0,
                    source.lastIndexOf(dest, source.length() - 2) + 1);

        }
        return source;
    }

    public static URL getResource(String resource) {
        return getClassLoader().getResource(resource);
    }

    public static URL getResource(@SuppressWarnings("rawtypes") Class cla,
                                  String resource) {
        return getClassLoader(cla).getResource(resource);
    }

    public static void traversalAllFolder( List< String > files,boolean isDepth ,String path, String currSubFolder,
                                           IFileMark fileMark, String mark ) {
        File file = new File( path );
        if(StringUtils.isBlank(currSubFolder)) {
            currSubFolder = "/";
        }
        if ( file.exists( ) ) {
            File[ ] fs = file.listFiles( );
            if ( fs.length == 0 ) {
                int idx = currSubFolder.lastIndexOf("/");
                if(idx >= 0) {
                    currSubFolder = currSubFolder.substring(0, idx);
                }
                return;
            } else {
                for ( File f : fs ) {
                    if ( f.isDirectory( ) ) {
                        if(isDepth) {
                            currSubFolder =  Path.join(currSubFolder,f.getName());
                            traversalAllFolder(files,isDepth, f.getAbsolutePath(), currSubFolder,
                                    fileMark, mark);
                        }
                    } else {
                        if ( fileMark.isMark( f, mark ) ) {
                            String filename = StringUtils.isBlank( currSubFolder )
                                    ? f.getName( ) : currSubFolder + f.getName( );
                            files.add( filename );
                        }
                    }
                }
                int idx = currSubFolder.lastIndexOf("/");
                if(idx >= 0) {
                    currSubFolder = currSubFolder.substring(0, idx);
                }

            }
        }
    }

    public static void traversalAllFolder2( List< String > files,boolean isDepth ,String path,String rootpath, String currSubFolder,
                                           IFileMark fileMark, String mark ) {
        File file = new File( path );
        if(StringUtils.isBlank(rootpath)) {
            rootpath = "/";
        }
        if ( file.exists( ) ) {
            File[ ] fs = file.listFiles( );
            if ( fs.length == 0 ) {
                return;
            } else {
                for ( File f : fs ) {
                    if ( f.isDirectory( ) ) {
                        if(isDepth) {
                            currSubFolder =  Path.join(rootpath,f.getName());
                            traversalAllFolder2(files,isDepth, f.getAbsolutePath(),rootpath, currSubFolder,
                                    fileMark, mark);
                        }
                    } else {
                        if ( fileMark.isMark( f, mark ) ) {
                            String filename = StringUtils.isBlank( currSubFolder )
                                    ? f.getName( ) : Path.joinWithFilename(f.getName(),rootpath,currSubFolder);
                            files.add( filename );
                        }
                    }
                }
//                int idx = currSubFolder.lastIndexOf("/");
//                if(idx >= 0) {
//                    currSubFolder = currSubFolder.substring(0, idx);
//                }

            }
        }
    }



    public static String join( String... paths ) {
        if ( null == paths || 0 == paths.length ) return null;
//        StringBuilder sb = new StringBuilder( );
        String ps = File.separator;
//        for ( String p : paths ) {
//            if ( p.endsWith( ps ) ) sb.append( p );
//            else sb.append( p ).append( ps );
//        }
        boolean isLast = true;
        StringBuilder sb = null;
        boolean isLastAdd = false;

        for ( String p : paths ) {
            if(null == sb){
                sb = new StringBuilder();
                sb.append(p);
                isLast = p.endsWith(ps);
            } else {
                if (isLast && p.startsWith(File.separator)) {
                    sb.append(p.substring(1));
                } else if(!isLast && !p.startsWith(File.separator)) {
                    sb.append(File.separator).append(p);
                } else {
                    sb.append(p);
                }
            }
        }
        return sb.toString( );
//        boolean isBeginWith = false;
//        StringBuffer sb = new StringBuffer();
//            for(String s : paths){
//                if(s.sta)
//            }



    }

    public static String joinWithFilename( String filename, String... paths ) {
        if ( null == paths || 0 == paths.length ) return filename;
        return join( paths ) + filename;
    }

    public static boolean isExist( String path ) {
        File f = new File( path );
        return f.exists( );
    }


    /**
     * Return a resource bundle using the specified base name.
     *
     * @param baseName the base name of the resource bundle, a fully qualified class name
     * @return a resource bundle for the given base name
     */
    public static ResourceBundle getBundle(String baseName) {
        return getBundle(baseName, Locale.getDefault());
    }

    /**
     * Return a resource bundle using the specified base name and locale.
     *
     * @param baseName the base name of the resource bundle, a fully qualified class name
     * @param locale the locale for which a resource bundle is desired
     * @return a resource bundle for the given base name and locale
     */
    public static ResourceBundle getBundle(String baseName, Locale locale) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(baseName, locale, classLoader);
    }

    public static  StringBuffer readLineFile(String filename){
        InputStreamReader inputReader = null;
        BufferedReader bufferReader = null;
        OutputStream outputStream = null;
        StringBuffer buffer = new StringBuffer();

        try
        {
            InputStream inputStream = new FileInputStream(filename);
            inputReader = new InputStreamReader(inputStream);
            bufferReader = new BufferedReader(inputReader);
            // 读取一行
            String line = null;
            while ((line = bufferReader.readLine()) != null)
            {
                buffer.append(line);
            }

        }
        catch (IOException e)
        {
        }
        finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if (null != bufferReader) {
                try {
                    bufferReader.close();
                } catch (IOException e) {
                }
            }
            if (null != inputReader) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                }
            }
        }
        return buffer;
    }

    public static String relativeToAbsolute(String currentPath,String relativePath){
        //begin with /
        if(relativePath.startsWith(File.separator)){
            return relativePath;
        }

        String path = currentPath;
        File f = new File(currentPath);
        if(f.isFile()) {
            path = FilenameUtils.getPath(currentPath);
        }


        //begin with . means current path
        if(!relativePath.startsWith("..")){
            if(relativePath.startsWith(".")){
                return joinWithFilename(relativePath.substring(1),currentPath);
            } else {
                return joinWithFilename(relativePath,currentPath);
            }
        }

        //begin with .. means back path
        int count = StringUtils.countMatches(relativePath,"..");
        if(path.endsWith(File.separator)){
            path = path.substring(0,path.length() - 1);
        }

        int idx = StringUtils.lastIndexOf(relativePath,"..");
        String realpath = relativePath.substring(idx + 1);

        String[] paths = StringUtils.split(path,File.separatorChar);
        int basepathCount = paths.length - count;
        if(0 > basepathCount) throw new RuntimeException("parent folder is so short.");
        if(0 == basepathCount) return realpath;
        String[] basePaths = new String[basepathCount];
        for(int i = 0; i < basepathCount; i++){
            basePaths[i] = paths[i];
        }

        String basepath = StringUtils.join(basePaths,File.separator);
        return joinWithFilename(realpath,basepath);
    }

}
