import org.apache.commons.codec.Charsets;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CashReceiptWorker {

    private static final String URL_SECURED_BY_BASIC_AUTHENTICATION = "https://proverkacheka.nalog.ru:9999/v1/mobile/users/login";
    //    private static final String URL_REQUEST = "https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/fss" +
//            "/9287440300143277/operations/1/tickets/51419?fiscalSign=2049110697&date=2019-09-06T20:57:00&sum=157600";
    private static final String URL_REQUEST = "https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/fss" +
            "/9281000100009218/operations/1/tickets/49916?fiscalSign=3487040725&date=2019-09-06T20:56:00&sum=7250";

    //    private static final String URL_REQUEST_FULL = "https://proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss" +
//            "/9287440300143277/tickets/51419?fiscalSign=2049110697&sendToEmail=no";
    private static final String URL_REQUEST_FULL = "https://proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss" +
            "/9281000100009218/tickets/49916?fiscalSign=3487040725&sendToEmail=no";


    public static void checkCashReceipt() throws IOException {

        HttpHost targetHost = new HttpHost(URL_SECURED_BY_BASIC_AUTHENTICATION);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("+79920279024", "375136"));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(
                new HttpGet(URL_SECURED_BY_BASIC_AUTHENTICATION), context);

        int statusCode1 = response.getStatusLine().getStatusCode();

        //-----------------------------------------------------------------
        QRWorker worker = new QRWorker();
        String qrString = worker.sendQRFile("src/main/resources/qr_1.jpg");
        QRData qrData = worker.parseResponse(qrString);
        //-----------------------------------------------------------------

        String reqest = "https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/" +
                "fss/" +
                qrData.getFn() +
                "/operations/" +
                qrData.getN() +
                "/tickets/" +
                qrData.getI() +
                "?fiscalSign=" +
                qrData.getFp() +
                "&date=" +
                qrData.getTime() +
                "&sum=" +
                qrData.getSum();

        HttpResponse response2 = client.execute(
                new HttpGet(reqest), context);

        int statusCode2 = response2.getStatusLine().getStatusCode();


        String reqestFull = "https://proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss/" +
                qrData.getFn() +
                "/tickets/" +
                qrData.getI() +
                "?fiscalSign=" +
                qrData.getFp() +
                "&sendToEmail=no";

        HttpGet httpGet = new HttpGet(reqestFull);
        httpGet.setHeader("device-id", "");
        httpGet.setHeader("device-os", "");

        HttpResponse response3 = client.execute(httpGet, context);

        int statusCode3 = response3.getStatusLine().getStatusCode();

        if (statusCode3 == 200) {
            HttpEntity entity = response3.getEntity();
            Header encodingHeader = entity.getContentEncoding();

            // you need to know the encoding to parse correctly
            Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8 :
                    Charsets.toCharset(encodingHeader.getValue());

            // use org.apache.http.util.EntityUtils to read json as string
            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            System.out.println("УРАААААААААААААААААААААААААААА!!!!!!!!!!!!!!!");
            System.out.println(json);
            int q1 = 0;
        }

        int q2 = 0;

    }


}
