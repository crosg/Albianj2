package org.albianj.mvc.service.impl;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.mvc.service.IAlbianMimeTypeService;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created by xuhaifeng on 16/12/13.
 */
public class AlbianMimeTypeService extends FreeAlbianParserService implements IAlbianMimeTypeService {

    public String getServiceName(){
        return Name;
    }


    private Properties mtProperties = new Properties();

    @Override
    public void init() throws AlbianParserException {
        // Load user velocity properties.

        Properties mt = initMimeTypeDefault();
        mtProperties.putAll(mt);
        String filename = null;
        try {
            filename = Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath()
                    + "mimetype.properties");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (Path.isExist(filename)) {
            InputStream inputStream = AlbianClassLoader.getInstance().getResourceAsStream(filename);

            if (inputStream != null) {
                try {
                    mtProperties.load(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        }
    }

    private Properties initMimeTypeDefault() {
        Properties mt = new Properties();
        mt.put("abs", "audio/x-mpeg");
        mt.put("ai", "application/postscript");
        mt.put("aif", "audio/x-aiff");
        mt.put("aifc", "audio/x-aiff");
        mt.put("aiff", "audio/x-aiff");
        mt.put("aim", "application/x-aim");
        mt.put("art", "image/x-jg");
        mt.put("asf=", "ideo/x-ms-asf");
        mt.put("asx", "video/x-ms-asf");
        mt.put("au", "audio/basic");
        mt.put("avi", "video/x-msvideo");
        mt.put("avx", "video/x-rad-screenplay");
        mt.put("bcpio", "application/x-bcpio");
        mt.put("bin", "application/octet-stream");
        mt.put("bmp", "image/bmp");
        mt.put("body", "text/html");
        mt.put("cdf", "application/x-cdf");
        mt.put("cer", "application/x-x509-ca-cert");
        mt.put("class", "application/java");
        mt.put("cpio", "application/x-cpio");
        mt.put("csh", "application/x-csh");
        mt.put("css", "text/css");
        mt.put("csv", "text/csv");
        mt.put("dib", "image/bmp");
        mt.put("doc", "application/msword");
        mt.put("dtd", "application/xml-dtd");
        mt.put("dv", "video/x-dv");
        mt.put("dvi", "application/x-dvi");
        mt.put("eps", "application/postscript");
        mt.put("etx", "text/x-setext");
        mt.put("exe", "application/octet-stream");
        mt.put("gif", "image/gif");
        mt.put("gtar", "application/x-gtar");
        mt.put("gz", "application/x-gzip");
        mt.put("hdf", "application/x-hdf");
        mt.put("hqx", "application/mac-binhex40");
        mt.put("htc", "text/x-component");
        mt.put("htm", "text/html");
        mt.put("html", "text/html");
        mt.put("ief", "image/ief");
        mt.put("jad", "text/vnd.sun.j2me.app-descriptor");
        mt.put("jar", "application/java-archive");
        mt.put("code", "text/plain");
        mt.put("jnlp", "application/x-java-jnlp-file");
        mt.put("jpe", "image/jpeg");
        mt.put("jpeg", "image/jpeg");
        mt.put("jpg", "image/jpeg");
        mt.put("js", "text/javascript");
        mt.put("jsf", "text/plain");
        mt.put("json", "application/json");
        mt.put("jspf", "text/plain");
        mt.put("kar", "audio/x-midi");
        mt.put("latex", "application/x-latex");
        mt.put("m3u", "audio/x-mpegurl");
        mt.put("mac", "image/x-macpaint");
        mt.put("man", "application/x-troff-man");
        mt.put("mathml", "application/mathml+xml");
        mt.put("me", "application/x-troff-me");
        mt.put("mid", "audio/x-midi");
        mt.put("midi", "audio/x-midi");
        mt.put("mif", "application/x-mif");
        mt.put("mov", "video/quicktime");
        mt.put("movie", "video/x-sgi-movie");
        mt.put("mp1", "audio/x-mpeg");
        mt.put("mp2", "audio/x-mpeg");
        mt.put("mp3", "audio/x-mpeg");
        mt.put("mpa", "audio/x-mpeg");
        mt.put("mpe", "video/mpeg");
        mt.put("mpeg", "video/mpeg");
        mt.put("mpega", "audio/x-mpeg");
        mt.put("mpg", "video/mpeg");
        mt.put("mpv2", "video/mpeg2");
        mt.put("ms", "application/x-wais-source");
        mt.put("msg", "application/vnd.ms-outlook");
        mt.put("nc", "application/x-netcdf");
        mt.put("oda", "application/oda");
        mt.put("ogg", "application/ogg");
        mt.put("pbm", "image/x-portable-bitmap");
        mt.put("pct", "image/pict");
        mt.put("pdf", "application/pdf");
        mt.put("pgm", "image/x-portable-graymap");
        mt.put("pic", "image/pict");
        mt.put("pict", "image/pict");
        mt.put("pls", "audio/x-scpls");
        mt.put("png", "image/png");
        mt.put("pnm", "image/x-portable-anymap");
        mt.put("pnt", "image/x-macpaint");
        mt.put("ppm", "image/x-portable-pixmap");
        mt.put("ppt", "application/powerpoint");
        mt.put("ps", "application/postscript");
        mt.put("psd", "image/x-photoshop");
        mt.put("qt", "video/quicktime");
        mt.put("qti", "image/x-quicktime");
        mt.put("qtif", "image/x-quicktime");
        mt.put("ras", "image/x-cmu-raster");
        mt.put("rdf", "application/rdf+xml");
        mt.put("rgb", "image/x-rgb");
        mt.put("rm", "application/vnd.rn-realmedia");
        mt.put("roff", "application/x-troff");
        mt.put("rtf", "application/rtf");
        mt.put("rtx", "text/richtext");
        mt.put("sh", "application/x-sh");
        mt.put("shar", "application/x-shar");
        mt.put("shtml", "text/x-server-parsed-html");
        mt.put("smf", "audio/x-midi");
        mt.put("sit", "application/x-stuffit");
        mt.put("snd", "audio/basic");
        mt.put("src", "application/x-wais-source");
        mt.put("sv4cpio", "application/x-sv4cpio");
        mt.put("sv4crc", "application/x-sv4crc");
        mt.put("svgxml", "image/svg+xml");
        mt.put("swf", "application/x-shockwave-flash");
        mt.put("t", "application/x-troff");
        mt.put("tar", "application/x-tar");
        mt.put("tcl", "application/x-tcl");
        mt.put("tex", "application/x-tex");
        mt.put("texi", "application/x-texinfo");
        mt.put("texinfo", "application/x-texinfo");
        mt.put("tif", "image/tiff");
        mt.put("tiff", "image/tiff");
        mt.put("tr", "application/x-troff");
        mt.put("tsv", "text/tab-separated-values");
        mt.put("txt", "text/plain");
        mt.put("ulw", "audio/basic");
        mt.put("ustar", "application/x-ustar");
        mt.put("vxml", "application/voicexml+xml");
        mt.put("xbm", "image/x-xbitmap");
        mt.put("xht", "application/xhtml+xml");
        mt.put("xhtml", "application/xhtml+xml");
        mt.put("xls", "application/vnd.ms-excel");
        mt.put("xml", "application/xml");
        mt.put("xpm", "image/x-xpixmap");
        mt.put("xsl", "application/xml");
        mt.put("xslt", "application/xslt+xml");
        mt.put("xul", "application/vnd.mozilla.xul+xml");
        mt.put("xwd", "image/x-xwindowdump");
        mt.put("wav", "audio/x-wav");
        mt.put("svg", "image/svg");
        mt.put("svgz", "image/svg");
        mt.put("vsd", "application/x-visio");
        mt.put("wbmp", "image/vnd.wap.wbmp");
        mt.put("wml", "text/vnd.wap.wml");
        mt.put("wmlc", "application/vnd.wap.wmlc");
        mt.put("wmls", "text/vnd.wap.wmlscript");
        mt.put("wmlscriptc", "application/vnd.wap.wmlscriptc");
        mt.put("wrl", "x-world/x-vrml");
        mt.put("Z", "application/x-compress");
        mt.put("z", "application/x-compress");
        mt.put("zip", "application/zip");
        return mt;
    }

    @Override
    public String getMimeType(String key) {
        if (Validate.isNullOrEmptyOrAllSpace(key))
            return "text/html";
        Object o = mtProperties.get(key);
        if (null == o) return "text/html";

        return o.toString();
    }
}
