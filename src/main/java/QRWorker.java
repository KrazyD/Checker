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


    public String sendQRFile(String filePath) throws IOException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost("http://api.qrserver.com/v1/read-qr-code/");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // This attaches the file to the POST:
        File f = new File(filePath);
        builder.addBinaryBody(
                "file",
                new FileInputStream(f),
                ContentType.APPLICATION_OCTET_STREAM,
                f.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);
        HttpEntity responseEntity = response.getEntity();

        String responseXml = EntityUtils.toString(responseEntity);

        int startIndex = responseXml.indexOf("t=");
        int endIndex = responseXml.lastIndexOf("\",\"error");

        return responseXml.substring(startIndex, endIndex);
    }

    public QRData parseResponse(String qrString) {

        QRData qrData = new QRData();

        String[] qrArray = qrString.split("&");

        if (qrArray.length == 6) {
            qrData.setTime(qrArray[0]);
            qrData.setSum(qrArray[1]);
            qrData.setFn(qrArray[2]);
            qrData.setI(qrArray[3]);
            qrData.setFp(qrArray[4]);
            qrData.setN(qrArray[5]);
        }

        return qrData;
    }

}
