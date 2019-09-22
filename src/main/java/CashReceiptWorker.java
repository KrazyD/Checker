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

    private HttpClientContext context = null;
    private HttpClient client = null;

    private static final String LOGIN_URL = "https://proverkacheka.nalog.ru:9999/v1/mobile/users/login";
    private static final String CASH_RECEIPT_CHECK_URL = "https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/fss/%s/operations/%s/tickets/%s?fiscalSign=%s&date=%s&sum=%s";
    private static final String CASH_RECEIPT_DATA_URL = "https://proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss/%s/tickets/%s?fiscalSign=%s&sendToEmail=no";

    public CashReceiptWorker(String phoneNumber, String password) {
        createContext(phoneNumber, password);
    }


    public int loginToFNS() throws IOException {

        HttpResponse response = client.execute(new HttpGet(LOGIN_URL), context);

        return response.getStatusLine().getStatusCode();
    }

    public int cashReceiptCheck(QRData qrData) throws IOException {

        String request = String.format(CASH_RECEIPT_CHECK_URL, qrData.getFn(), qrData.getN(),
                qrData.getI(), qrData.getFp(), qrData.getDateTime(), qrData.getSum());
        HttpResponse response = client.execute(new HttpGet(request), context);

        return response.getStatusLine().getStatusCode();
    }

    public String getCashRecieptData(QRData qrData) throws IOException, InterruptedException {

        String request = String.format(CASH_RECEIPT_DATA_URL, qrData.getFn(), qrData.getI(), qrData.getFp());

        HttpGet httpGet = new HttpGet(request);
        httpGet.setHeader("device-id", "");
        httpGet.setHeader("device-os", "");

        HttpResponse response = client.execute(httpGet, context);

        int statusCode = response.getStatusLine().getStatusCode();

        int counter = 5;

        if (statusCode == 202) {
            while (counter != 0 && statusCode != 200) {
                Thread.sleep(1000);
                response = client.execute(httpGet, context);
                statusCode = response.getStatusLine().getStatusCode();
                counter--;
            }
        }

        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            Header encodingHeader = entity.getContentEncoding();

            Charset encoding = encodingHeader == null ? StandardCharsets.UTF_8 :
                    Charsets.toCharset(encodingHeader.getValue());

            return EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } else {
            return null;
        }
    }

    private void createContext(String phoneNumber, String password) {
        HttpHost targetHost = new HttpHost(LOGIN_URL);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(phoneNumber, password));

        AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        client = HttpClientBuilder.create().build();
    }

}
