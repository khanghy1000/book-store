package io.dedyn.hy.watchworldshop.utils;

public class SlugifyUtil {
    public static String slugify(String name) {
        name = name.toLowerCase();
        // khai báo mảng chứa các ký tự tiếng việt có dấu
        String[] a = {"à", "á", "ạ", "ả", "ã", "â", "ầ", "ấ", "ậ", "ẩ", "ẫ", "ă", "ắ", "ằ", "ắ", "ặ", "ẳ", "ẵ", "a"};
        String[] d = {"đ", "d"};
        String[] e = {"è", "é", "ẹ", "ẻ", "ẽ", "ê", "ề", "ế", "ệ", "ể", "ễ", "e"};
        String[] i = {"ì", "í", "ị", "ỉ", "ĩ", "i"};
        String[] y = {"ỳ", "ý", "ỵ", "ỷ", "ỹ", "y"};
        String[] o = {"ò", "ó", "ọ", "ỏ", "õ", "ô", "ồ", "ố", "ộ", "ổ", "ỗ", "ơ", "ờ", "ớ", "ợ", "ở", "ỡ", "o"};
        String[] u = {"ù", "ú", "ụ", "ủ", "ũ", "ừ", "ứ", "ự", "ử", "ữ", "u", "ư"};
        // thay thế ký tự đặc biệt theo ý muốn
        name = name.replaceAll(" +", "-");
        name = name.replace("#", "sharp");
        name = name.replace("$", "dola");
        // khai báo mảng ký tự đặt biệt
        String[] specialchars = {")", "(", "*", "[", "]", "}", "{", ">", "<", "=", ":", ",", "'", "\"", "/", "\\", "&",
            "?", ";", ".", "@", "^"};
        // thay thế ký tự có dấu thành không dấu
        for (String specialchar : specialchars) name = name.replace(specialchar, "");
        for (String s : a) name = name.replace(s, "a");
        for (String s : d) name = name.replace(s, "d");
        for (String s : e) name = name.replace(s, "e");
        for (String s : i) name = name.replace(s, "i");
        for (String s : y) name = name.replace(s, "y");
        for (String s : o) name = name.replace(s, "o");
        for (String s : u) name = name.replace(s, "u");
        return name;
    }
}
