public class QRData {

    private String time;
    private String sum;
    private String fn;
    private String n; // i
    private String fp;
    private String i;

    public QRData() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        StringBuilder builder = new StringBuilder(time.substring(2));
        builder.insert(4, "-");
        builder.insert(7, "-");
        builder.insert(13, ":");
        builder.insert(16, ":");

        this.time = builder.toString();
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum.replace(".", "").substring(2);
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn.substring(3);
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n.substring(2);
    }

    public String getFp() {
        return fp;
    }

    public void setFp(String fp) {
        this.fp = fp.substring(3);
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i.substring(2);
    }
}
