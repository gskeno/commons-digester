package constructor;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreateRule;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {
        ObjectCreateRule createRule = new ObjectCreateRule( MyBean.class );
        createRule.setConstructorArgumentTypes( Double.class,  Boolean.class);

        Digester digester = new Digester();
        digester.addRule( "root/bean", createRule );
        //digester.addCallParam( "root/bean/rate", 0 );
        //digester.addCallParam( "root/bean", 1, "super" );

        MyBean myBean = (MyBean)digester.parse(new File("/Users/ruchen/IdeaProjects/commons-digester/commons-digester3-examples/api/addressbook/src/main/java/constructor/mybean.xml"));
        System.out.println(myBean.getClass().getSimpleName());
        System.out.println(myBean);
    }
}
