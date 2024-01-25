package zip;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, SAXException {
        Digester digester = new Digester();
        digester.setValidating( false );

        digester.addObjectCreate( "zipfiles", ZipFiles.class);
        digester.addSetProperties( "zipfiles" );

        digester.addObjectCreate( "zipfiles/zipfile", ZipFile.class );
        digester.addSetProperties( "zipfiles/zipfile" );
        digester.addSetNext( "zipfiles/zipfile", "addZipFile");
        // ??srcdir??????????setSrcdir??
        digester.addCallMethod("zipfiles/zipfile/srcdir", "setSrcdir", 0);
        // ??destdir??????????addDestdir??
        digester.addCallMethod("zipfiles/zipfile/destdirs/destdir", "addDestdir", 0);

        ZipFiles zipFiles = digester.parse(new File("/Users/ruchen/IdeaProjects/commons-digester/commons-digester3-examples/api/addressbook/src/main/java/zip/zip.xml"));
        System.out.println(zipFiles);
    }
}
