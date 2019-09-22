public class QRData {

    private String dateTime; // дата и время осуществления расчета в формате ГГГГММДДТЧЧММ
    private String sum; // сумма расчета в рублях и копейках, разделенных точкой
    private String fn; // заводской номер фискального накопителя
    private String n; // признак расчетах
    private String fp; // фискальный признак документа, нулями не дополняется
    private String i; // порядковый номер фискального документа, нулями не дополняется

    public QRData() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        if (dateTime.length() > 14) {
            StringBuilder builder = new StringBuilder(dateTime.substring(2));
            builder.insert(4, "-");
            builder.insert(7, "-");
            builder.insert(13, ":");
            if (dateTime.length() == 15) {
                builder.insert(16, ":00");
            } else if (dateTime.length() == 17) {
                builder.insert(16, ":");
            }
            this.dateTime = builder.toString();
        } else {
            this.dateTime = "";
        }
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = getClippedValue(sum.replace(".", ""), 2);
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = getClippedValue(fn, 3);
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = getClippedValue(n, 2);
    }

    public String getFp() {
        return fp;
    }

    public void setFp(String fp) {
        this.fp = getClippedValue(fp, 3);
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = getClippedValue(i, 2);
    }

    private String getClippedValue(String value, int length) {
        if (value.length() > length) {
            return value.substring(length);
        } else {
            return  "";
        }
    }
}
