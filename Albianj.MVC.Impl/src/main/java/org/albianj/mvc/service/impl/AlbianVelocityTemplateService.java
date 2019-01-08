package org.albianj.mvc.service.impl;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.mvc.View;
import org.albianj.mvc.config.AlbianHttpConfigurtion;
import org.albianj.mvc.config.ViewConfigurtion;
import org.albianj.mvc.impl.ctags.velocity.MasterViewTag;
import org.albianj.mvc.service.IAlbianTemplateService;
import org.albianj.mvc.service.TemplateException;
import org.albianj.service.*;
import org.albianj.service.parser.AlbianParserException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.io.VelocityWriter;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;
import org.apache.velocity.util.SimplePool;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

//import org.albianj.mvc.impl.ctags;


/**
 * Created by xuhaifeng on 16/12/1.
 */
public class AlbianVelocityTemplateService extends FreeAlbianService implements IAlbianTemplateService {

    protected static final int WRITER_BUFFER_SIZE = 32 * 1024;
    protected SimplePool writerPool = new SimplePool(40);
    @AlbianServiceFieldRant(Type = AlbianServiceFieldType.Ref, Value = "AlbianMvcConfigurtionService.HttpConfigurtion",SetterLifetime = AlbianServiceFieldSetterLifetime.AfterNew)
    private AlbianHttpConfigurtion c;

    public String getServiceName() {
        return Name;
    }

    public void setHttpConfigurtion(AlbianHttpConfigurtion c) {
        this.c = c;
    }


    public void loading() throws AlbianParserException {

        Properties props = new Properties();
        props.setProperty("resource.loader", "file");
        props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
        props.setProperty("file.resource.loader.path", c.getRootPath());
        props.put("input.encoding", c.getCharset());
        props.put("output.encoding", c.getCharset());

        try {
            Properties up = getInitProperties();
            props.putAll(up);
        } catch (Exception e) {
            throw new AlbianParserException(e);
        }
        Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM, new LogChuteAdapter());
        if (props.containsKey("userdirective")) {
            String v = (String) props.get("userdirective");
            props.put("userdirective", v + "," + MasterViewTag.class.getName());
        } else {
            props.put("userdirective", MasterViewTag.class.getName());
        }

        Velocity.init(props);

    }

    protected Properties getInitProperties() throws IOException {

        // Load user velocity properties.
        Properties userProperties = new Properties();

        String filename = null;
        try {
            filename = Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath()
                    + "velocity.properties");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (Path.isExist(filename)) {
            InputStream inputStream = new FileInputStream(filename);

            if (inputStream != null) {
                try {
                    userProperties.load(inputStream);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException ioe) {
                    }
                }
            }
        }
        return userProperties;
    }


    public void renderTemplate(View page, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException {

        ViewConfigurtion pc = c.getPages().get(page.getClass().getName());
        String templatePath = pc.getTemplate();

        internalRenderTemplate(templatePath, page, model, funcs, writer);
    }

    public void renderTemplate(String templatePath, Map<String, ?> model, Map<String, Class<?>> funcs, Writer writer)
            throws IOException, TemplateException {

        internalRenderTemplate(templatePath, null, model, funcs, writer);
    }

    protected void internalRenderTemplate(String templatePath,
                                          View page,
                                          Map<String, ?> model,
                                          Map<String, Class<?>> funcs,
                                          Writer writer)
            throws IOException, TemplateException {

        VelocityContext velocityContext = new VelocityContext(model);

        // May throw parsing error if template could not be obtained
        Template template = null;
        VelocityWriter velocityWriter = null;
        try {
            String charset = c.getCharset();
            if (charset != null) {
                template = Velocity.getTemplate(templatePath, charset);

            } else {
                template = Velocity.getTemplate(templatePath);
            }

            velocityWriter = (VelocityWriter) writerPool.get();

            if (velocityWriter == null) {
                velocityWriter =
                        new VelocityWriter(writer, WRITER_BUFFER_SIZE, true);
            } else {
                velocityWriter.recycle(writer);
            }

            template.merge(velocityContext, velocityWriter);

        } catch (ParseErrorException pee) {
            throw pee;

        } catch (TemplateInitException tie) {
            throw tie;
        } catch (Exception error) {
            throw error;
        } finally {
            if (velocityWriter != null) {
                velocityWriter.flush();
                velocityWriter.recycle(null);
                writerPool.put(velocityWriter);
            }

            writer.flush();
            writer.close();
        }
    }


    public StringBuffer renderTemplate(Map params, Map<String, Class<?>> funcs, String vmContext) {
        VelocityContext context = new VelocityContext(params);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "", vmContext);

        return writer.getBuffer();
    }


// Inner Classes ----------------------------------------------------------

    /**
     * Provides a Velocity <tt>LogChute</tt> adapter class around the application
     * log service to enable the Velocity Runtime to log to the application
     * LogService.
     * <p/>
     * Velocity logging.
     * <p/>
     * <b>PLEASE NOTE</b> this class is <b>not</b> for public use.
     */
    public static class LogChuteAdapter implements LogChute {

        /**
         * The logger instance Velocity application attribute key.
         */
        private static final String LOG_INSTANCE =
                LogChuteAdapter.class.getName() + ".LOG_INSTANCE";

        /**
         * The velocity logger instance Velocity application attribute key.
         */
        private static final String LOG_LEVEL =
                LogChuteAdapter.class.getName() + ".LOG_LEVEL";


        private static final String MSG_PREFIX = "Velocity: ";

        /**
         * The log level.
         */
        protected int logLevel;

        IAlbianLoggerService logger = null;

        /**
         * Initialize the logger instance for the Velocity runtime. This method
         * is invoked by the Velocity runtime.
         *
         * @param rs the Velocity runtime services
         * @throws Exception if an initialization error occurs
         * @see LogChute#init(RuntimeServices)
         */
        public void init(RuntimeServices rs) throws Exception {

//            // Swap the default logger instance with the global application logger
//            ConfigService configService = (ConfigService)
//                    rs.getApplicationAttribute(ConfigService.class.getName());
//
//            this.logger = configService.getLogService();

            logger = AlbianServiceRouter.getLogger();
//            Integer level = (Integer) rs.getApplicationAttribute(LOG_LEVEL);
//            if (level != null) {
//                logLevel = level;
//
//            } else {
//                String msg = "Could not retrieve LOG_LEVEL from Runtime attributes";
//                throw new IllegalStateException(msg);
//            }

            rs.setApplicationAttribute(LOG_INSTANCE, this);
        }

        /**
         * Tell whether or not a log level is enabled.
         *
         * @param level the logging level to test
         * @return true if the given logging level is enabled
         * @see LogChute#isLevelEnabled(int)
         */
        public boolean isLevelEnabled(int level) {
            if (level <= LogChute.TRACE_ID) {
                return true;

            } else if (level <= LogChute.DEBUG_ID) {
                return true;

            } else if (level <= LogChute.INFO_ID) {
                return true;

            } else if (level == LogChute.WARN_ID) {
                return true;

            } else {
                return false;
            }
        }

        /**
         * Log the given message and optional error at the specified logging level.
         *
         * @param level   the logging level
         * @param message the message to log
         * @see LogChute#log(int, String)
         */
        public void log(int level, String message) {
            if (level < logLevel) {
                return;
            }

//            if (level == TRACE_ID) {
//                logger.trace(MSG_PREFIX + message);

//            } else
            if (level == DEBUG_ID) {
                logger.debug(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message);

            } else if (level == INFO_ID) {
                logger.info(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message);

            } else if (level == WARN_ID) {
                logger.warn(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message);

            } else if (level == ERROR_ID) {
                logger.error(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message);

            } else {
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
        }

        /**
         * Log the given message and optional error at the specified logging level.
         * <p/>
         * If you need to customise the Click and Velocity runtime logging for your
         * application modify this method.
         *
         * @param level   the logging level
         * @param message the message to log
         * @param error   the optional error to log
         * @see LogChute#log(int, String, Throwable)
         */
        public void log(int level, String message, Throwable error) {
            if (level < logLevel) {
                return;
            }

//            if (level == TRACE_ID) {
//                logger.trace(MSG_PREFIX + message, error);
//
//            } else
            if (level == DEBUG_ID) {
                logger.debug(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message, error);

            } else if (level == INFO_ID) {
                logger.info(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message, error);

            } else if (level == WARN_ID) {
                logger.warn(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message, error);

            } else if (level == ERROR_ID) {
                logger.error(IAlbianLoggerService.AlbianRunningLoggerName, MSG_PREFIX + message, error);

            } else {
                throw new IllegalArgumentException("Invalid log level: " + level);
            }
        }

    }


}
