package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class HttpService {
    private String url;
    private String requestMethod;
    private String formData;
    private HttpURLConnection connection;
    private String headerName;
    private String headerValue;

    public void setConnection(String url, String requestMethod, String formData) {
        this.url = url;
        this.requestMethod = requestMethod;
        this.formData = formData;
    }

    public void setConnection(String url, String requestMethod) {
        this.url = url;
        this.requestMethod = requestMethod;
    }

    public void addHeader(String headerName, String headerValue){
        this.headerName=headerName;
        this.headerValue=headerValue;
    }

    public String getHeader(String headerName) throws IOException {
            if (connection == null) {
                createConnection();
            }
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> values = headerFields.get(headerName);
            if (values != null && !values.isEmpty()) {
                return values.get(0);
            }
        return null;
    }

    public String getBody() throws IOException {
        try {
            if (connection == null) {
                createConnection();
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                BufferedReader br;
                if ("gzip".equals(connection.getContentEncoding())) {
                    br = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream())));
                } else {
                    br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                }
                String output;
                while ((output = br.readLine()) != null) {
                    response.append(output);
                }
                br.close();
                connection.disconnect();
                return response.toString();
             } else {
                 throw new IOException("HTTP zahteva je proizvedla kodo: " + responseCode);
             }
        } catch (IOException e) {
            System.out.println(e);
        }
        return null;
    }

    private void createConnection() throws IOException {
        try {
            URI uri = new URI(url);
            connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod(requestMethod);
            setHeaders();
            
            connection.setDoOutput(true);

            if (formData != null && !formData.isEmpty()) {
                connection.getOutputStream().write(formData.getBytes("UTF-8"));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IOException("Napačna URL struktura.");
        }
    }

    public void closeConnection() {
        if (connection != null) {
            connection.disconnect();
            connection = null;
        }
    }

    public void resetConnection() {
        closeConnection();
        headerName=headerValue=null;
    }

    private void setHeaders(){
        connection.setRequestProperty("host","https://www.easistent.com");
        connection.setRequestProperty("x-child-id","370747");
        connection.setRequestProperty("x-client-platform","web");
        connection.setRequestProperty("x-client-version","13");
        connection.setRequestProperty("useragent","ea-cli/1.3");
        connection.setRequestProperty("cookie","easistent_cookie=zapri");
        if (headerName!=null & headerValue!=null){
            connection.setRequestProperty(headerName,headerValue);
        }
    }

    public class HttpStatusException extends RuntimeException {
        public HttpStatusException(String message) {
            super(message);
        }
    }
}

