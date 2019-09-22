import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;

public class QRWorker {

    private final String API_URL = "http://api.qrserver.com/v1/read-qr-code/";

    public String parseQRCode(String filePath) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost postToUpload = new HttpPost(API_URL);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        File fileQR = new File(filePath);
        builder.addBinaryBody(
                "file",
                new FileInputStream(fileQR),
                ContentType.APPLICATION_OCTET_STREAM,
                fileQR.getName()
        );

        HttpEntity multipart = builder.build();
        postToUpload.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(postToUpload);
        HttpEntity responseEntity = response.getEntity();

        return EntityUtils.toString(responseEntity);
    }

    public QRResponce parseQRResponse(String response) {
        ObjectMapper mapper = new ObjectMapper();
        QRResponce qrResponse = null;

        try {
            qrResponse = mapper.readValue(response.substring(1, response.length() - 1), QRResponce.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return qrResponse;
    }

    public QRData parseResponse(String qrString) {

        QRData qrData = new QRData();

        String[] qrArray = qrString.split("&");

        if (qrArray.length == 6) {
            qrData.setDateTime(qrArray[0]);
            qrData.setSum(qrArray[1]);
            qrData.setFn(qrArray[2]);
            qrData.setI(qrArray[3]);
            qrData.setFp(qrArray[4]);
            qrData.setN(qrArray[5]);
        }

        return qrData;
    }

}
