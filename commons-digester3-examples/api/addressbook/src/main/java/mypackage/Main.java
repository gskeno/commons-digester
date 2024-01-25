package mypackage;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating( false );

        digester.addObjectCreate( "foo", "mypackage.Foo" );
        digester.addSetProperties( "foo" );

        digester.addObjectCreate( "foo/bar", "mypackage.Bar" );
        digester.addSetProperties( "foo/bar" );
        digester.addSetNext( "foo/bar", "addBar", "mypackage.Bar" );


        Foo foo = digester.parse(new File("/Users/ruchen/IdeaProjects/commons-digester/commons-digester3-examples/api/addressbook/src/main/java/mypackage/foobar.xml"));
        System.out.println(foo);
    }
}
