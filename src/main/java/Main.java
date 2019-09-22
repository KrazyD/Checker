
public class Main {

    public static void main(String[] args) throws Exception {

        CashReceiptWorker receiptWorker = new CashReceiptWorker("+79920279024", "375136");
        int responseCode = receiptWorker.loginToFNS();

        if (responseCode != 200) {
            throw new Exception("Error login to FNS!");
        }

        QRWorker worker = new QRWorker();
        String qrString = worker.parseQRCode("src/main/resources/qr_6.jpg");
        QRResponce responce = worker.parseQRResponse(qrString);

        for(ResponseSymbol symbol: responce.getSymbol()) {
            if (symbol.getError() != null) {
                System.err.println("ERROR WHILE GET DATA FROM QR CODE!");
                System.err.println("REASON: " + symbol.getError());
            } else {
                QRData qrData = worker.parseResponse(symbol.getData());
                responseCode = receiptWorker.cashReceiptCheck(qrData);

//                if (responseCode != 204 && responseCode != 200) {
//                    throw new Exception("Error while cash receipt check!");
//                }

                String cashReceiptData = receiptWorker.getCashRecieptData(qrData);

                System.out.println(cashReceiptData);
            }
        }

        int q = 0;
    }
}
