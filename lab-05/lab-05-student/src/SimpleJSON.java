import java.util.*;

/**
 * SimpleJSON — a minimal JSON parser that covers the subset needed
 * for world_config.json (objects, arrays, strings, ints, booleans).
 *
 * No external dependencies required.
 *
 * Usage:
 *   SimpleJSON.JObject root = SimpleJSON.parseObject(jsonString);
 *   int w = root.getInt("width");
 *   SimpleJSON.JArray arr = root.getArray("tiles");
 *   SimpleJSON.JObject tile = arr.getObject(0);
 */
public class SimpleJSON {

    // ----------------------------------------------------------------
    // Public types
    // ----------------------------------------------------------------

    public static class JObject {
        private final Map<String, Object> map = new LinkedHashMap<>();

        void put(String key, Object val) { map.put(key, val); }

        public String  getString (String k) { return (String)  map.get(k); }
        public int     getInt    (String k) { return ((Number) map.get(k)).intValue(); }
        public boolean getBoolean(String k) { return (Boolean) map.get(k); }
        public JObject getObject (String k) { return (JObject) map.get(k); }
        public JArray  getArray  (String k) { return (JArray)  map.get(k); }
        public boolean has       (String k) { return map.containsKey(k); }

        /** Returns null (not exception) if key absent. */
        public JArray optArray(String k) {
            Object v = map.get(k);
            return (v instanceof JArray) ? (JArray) v : null;
        }
    }

    public static class JArray {
        private final List<Object> list = new ArrayList<>();

        void add(Object val) { list.add(val); }
        public int size()    { return list.size(); }

        public JObject getObject(int i) { return (JObject) list.get(i); }
        public String  getString(int i) { return (String)  list.get(i); }
        public int     getInt   (int i) { return ((Number) list.get(i)).intValue(); }
    }

    // ----------------------------------------------------------------
    // Entry point
    // ----------------------------------------------------------------

    public static JObject parseObject(String json) {
        int[] pos = {0};
        skipWhitespace(json, pos);
        return readObject(json, pos);
    }

    // ----------------------------------------------------------------
    // Internal recursive descent
    // ----------------------------------------------------------------

    private static Object readValue(String s, int[] pos) {
        skipWhitespace(s, pos);
        char c = s.charAt(pos[0]);
        if (c == '{')  return readObject(s, pos);
        if (c == '[')  return readArray(s, pos);
        if (c == '"')  return readString(s, pos);
        if (c == 't')  { pos[0] += 4; return true; }
        if (c == 'f')  { pos[0] += 5; return false; }
        if (c == 'n')  { pos[0] += 4; return null; }
        return readNumber(s, pos);
    }

    private static JObject readObject(String s, int[] pos) {
        JObject obj = new JObject();
        pos[0]++; // skip '{'
        skipWhitespace(s, pos);
        while (s.charAt(pos[0]) != '}') {
            skipWhitespace(s, pos);
            String key = readString(s, pos);
            skipWhitespace(s, pos);
            pos[0]++; // skip ':'
            skipWhitespace(s, pos);
            Object val = readValue(s, pos);
            obj.put(key, val);
            skipWhitespace(s, pos);
            if (s.charAt(pos[0]) == ',') pos[0]++;
            skipWhitespace(s, pos);
        }
        pos[0]++; // skip '}'
        return obj;
    }

    private static JArray readArray(String s, int[] pos) {
        JArray arr = new JArray();
        pos[0]++; // skip '['
        skipWhitespace(s, pos);
        while (s.charAt(pos[0]) != ']') {
            arr.add(readValue(s, pos));
            skipWhitespace(s, pos);
            if (s.charAt(pos[0]) == ',') pos[0]++;
            skipWhitespace(s, pos);
        }
        pos[0]++; // skip ']'
        return arr;
    }

    private static String readString(String s, int[] pos) {
        pos[0]++; // skip opening '"'
        StringBuilder sb = new StringBuilder();
        while (pos[0] < s.length()) {
            char c = s.charAt(pos[0]);
            if (c == '"') { pos[0]++; break; }
            if (c == '\\') {
                pos[0]++;
                char esc = s.charAt(pos[0]);
                sb.append(switch (esc) {
                    case 'n' -> '\n'; case 't' -> '\t';
                    case 'r' -> '\r'; default  -> esc;
                });
            } else {
                sb.append(c);
            }
            pos[0]++;
        }
        return sb.toString();
    }

    private static Number readNumber(String s, int[] pos) {
        int start = pos[0];
        while (pos[0] < s.length()) {
            char c = s.charAt(pos[0]);
            if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) break;
            pos[0]++;
        }
        String num = s.substring(start, pos[0]);
        return num.contains(".") ? Double.parseDouble(num) : Long.parseLong(num);
    }

    private static void skipWhitespace(String s, int[] pos) {
        while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) {
            pos[0]++;
        }
    }
}
