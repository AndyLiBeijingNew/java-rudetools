package net.sf.rudetools.common.util.logback;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import net.sf.rudetools.common.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DebugLogger {

    private Logger logger;
    private String className;
    private static final int MAX_FIELD_VALUE_LENGTH = 300;

    /** Debug format increment */
    private ThreadLocal<String> increment = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            return "";
        }
    };

    public DebugLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.className = clazz.getSimpleName();
    }

    /**
     * Log the request message in Debug log.
     * 
     * @param funcName
     * @param request
     */
    public void funcStart(String funcName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            increment.set(increment.get() + "\t");
            StringBuffer sb = new StringBuffer();
            if (parameters != null && parameters.length > 0) {
                for (Object parameter : parameters) {
                    sb.append(getVarDetails(parameter));
                }
            }
            logger.debug("{} >>>>>> {}.{}(){}", increment.get(), className, funcName, sb.toString());
        }
    }

    public void funcMsg(String msg) {
        logger.debug("\t{} >>>>>> {} - {}", increment.get(), className, msg);
    }

    /**
     * Log the response message in Debug log.
     * 
     * @param methodName
     * @param message
     */
    /**
     * @param methodName
     * @param response
     */
    public void funcEnd(String funcName, Object... parameters) {
        if (logger.isDebugEnabled()) {
            StringBuffer sb = new StringBuffer();
            if (parameters != null && parameters.length > 0) {
                for (Object parameter : parameters) {
                    sb.append(getVarDetails(parameter));
                }
            }
            logger.debug("{} <<<<<< {}.{}(){}", increment.get(), className, funcName, sb.toString());

            String incre = increment.get();
            if (incre.endsWith("\t")) {
                increment.set(incre.substring(0, incre.lastIndexOf("\t")));
            }
        }
    }

    public void printSimpleVar(Object var) {
        if (logger.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();
            String incre = "\n\t" + increment.get();
            msg.append(incre).append("----------------------------------------");
            String toString = var.toString();
            if (toString.length() > 80) {
                toString = toString.substring(0, 80);
            }

            if (toString.endsWith("\n")) {
                toString = toString.substring(0, toString.length() - 1);
            }
            msg.append(incre).append("| ").append(var.getClass().getSimpleName()).append("=[").append(toString)
                    .append("]");
            msg.append(incre).append("----------------------------------------");

            logger.debug(msg.toString());
        }
    }

    public void printSimpleVar(String varName, Object var) {
        if (logger.isDebugEnabled()) {
            StringBuffer msg = new StringBuffer();
            String incre = "\n\t" + increment.get();
            msg.append(incre).append("----------------------------------------");
            msg.append(incre).append("| ").append(varName).append("=[").append(var.toString()).append("]");
            msg.append(incre).append("----------------------------------------");
            logger.debug(msg.toString());
        }
    }

    public void printVar(Object var) {
        if (logger.isDebugEnabled()) {
            logger.debug(getVarDetails(var));
        }
    }

    private String getVarDetails(Object var) {
        StringBuffer msg = new StringBuffer();
        String incre = "\n\t" + increment.get();
        // logger.debug("\t{}----------------------------------------",
        // increment.get());
        msg.append(incre).append("----------------------------------------");
        if (var == null) {
            // logger.debug("\t{}| Var Class:\t NULL",
            // increment.get());
            msg.append(incre).append("| Var Class:\t NULL");
        } else {
            // logger.debug("\t{}| Var Class:\t{}", increment.get(),
            // StringUtil.toString(var));
            msg.append(incre).append("| Var Class:\t").append(StringUtil.toString(var));
            Class<?> clazz = var.getClass();
//            if (!(var instanceof GeneratedMessage)) {
//                // logger.debug("\t{}| \ttoString=[{}]",
//                // increment.get(), var.toString());
//                msg.append(incre).append("| \ttoString=[").append(var.toString()).append("]");
//            }

            int i = 0;
            Field[] fields = null;
            // for parent fields
            Class<?> fatherClazz = clazz.getSuperclass();
            if (fatherClazz != null) {
                fields = fatherClazz.getDeclaredFields();
                for (Field field : fields) {
                    try {
                        int mod = field.getModifiers();
                        if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod) && !Modifier.isTransient(mod)) {
                            field.setAccessible(true);
                            Object obj = field.get(var);
                            String varValue = null;
                            if (obj != null) {
                                varValue = obj.toString();
                                // more than length, the next line
                                // index.
                                int lastCr = varValue.indexOf("\n", MAX_FIELD_VALUE_LENGTH);
                                if (lastCr > 0) {
                                    varValue = varValue.substring(0, lastCr) + "\n ... \n";
                                }
                                if (varValue.contains("\n")) {
                                    varValue = varValue.replace("\n", incre + "| \t\t\t\t");
                                }
                            }
                            msg.append(incre).append("| \tfield ").append(i++).append(":\t")
                                    .append(getFieldName(field)).append("=[").append(varValue).append("]");
                        }
                    } catch (IllegalArgumentException e) {
                        logger.warn(e.getMessage());
                    } catch (IllegalAccessException e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
            // for self fields
            fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                try {
                    int mod = field.getModifiers();
                    if (!Modifier.isStatic(mod) && !Modifier.isFinal(mod) && !Modifier.isTransient(mod)) {
                        field.setAccessible(true);
                        Object obj = field.get(var);
                        String varValue = null;
                        if (obj != null) {
                            varValue = obj.toString();
                            // more than length, the next line index.
                            int lastCr = varValue.indexOf("\n", MAX_FIELD_VALUE_LENGTH);
                            if (lastCr > 0) {
                                varValue = varValue.substring(0, lastCr) + "\n ... \n";
                            }
                            if (varValue.contains("\n")) {
                                varValue = varValue.replace("\n", incre + "| \t\t\t\t");
                            }
                        }
                        msg.append(incre).append("| \tfield ").append(i++).append(":\t").append(getFieldName(field))
                                .append("=[").append(varValue).append("]");
                    }
                } catch (IllegalArgumentException e) {
                    logger.warn(e.getMessage());
                } catch (IllegalAccessException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        msg.append(incre).append("----------------------------------------");
        return msg.toString();
    }

    private String getFieldName(Field field) {
        StringBuffer sb = new StringBuffer();
        sb.append(getModifiers(field.getModifiers()));
        sb.append(field.getType().getSimpleName()).append(" ");
        sb.append(field.getName());

        return sb.toString();
    }

    private String getModifiers(int mod) {
        StringBuffer sb = new StringBuffer();
        if (Modifier.isTransient(mod)) {
            sb.append("transient ");
        }

        if (Modifier.isVolatile(mod)) {
            sb.append("volatile ");
        }

        if (Modifier.isPublic(mod)) {
            sb.append("public ");
        } else if (Modifier.isProtected(mod)) {
            sb.append("protected ");
        } else if (Modifier.isPrivate(mod)) {
            sb.append("private ");
        }

        if (Modifier.isStatic(mod)) {
            sb.append("static ");
        }

        if (Modifier.isFinal(mod)) {
            sb.append("final ");
        }

        return sb.toString();
    }
}
