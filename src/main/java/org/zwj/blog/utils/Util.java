package org.zwj.blog.utils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static boolean valid(Map map) {
        if (map != null && !map.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean valid(Boolean bool) {
        if (bool != null && bool) {
            return true;
        }
        return false;
    }

    public static boolean valid(String string) {
        if (string != null && !"".equals(string)) {
            return true;
        }
        return false;
    }

    public static boolean valid(Integer integer) {
        if (integer != null && integer > 0) {
            return true;
        }
        return false;
    }

    public static boolean valid(Float f) {
        if (f != null && f > 0) {
            return true;
        }
        return false;
    }

    public static boolean valid(Double d) {
        if (d != null && d > 0) {
            return true;
        }
        return false;
    }

    public static boolean valid(Long l) {
        if (l != null && l > 0) {
            return true;
        }
        return false;
    }

    public static boolean valid(Object[] objects) {
        if (objects != null && objects.length > 0) {
            return true;
        }
        return false;
    }

    public static boolean valid(Collection<Object> collection) {
        if (collection != null && !collection.isEmpty()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static boolean valid(Object object) {
        if (object != null) {
            if (object instanceof Object[]) {
                return valid((Object[]) object);
            } else if (object instanceof Collection<?>) {
                return valid((Collection<Object>) object);
            } else if (object instanceof Map) {
                return valid( (Map) object);
            } else if (object instanceof Boolean) {
                return valid((Boolean) object);
            } else if (object instanceof String) {
                return valid((String) object);
            } else if (object instanceof Integer) {
                return valid((Integer) object);
            } else if (object instanceof Long) {
                return valid((Long) object);
            } else if (object instanceof Float) {
                return valid((Float) object);
            } else if (object instanceof Double) {
                return valid((Double) object);
            }
            return true;
        }
        return false;
    }

    public static <T> T getFirst(Collection<T> collection) {
        if (collection != null && !collection.isEmpty()) {
            return collection.stream().findFirst().get();
        } else {
            return null;
        }
    }

    public static String randomUUIDToString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isSpecialChar(String str) {
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\n|\r|\t";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }


}
