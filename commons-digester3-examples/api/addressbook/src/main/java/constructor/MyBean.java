package constructor;

public class MyBean {

    private final Double rate;
    private final Boolean s;

    public MyBean(Double rate, Boolean s )
    {
        this.rate = rate;
        this.s = s;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "rate=" + rate +
                ", s=" + s +
                '}';
    }
}
