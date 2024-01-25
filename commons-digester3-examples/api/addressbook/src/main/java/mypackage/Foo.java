package mypackage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Foo {
    private List<Bar> barsList = new ArrayList<>();
    private String name;

    public void addBar( Bar bar ){
        barsList.add(bar);
    }

    public Bar findBar( int id ){
        for(Bar bar : barsList){
            if (bar.getId() == id){
                return bar;
            }
        }
        return null;
    }

    public Iterator getBars(){
        Iterator<Bar> iterator = barsList.iterator();
        return iterator;
    }

    public String getName(){
        return name;
    }

    public void setName( String name ){
        this.name = name;
    }

    @Override
    public String toString() {
        return "Foo{" +
                "barsList=" + barsList +
                ", name='" + name + '\'' +
                '}';
    }
}
