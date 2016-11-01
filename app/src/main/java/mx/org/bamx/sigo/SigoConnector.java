package mx.org.bamx.sigo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * This c is for connecting externally with the SIGO server. Communication is handled in an
 * asynchronous task to avoid blocking the ui thread while synchronization runs.
 */
public class SigoConnector extends AsyncTask<Context, Integer, Boolean> {

    Activity context;
    String TEST_URL = "http://google.com";
    String SIGO_COMMUNITIES = "http://bamx.sytes.net/webforms/padron/frmRegistrarEstudioSN.aspx/fnComunidades";

    public SigoConnector(Activity context) {

        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Context... params) {
        if (testConnection()) {
            HashMap dataParams = new HashMap<String, String>();

            // An example of a foodbank string
            dataParams.put("strBanco", "CIt6OBMWhXc=");

            String response = sendPostRequest(SIGO_COMMUNITIES, dataParams);

            if (response != null) {
                return true;
            }
        }
        return false;

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        Sigo sigo = (Sigo) context;
        sigo.setIsSyncing(false);

        try {
            if (result) {
                Toast.makeText(context, R.string.syncSuccessful,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, R.string.syncNotSuccessful,
                        Toast.LENGTH_LONG).show();
            }

        } catch (NullPointerException e) {
            // If a nullpoiner is encountered here, it means that the method has been invoked
            // by the scheduled alarm manager - we can ignore this error, no toast is needed
            // for background synchronization tasks.
        }

    }

    public boolean testConnection() {

        try {
            URL url = null;
            url = new URL(TEST_URL);

            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setConnectTimeout(7000);

            client.setReadTimeout(7000);

            client.connect();
            int response = client.getResponseCode();

            return HttpURLConnection.HTTP_OK == response;

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        URL url;

        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                while ((response = br.readLine()) != null){
                    sb.append(response);
                }
                return sb.toString();
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}
