package com.tengjiao.tool.third.xml;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.xml.sax.InputSource;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Serialize java object & De-serialize xml Using XStream
 * @author rise
 *
 */
public final class XmlTool {
    public static final String top = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    public static final String topOfGBK = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n";

    /** XStream对象是线程安全的，一个XStream实例即可用于多线程 */
    private static XStream xStream;

    static {
        xStream = new XStream(new XppDriver(new NoNameCoder()));
        // 去掉class属性
        xStream.aliasSystemAttribute(null, "class");
        // 自动探测注解
        xStream.autodetectAnnotations(true);
        // 忽略未知元素
        xStream.ignoreUnknownElements();

        // to be removed after 1.5
        // 避免出现以下警告:Security framework of XStream not initialized, XStream is probably vulnerable
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypesByWildcard(new String[] {
                // 设置允许的反序列化的包路径，避免出现 com.thoughtworks.xstream.security.ForbiddenClassException
                //"me.jrise.**", // 允许me.jrise包
                // 允许所有包
                "**",
        });
    }

    /*
    private static SimpleXmlTool INSTANCE = new SimpleXmlTool();

    public static final SimpleXmlTool geInstance() {
        return INSTANCE;
    }*/
    /**
     * 通过静态内部类实现单例模式
     */
    private static class LazyHolder {
        private static final XmlTool INSTANCE = new XmlTool();
    }

    public static final XmlTool getInstance() {
        return LazyHolder.INSTANCE;
    }

    /** 禁止外部实例化此类 */
    private XmlTool() {}

    /**
     * 将Java对象序列化成XML格式
     * @param object
     * @return
     */
    public String serialize(Object object, final String alias, String... charset){
        xStream.alias(alias, object.getClass());
        String xml = xStream.toXML(object);
        return ((charset!=null && charset.length>0 && "GBK".equals(charset[0]))?topOfGBK:top) + xml;
    }

    /**
     * 将Java对象序列化成XML格式字符串并保存至指定文件中
     *
     * @param object
     * @param alias
     * @param file
     * @prarm charset
     * @param file
     */
    public void serialize(Object object, final String alias, File file, String... charset) throws TransformerException {
        String xml = serialize(object, alias, charset);
        saveToFile(xml, file);
    }

    /**
     * 使用同步类 Transformer,将 xml 字符串保存到文件中
     *
     * @param xml
     * @param file
     * @throws Exception
     */
    private void saveToFile(String xml, File file) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        // 将字符串xml转为Source对象
        InputSource is = new InputSource(new ByteArrayInputStream(xml.getBytes()));
        Source xmlSource = new SAXSource(is);
        // 同步操作
        transformer.transform(xmlSource, new StreamResult(file));
    }

    /**
     * 将XML反序列化还原为Java对象
     * @param xml
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(String xml, final String alias, Class<T> clazz) {
        xStream.alias(alias, clazz);
        T object = (T)xStream.fromXML(xml);
        return object;
    }



    /**
     * 从XML Reader反序列化还原为Java对象
     *
     * @param reader
     * @return
     */
    public <T> T deserialize(Reader reader, final String alias, Class<T> clazz) {
        xStream.alias(alias, clazz);
        T object = (T) xStream.fromXML(reader);
        return object;
    }

    /**
     * 从InputStream反序列化还原为Java对象
     *
     * @param inputStream
     * @return
     */
    public <T> T deserialize(InputStream inputStream, final String alias, Class<T> clazz) {
        xStream.alias(alias, clazz);
        T object = (T) xStream.fromXML(inputStream);
        return object;
    }

    /**
     * 从URL反序列化还原为Java对象
     *
     * @param url
     * @return
     */
    public <T> T deserialize(URL url, final String alias, Class<T> clazz) {
        xStream.alias(alias, clazz);
        T object = (T) xStream.fromXML(url);
        return object;
    }


    /**
     * 从File反序列化还原为Java对象
     *
     * @param file
     * @return
     */
    public <T> T deserialize(File file, final String alias, Class<T> clazz) {
        xStream.alias(alias, clazz);
        T object = (T) xStream.fromXML(file);
        return object;
    }
}
