/*
 * Certain versions of software and/or documents ("Material") accessible here may contain branding from
 * Hewlett-Packard Company (now HP Inc.) and Hewlett Packard Enterprise Company.  As of September 1, 2017,
 * the Material is now offered by Micro Focus, a separately owned and operated company.  Any reference to the HP
 * and Hewlett Packard Enterprise/HPE marks is historical in nature, and the HP and Hewlett Packard Enterprise/HPE
 * marks are the property of their respective owners.
 * __________________________________________________________________
 * MIT License
 *
 * (c) Copyright 2012-2023 Micro Focus or one of its affiliates.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * ___________________________________________________________________
 */

package com.microfocus.application.automation.tools.mc;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

public class HttpUtils {

    public static final String POST = "POST";
    public static final String GET = "GET";

    private HttpUtils() {

    }

    public static HttpResponse doPost(ProxyInfo proxyInfo, String url, Map<String, String> headers, byte[] data) {

        HttpResponse response = null;
        try {
            response = doHttp(proxyInfo, POST, url, null, headers, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse doGet(ProxyInfo proxyInfo, String url, Map<String, String> headers, String queryString) {

        HttpResponse response = null;
        try {
            response = doHttp(proxyInfo, GET, url, queryString, headers, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


    private static HttpResponse doHttp(ProxyInfo proxyInfo, String requestMethod, String connectionUrl, String queryString, Map<String, String> headers, byte[] data) throws IOException {
        HttpResponse response = new HttpResponse();

        if ((queryString != null) && !queryString.isEmpty()) {
            connectionUrl += "?" + queryString;
        }

        URL url = new URL(connectionUrl);


        HttpURLConnection connection = (HttpURLConnection) openConnection(proxyInfo, url);

        connection.setRequestMethod(requestMethod);

        setConnectionHeaders(connection, headers);

        if (data != null && data.length > 0) {
            connection.setDoOutput(true);
            try {
                OutputStream out = connection.getOutputStream();
                out.write(data);
                out.flush();
                out.close();
            } catch (Throwable cause) {
                cause.printStackTrace();
            }
        }

        connection.connect();

        int responseCode = connection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = connection.getInputStream();
            JSONObject jsonObject = convertStreamToJSONObject(inputStream);
            response.setHeaders(connection.getHeaderFields());
            if (null == jsonObject) {
                System.out.println(requestMethod + " " + connectionUrl + " return is null.");
            } else {
                response.setJsonObject(jsonObject);
            }
        } else {
            System.out.println(requestMethod + " " + connectionUrl + " failed with response code:" + responseCode);
        }
        connection.disconnect();

        return response;
    }

    private static URLConnection openConnection(final ProxyInfo proxyInfo, URL _url) throws IOException {

        Proxy proxy = null;

        if (proxyInfo != null && !proxyInfo.isEmpty()) {
            try {
                int port = Integer.parseInt(proxyInfo.port.trim());
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyInfo.host, port));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (proxy != null && !proxyInfo.isEmpty()) {
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(proxyInfo.userName, proxyInfo.password.toCharArray());    //To change body of overridden methods use File | Settings | File Templates.
                }
            };

            Authenticator.setDefault(authenticator);
        }

        if (proxy == null) {
            return _url.openConnection();
        }


        return _url.openConnection(proxy);
    }


    private static void setConnectionHeaders(HttpURLConnection connection, Map<String, String> headers) {

        if (connection != null && headers != null && headers.size() != 0) {
            Iterator<Map.Entry<String, String>> headersIterator = headers.entrySet().iterator();
            while (headersIterator.hasNext()) {
                Map.Entry<String, String> header = headersIterator.next();
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

    }

    private static JSONObject convertStreamToJSONObject(InputStream inputStream) {
        JSONObject obj = null;

        if (inputStream != null) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer res = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    res.append(line);
                }
                obj = (JSONObject) JSONValue.parseStrict(res.toString());
            } catch (ClassCastException e) {
                System.out.println("WARN::INVALIDE JSON Object" + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }


    public static ProxyInfo setProxyCfg(String host, String port, String userName, String password) {

        return new ProxyInfo(host, port, userName, password);
    }

    public static ProxyInfo setProxyCfg(String host, String port) {

        ProxyInfo proxyInfo = new ProxyInfo();

        proxyInfo.host = host;
        proxyInfo.port = port;

        return proxyInfo;
    }

    public static ProxyInfo setProxyCfg(String address, String userName, String password) {
        ProxyInfo proxyInfo = new ProxyInfo();

        if (address != null) {
            if (address.endsWith("/")) {
                int end = address.lastIndexOf("/");
                address = address.substring(0, end);
            }

            int index = address.lastIndexOf(':');
            if (index > 0) {
                proxyInfo.host = address.substring(0, index);
                proxyInfo.port = address.substring(index + 1, address.length());
            } else {
                proxyInfo.host = address;
                proxyInfo.port = "80";
            }
        }
        proxyInfo.userName = userName;
        proxyInfo.password = password;

        return proxyInfo;
    }

    static class ProxyInfo {
        String host;
        String port;
        String userName;
        String password;

        public ProxyInfo() {

        }

        public ProxyInfo(String host, String port, String userName, String password) {
            this.host = host;
            this.port = port;
            this.userName = userName;
            this.password = password;
        }

        public boolean isEmpty() {
            return StringUtils.isEmpty(host) || StringUtils.isEmpty(port) || StringUtils.isEmpty(userName) || StringUtils.isEmpty(password);
        }

    }
}