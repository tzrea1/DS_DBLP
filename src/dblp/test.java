package dblp;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @Author: cxx
 * SAX解析DOM
 * 一行一行  Handler
 * startElement
 * endElement
 * @Date: 2018/5/29 20:03
 */
public class test {

    public static void main(String[] args) throws Exception {
        //1.或去SAXParserFactory实例
        SAXParserFactory factory = SAXParserFactory.newInstance();
        //2.获取SAXparser实例
        SAXParser saxParser = factory.newSAXParser();
        //创建Handel对象
        SAXDemoHandel handel = new SAXDemoHandel();
        saxParser.parse("src/test.xml",handel);
    }
}

class SAXDemoHandel extends DefaultHandler {
    //遍历xml文件开始标签
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        System.out.println("sax解析开始");
    }

    //遍历xml文件结束标签
    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        System.out.println("sax解析结束");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("article")||qName.equals("book")){
            System.out.println("============开始遍历"+qName+"=============");
            //System.out.println(attributes.getValue("rollno"));
        }
        else if (!qName.equals("dblp")){
            System.out.print("节点名称:"+qName+"----");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (qName.equals("article")||qName.equals("book")){
            System.out.println("============结束遍历"+qName+"=============");
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        String value = new String(ch,start,length).trim();
        if (!value.equals("")) {
            System.out.println(value);
        }
    }
}

