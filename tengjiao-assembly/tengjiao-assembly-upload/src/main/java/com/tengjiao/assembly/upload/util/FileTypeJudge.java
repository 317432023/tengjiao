package com.tengjiao.assembly.upload.util;

import java.io.*;

import static com.tengjiao.tool.indep.ByteUtil.bytesToHex;

public final class FileTypeJudge {
    public static enum FileType {
        /**
         * JEPG.
         */
        JPEG("FFD8FF"),

        /**
         * PNG.
         */
        PNG("89504E47"),

        /**
         * GIF.
         */
        GIF("47494638"),

        /**
         * TIFF.
         */
        TIFF("49492A00"),

        /**
         * Windows Bitmap.
         */
        BMP("424D"),

        /**
         * CAD.
         */
        DWG("41433130"),

        /**
         * Adobe Photoshop.
         */
        PSD("38425053"),

        /**
         * Rich Text Format.
         */
        RTF("7B5C727466"),

        /**
         * XML.
         */
        XML("3C3F786D6C"),

        /**
         * HTML.
         */
        HTML("68746D6C3E"),
        /**
         * CSS.
         */
        CSS("48544D4C207B0D0A0942"),
        /**
         * JS.
         */
        JS("696B2E71623D696B2E71"),
        /**
         * Email [thorough only].
         */
        EML("44656C69766572792D646174653A"),

        /**
         * Outlook Express.
         */
        DBX("CFAD12FEC5FD746F"),

        /**
         * Outlook (pst).
         */
        PST("2142444E"),

        /**
         * MS Word/Excel.
         */
        XLS_DOC("D0CF11E0"), XLSX_DOCX("504B030414000600080000002100"),
        /**
         * Visio
         */
        VSD("d0cf11e0a1b11ae10000"),
        /**
         * MS Access.
         */
        MDB("5374616E64617264204A"),
        /**
         * WPS文字wps、表格et、演示dps都是一样的
         */
        WPS("d0cf11e0a1b11ae10000"),
        /**
         * torrent
         */
        TORRENT("6431303A637265617465"),
        /**
         * WordPerfect.
         */
        WPD("FF575043"),

        /**
         * Postscript.
         */
        EPS("252150532D41646F6265"),

        /**
         * Adobe Acrobat.
         */
        PDF("255044462D312E"),

        /**
         * Quicken.
         */
        QDF("AC9EBD8F"),

        /**
         * Windows Password.
         */
        PWL("E3828596"),

        /**
         * ZIP Archive.
         */
        ZIP("504B0304"),

        /**
         * RAR Archive.
         */
        RAR("52617221"),
        /**
         * JSP Archive.
         */
        JSP("3C2540207061676520"),
        /**
         * JAVA Archive.
         */
        JAVA("7061636B61676520"),
        /**
         * CLASS Archive.
         */
        CLASS("CAFEBABE0000002E00"),
        /**
         * JAR Archive.
         */
        JAR("504B03040A000000"),
        /**
         * MF Archive.
         */
        MF("4D616E69666573742D56"),
        /**
         *EXE Archive.
         */
        EXE("4D5A9000030000000400"),
        /**
         *CHM Archive.
         */
        CHM("49545346030000006000"),
        /*
         * INI("235468697320636F6E66"), SQL("494E5345525420494E54"), BAT(
         * "406563686F206f66660D"), GZ("1F8B0800000000000000"), PROPERTIES(
         * "6C6F67346A2E726F6F74"), MXP(
         * "04000000010000001300"),
         */
        /**
         * Wave.
         */
        WAV("57415645"),

        /**
         * AVI.
         */
        AVI("41564920"),

        /**
         * Real Audio.
         */
        RAM("2E7261FD"),

        /**
         * Real Media.
         */
        RM("2E524D46"),

        /**
         * MPEG (mpg).
         */
        MPG("000001BA"),

        /**
         * Quicktime.
         */
        MOV("6D6F6F76"),

        /**
         * Windows Media.
         */
        ASF("3026B2758E66CF11"),

        /**
         * MIDI.
         */
        MID("4D546864"),
        /**
         * MP4.
         */
        MP4("00000020667479706d70"),
        /**
         * MP3.
         */
        MP3("49443303000000002176"),
        /**
         * FLV.
         */
        FLV("464C5601050000000900");

        private String value = "";

        /**
         * Constructor.
         *
         */
        private FileType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }
    /**
     * Constructor
     */
    private FileTypeJudge() {
    }

    /**
     * 去得文件头字节
     * @param is
     * @return
     * @throws IOException
     */
    private static byte[] getFileHeadBytes(InputStream is) throws IOException {
        byte[] b = new byte[28];

        try {
            is.read(b, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return b;
    }
    /**
     * 得到文件头
     *
     * @param is
     *            文件路径
     * @return 文件头
     * @throws IOException
     */
    private static String getFileContent(InputStream is) throws IOException {

        byte[] b = getFileHeadBytes(is);
        return bytesToHex(b);
    }

    /**
     * 得到文件头(操作共享变量情况下非线程安全，因为使用了System.arraycopy)
     *
     * @param bs 字节数组
     * @return 文件头
     * @throws IOException
     */
    private static String getFileContent(byte[] bs) throws IOException {
        if(bs.length < 28) {
            return "";
        }
        byte[] b = null;
        if(bs.length == 28) {
            b = bs;
        }else {
            b = new byte[28];
            System.arraycopy(bs, 0, b, 0, 28);
        }
        return bytesToHex(b);
    }

    private static FileType getType(String fileHead) {
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }
        return null;
    }
    /**
     * 判断文件类型
     *
     * @param bs 字节数组
     * @return 文件类型
     */
    public static FileType getType(byte[] bs) throws IOException {

        String fileHead = getFileContent(bs);
        return getType(fileHead);
    }

    /**
     * 判断文件类型
     *
     * @param is
     *            文件输入流
     * @return 文件类型
     */
    public static FileType getType(InputStream is) throws IOException {

        String fileHead = getFileContent(is);
        return getType(fileHead);
    }
    /**
     *
     * @param value 表示文件类型
     * @return 1 表示图片,2 表示文档,3 表示视频,4 表示种子,5 表示音乐,6 表示其它
     * @return
     */
    public static Integer isFileType(FileType value) {
        Integer type = 6;// 其他
        // 图片
        FileType[] pics = { FileType.JPEG, FileType.PNG, FileType.GIF, FileType.TIFF, FileType.BMP, FileType.DWG, FileType.PSD };

        FileType[] docs = { FileType.RTF, FileType.XML, FileType.HTML, FileType.CSS, FileType.JS, FileType.EML, FileType.DBX, FileType.PST, FileType.XLS_DOC, FileType.XLSX_DOCX, FileType.VSD,
                FileType.MDB, FileType.WPS, FileType.WPD, FileType.EPS, FileType.PDF, FileType.QDF, FileType.PWL, FileType.ZIP, FileType.RAR, FileType.JSP, FileType.JAVA, FileType.CLASS,
                FileType.JAR, FileType.MF, FileType.EXE, FileType.CHM };

        FileType[] videos = { FileType.AVI, FileType.RAM, FileType.RM, FileType.MPG, FileType.MOV, FileType.ASF, FileType.MP4, FileType.FLV, FileType.MID };

        FileType[] tottents = { FileType.TORRENT };

        FileType[] audios = { FileType.WAV, FileType.MP3 };

        FileType[] others = {};

        // 图片
        for (FileType fileType : pics) {
            if (fileType.equals(value)) {
                type = 1;
            }
        }
        // 文档
        for (FileType fileType : docs) {
            if (fileType.equals(value)) {
                type = 2;
            }
        }
        // 视频
        for (FileType fileType : videos) {
            if (fileType.equals(value)) {
                type = 3;
            }
        }
        // 种子
        for (FileType fileType : tottents) {
            if (fileType.equals(value)) {
                type = 4;
            }
        }
        // 音乐
        for (FileType fileType : audios) {
            if (fileType.equals(value)) {
                type = 5;
            }
        }
        return type;
    }

    private static String getTypeName(FileType fileType) {
        if(fileType == null) {
            return "unknown";
        }
        int iType = FileTypeJudge.isFileType(fileType);

        switch(iType) {
            case 1:
                return "image";
            case 2:
                return "doc";
            case 3:
                return "video";
            case 4:
                return "tottents";
            case 5:
                return "music";
        }
        return "unknown";
    }
    public static String getTypeName(InputStream is) {
        FileType fileType = null;
        try {
            fileType = FileTypeJudge.getType(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getTypeName(fileType);
    }
    public static String getTypeName(byte[] bs) {
        return getTypeName(new ByteArrayInputStream(bs));
    }
    public static String getTypeName(File file) {
        try {
            return getTypeName(new FileInputStream(
                    file));
        } catch (FileNotFoundException e) {
        }
        return "unknown";
    }

    public static void main(String args[]) throws Exception {
        System.out.println(
                FileTypeJudge.isFileType(
                        FileTypeJudge.getType(
                                new FileInputStream(
                                        new File("C:\\tmp\\upload\\file\\2020\\03\\11\\38e1a7b092c541cca63b753c966e1468.jpg")))));
        for (FileType type : FileType.values()) {
            System.out.print(type + "\t");
        }
    }
}
