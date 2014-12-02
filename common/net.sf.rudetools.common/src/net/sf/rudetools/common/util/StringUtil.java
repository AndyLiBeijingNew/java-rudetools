package net.sf.rudetools.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {

    protected static String NULL = "NULL";

    protected static String BASH_ERROR = "bash:(.*)$";
    protected static Pattern BASH_ERROR_PATTERN = Pattern.compile(BASH_ERROR);

    public static String extractFileName(String fullPath) {
        String fileName = null;
        if (fullPath != null) {
            int indexOfColon = fullPath.indexOf(":");
            if (indexOfColon >= 0) {
                fullPath = fullPath.substring(0, indexOfColon);
            }
            // remove the last slash from end
            while (fullPath.endsWith("/")) {
                fullPath = fullPath.substring(0, fullPath.length() - 1);
            }
            // slash will not the end
            int indexOfSlash = fullPath.lastIndexOf("/");
            if (indexOfSlash >= 0) {
                fileName = fullPath.substring(indexOfSlash + 1);
            } else {
                fileName = fullPath;
            }
        }

        return fileName;
    }

    public static String formatInt(int no, int length) {
        String format = "%0" + length + "d";
        return String.format(format, no);
    }

    public static String getDateString(long timeMillis) {
        return getCommonDateString(timeMillis);
    }

    public static String getCommonDateString() {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        return df.format(new Date());
    }

    public static String getCommonDateString(long timeMillis) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
        return df.format(new Date(timeMillis));
    }

    public static String getDateNumberString() {
        DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
        return df.format(new Date());
    }

    public static String getShortDate(long timeMillis) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        return df.format(timeMillis);
    }

    public static String getMediumTime(long timeMillis) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        return df.format(timeMillis);
    }

    public static String getLinuxLibName(String string) {
        if (string != null && string.startsWith("lib") && string.endsWith(".so")) {
            int length = string.length();
            string = string.substring(3, length - 3);
        }
        return string;
    }

    public static String getSimplePname(String pname) {
        if (pname == null) {
            return "";
        }
        // Used only for Linux system.
        String pathSeparator = "/";
        if (pname.startsWith(pathSeparator)) {
            int index = pname.lastIndexOf(pathSeparator);
            pname = pname.substring(index + 1);
        }

        return pname;
    }

    public static String parseBashErrorString(String string) {

        String bashError = null;
        String[] lines = string.split("\n");
        for (String line : lines) {
            Matcher matcher = BASH_ERROR_PATTERN.matcher(line);
            if (matcher.find()) {
                bashError = matcher.group(1);
                break;
            }
        }
        return bashError;
    }

    public static String showHexValue(long value) {
        if (value == 0) {
            return NULL;
        }

        return String.format("%#x", value);
    }

    /**
     * If we get a byte, we need consider the signed char or un-signed
     * char. In Java there is no String.valueOf( byte), the following
     * will parse the byte as a integer. value =
     * String.valueOf((char)valBuffer.get());
     * 
     * @param byteValue
     * @return
     */
    public static String showByteValue(byte byteValue) {
        String value = String.valueOf(byteValue);
        if (0 < byteValue && byteValue < 128) {
            // e.g. "65 'A'"
            value += " '" + (char) byteValue + "'";
        }
        return value;
    }

    /**
     * Show the byte array value to a string.
     * 
     * @param byteArray
     * @return
     */
    public static String showByteValue(byte[] byteArray) {
        if (byteArray == null) {
            return NULL;
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        if (byteArray.length > 0) {
            sb.append(byteArray[0]);
        }
        for (int index = 1; index < byteArray.length; index++) {
            sb.append(", ").append(byteArray[index]);
        }
        sb.append("]");
        return sb.toString();
    }

    public static String showValue(String string) {
        if (string == null) {
            return NULL;
        }
        return "\"" + string + "\"";
    }

    /**
     * @param x
     * @return
     */
    public static String showUnsignedByte(byte x) {
        short num = (short) (x & 0xff);
        return String.valueOf(num);
    }

    /**
     * @param x
     * @return
     */
    public static String showUnsignedInt(int x) {
        long num = x & 0xffffffffL;
        return String.valueOf(num);
    }

    /**
     * @param x
     * @return
     */
    public static String showUnsignedShort(short x) {
        int num = x & 0xffff;
        return String.valueOf(num);
    }

    /**
     * Create the string with step format. i.e. (033/130)
     * 
     * @param i
     * @param count
     * @return
     */
    public static String toPartOfString(int i, int count) {
        int length = String.valueOf(count).length();
        String step = formatInt(i, length);

        return "(" + step + "/" + count + ")";
    }

    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.getClass().getSimpleName() + "@" + Integer.toHexString(obj.hashCode());
    }

    public static int parseToInt(String value) {
        int ret;
        try {
            ret = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            ret = -1;
        }

        return ret;
    }
}
