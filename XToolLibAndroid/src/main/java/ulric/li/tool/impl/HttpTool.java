package ulric.li.tool.impl;

import android.os.Message;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import ulric.li.XLibFactory;
import ulric.li.tool.intf.IHttpObjectResultListener;
import ulric.li.tool.intf.IHttpTool;
import ulric.li.tool.intf.IHttpToolListener;
import ulric.li.tool.intf.IHttpToolResult;
import ulric.li.tool.intf.IHttpToolUploadFile;
import ulric.li.utils.UtilsEncrypt;
import ulric.li.utils.UtilsEnv;
import ulric.li.utils.UtilsFile;
import ulric.li.utils.UtilsMediaStore;
import ulric.li.xlib.impl.XObserverAutoRemove;
import ulric.li.xlib.intf.IXThreadPool;
import ulric.li.xlib.intf.IXThreadPoolListener;

public class HttpTool extends XObserverAutoRemove<IHttpToolListener> implements IHttpTool {
    private IXThreadPool mIXThreadPool = null;

    private static final int VALUE_INT_CONNECT_TIMEOUT = 12 * 1000;
    private static final int VALUE_INT_READ_TIMEOUT = 12 * 1000;
    private static final int VALUE_INT_UPLOAD_CONNECT_TIMEOUT = 12 * 1000;
    private static final int VALUE_INT_UPLOAD_READ_TIMEOUT = 12 * 1000;

    public HttpTool() {
        _init();
    }

    private void _init() {
        mIXThreadPool = (IXThreadPool) XLibFactory.getInstance().createInstance(IXThreadPool.class);
    }

    @Override
    public IHttpToolResult requestToBufferByGetSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty) {
        return requestToBufferByGetSync(strURL, mapParam, mapRequestProperty, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public IHttpToolResult requestToBufferByGetSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return null;

        HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_GET_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        requestByGet(httpToolTask, httpToolResult);
        return httpToolResult;
    }

    @Override
    public void requestByGetAsync(String strURL, Map<String, String> mapParam, final IHttpObjectResultListener listener) {
        requestToBufferByGetAsync(strURL, mapParam, null, null, new IHttpToolListener() {
            @Override
            public void onRequestToBufferByGetAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult) {
                if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                    if (listener != null) {
                        listener.onRequestFailed(iHttpToolResult);
                    }
                } else {
                    if (listener != null) {
                        listener.onRequestSuccess(iHttpToolResult);
                    }
                }
            }
        });
    }

    @Override
    public void requestToBufferByGetAsync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener) {
        requestToBufferByGetAsync(strURL, mapParam, mapRequestProperty, objectTag, iHttpToolListener, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public void requestToBufferByGetAsync(final String strURL, final Map<String, String> mapParam, final Map<String, String> mapRequestProperty, final Object objectTag, final IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return;

        final HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_GET_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        final HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                requestByGet(httpToolTask, httpToolResult);
            }

            @Override
            public void onTaskComplete() {
                if (null != iHttpToolListener) {
                    iHttpToolListener.onRequestToBufferByGetAsyncComplete(strURL, mapParam, objectTag, httpToolResult);
                } else {
                    for (IHttpToolListener listener : getListenerList())
                        listener.onRequestToBufferByGetAsyncComplete(strURL, mapParam, objectTag, httpToolResult);
                }
            }

            @Override
            public void onMessage(Message msg) {
            }
        });
    }

    @Override
    public IHttpToolResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty) {
        return requestToBufferByPostSync(strURL, mapParam, mapRequestProperty, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public IHttpToolResult requestToBufferByPostSync(String strURL, Map<String, String> mapParam, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return null;

        HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_POST_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        requestByPost(httpToolTask, httpToolResult);
        return httpToolResult;
    }

    @Override
    public void requestByPostAsync(String strURL, Map<String, String> mapParam,final IHttpObjectResultListener listener) {
        requestToBufferByPostAsync(strURL, mapParam, null, null, new IHttpToolListener() {
            @Override
            public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult) {
                if (null == iHttpToolResult || !iHttpToolResult.isSuccess() || null == iHttpToolResult.getBuffer()) {
                    if (listener != null) {
                        listener.onRequestFailed(iHttpToolResult);
                    }
                } else {
                    if (listener != null) {
                        listener.onRequestSuccess(iHttpToolResult);
                    }
                }
            }
        });
    }

    @Override
    public void requestToBufferByPostAsync(final String strURL, final Map<String, String> mapParam, final Map<String, String> mapRequestProperty, final Object objectTag, final IHttpToolListener iHttpToolListener) {
        requestToBufferByPostAsync(strURL, mapParam, mapRequestProperty, objectTag, iHttpToolListener, VALUE_INT_CONNECT_TIMEOUT, VALUE_INT_READ_TIMEOUT, true);
    }

    @Override
    public void requestToBufferByPostAsync(final String strURL, final Map<String, String> mapParam, final Map<String, String> mapRequestProperty, final Object objectTag, final IHttpToolListener iHttpToolListener, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return;

        final HttpToolTask httpToolTask = new HttpToolTask();
        httpToolTask.mRequestType = HttpToolTask.VALUE_INT_REQUEST_POST_TYPE;
        httpToolTask.mConnectTimeout = nConnectTimeout;
        httpToolTask.mReadTimeout = nReadTimeout;
        httpToolTask.mURL = strURL;
        httpToolTask.mMapParam = mapParam;
        httpToolTask.mMapRequestProperty = mapRequestProperty;
        httpToolTask.mIsNeedEncrypt = bIsNeedEncrypt;

        final HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                requestByPost(httpToolTask, httpToolResult);
            }

            @Override
            public void onTaskComplete() {
                if (null != iHttpToolListener) {
                    iHttpToolListener.onRequestToBufferByPostAsyncComplete(strURL, mapParam, objectTag, httpToolResult);
                } else {
                    for (IHttpToolListener listener : getListenerList())
                        listener.onRequestToBufferByPostAsyncComplete(strURL, mapParam, objectTag, httpToolResult);
                }
            }

            @Override
            public void onMessage(Message msg) {

            }
        });
    }

    @Override
    public IHttpToolResult uploadFileByPostSync(String strURL, Map<String, String> mapParam, List<IHttpToolUploadFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty) {
        return uploadFileByPostSync(strURL, mapParam, listHttpToolUploadFile, mapRequestProperty, VALUE_INT_UPLOAD_CONNECT_TIMEOUT, VALUE_INT_UPLOAD_READ_TIMEOUT, true);
    }

    @Override
    public IHttpToolResult uploadFileByPostSync(String strURL, Map<String, String> mapParam, List<IHttpToolUploadFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return null;

        HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        uploadFile(strURL, mapParam, listHttpToolUploadFile, mapRequestProperty, httpToolResult, nConnectTimeout, nReadTimeout, bIsNeedEncrypt);
        return httpToolResult;
    }

    @Override
    public void uploadFileByPostAsync(String strURL, Map<String, String> mapParam, List<IHttpToolUploadFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, Object objectTag, IHttpToolListener iHttpToolListener) {
        uploadFileByPostAsync(strURL, mapParam, listHttpToolUploadFile, mapRequestProperty, null, iHttpToolListener, VALUE_INT_UPLOAD_CONNECT_TIMEOUT, VALUE_INT_UPLOAD_READ_TIMEOUT, true);
    }

    @Override
    public void uploadFileByPostAsync(final String strURL, final Map<String, String> mapParam, final List<IHttpToolUploadFile> listHttpToolUploadFile, final Map<String, String> mapRequestProperty, final Object objectTag, final IHttpToolListener iHttpToolListener, final int nConnectTimeout, final int nReadTimeout, final boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL))
            return;

        final HttpToolResult httpToolResult = (HttpToolResult) XLibFactory.getInstance().createInstance(IHttpToolResult.class, HttpToolResult.class);
        mIXThreadPool.addTask(new IXThreadPoolListener() {
            @Override
            public void onTaskRun() {
                uploadFile(strURL, mapParam, listHttpToolUploadFile, mapRequestProperty, httpToolResult, nConnectTimeout, nReadTimeout, bIsNeedEncrypt);
            }

            @Override
            public void onTaskComplete() {
                if (null != iHttpToolListener) {
                    iHttpToolListener.onUploadFileByPostAsyncComplete(strURL, mapParam, listHttpToolUploadFile, objectTag, httpToolResult);
                } else {
                    for (IHttpToolListener listener : getListenerList())
                        listener.onUploadFileByPostAsyncComplete(strURL, mapParam, listHttpToolUploadFile, objectTag, httpToolResult);
                }
            }

            @Override
            public void onMessage(Message msg) {
            }
        });
    }

    private void requestByGet(HttpToolTask httpToolTask, HttpToolResult httpToolResult) {
        if (null == httpToolTask || null == httpToolResult)
            return;

        try {
            String strURL = httpToolTask.mURL;
            if (null != httpToolTask.mMapParam && !httpToolTask.mMapParam.isEmpty()) {
                StringBuffer sbParam = new StringBuffer();
                sbParam.append("?");
                for (Map.Entry<String, String> entry : httpToolTask.mMapParam.entrySet()) {
                    if (httpToolTask.mIsNeedEncrypt) {
                        sbParam.append(entry.getKey())
                                .append("=").
                                append(UtilsEncrypt.byteToHexString(UtilsEncrypt.encryptByBlowFish(entry.getValue().getBytes(), null))).
                                append("&");
                    } else {
                        sbParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                }

                sbParam.deleteCharAt(sbParam.length() - 1);
                strURL += sbParam.toString();
            }

            URL url = new URL(strURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(httpToolTask.mConnectTimeout);
            httpURLConnection.setReadTimeout(httpToolTask.mReadTimeout);

            if (null != httpToolTask.mMapRequestProperty && !httpToolTask.mMapRequestProperty.isEmpty()) {
                for (Map.Entry<String, String> entry : httpToolTask.mMapRequestProperty.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            httpURLConnection.connect();

            int nResponseCode = httpURLConnection.getResponseCode();
            httpToolResult.setResponseCode(nResponseCode);

            InputStream is = null;
            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                is = httpURLConnection.getInputStream();
            } else {
                is = httpURLConnection.getErrorStream();
            }

            int nAllSize = 0;
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];

            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bis.available());
            while ((nReadSize = bis.read(buffer)) > 0) {
                nAllSize += nReadSize;
                baos.write(buffer, 0, nReadSize);
            }

            is.close();
            bis.close();
            baos.close();

            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                httpToolResult.setSuccess(true);
                httpToolResult.setHeaderFieldMap(httpURLConnection.getHeaderFields());
                if (httpToolTask.mIsNeedEncrypt) {
                    httpToolResult.setBuffer(UtilsEncrypt.decryptByBlowFish(UtilsEncrypt.stringHexToByte(new String(baos.toByteArray())), null));
                } else {
                    httpToolResult.setBuffer(baos.toByteArray());
                }
            } else {
                httpToolResult.setException(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpToolResult.setException(e.toString());
        }
    }

    private void requestByPost(HttpToolTask httpToolTask, HttpToolResult httpToolResult) {
        if (null == httpToolTask || null == httpToolResult)
            return;

        try {
            URL url = new URL(httpToolTask.mURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(httpToolTask.mConnectTimeout);
            httpURLConnection.setReadTimeout(httpToolTask.mReadTimeout);

            if (null != httpToolTask.mMapRequestProperty && !httpToolTask.mMapRequestProperty.isEmpty()) {
                for (Map.Entry<String, String> entry : httpToolTask.mMapRequestProperty.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            httpURLConnection.connect();

            if (null != httpToolTask.mMapParam && !httpToolTask.mMapParam.isEmpty()) {
                StringBuffer sbParam = new StringBuffer();
                for (Map.Entry<String, String> entry : httpToolTask.mMapParam.entrySet()) {
                    if (httpToolTask.mIsNeedEncrypt) {
                        sbParam.append(entry.getKey())
                                .append("=").
                                append(UtilsEncrypt.byteToHexString(UtilsEncrypt.encryptByBlowFish(entry.getValue().getBytes(), null))).
                                append("&");
                    } else {
                        sbParam.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                }

                sbParam.deleteCharAt(sbParam.length() - 1);

                OutputStream os = httpURLConnection.getOutputStream();
                os.write(sbParam.toString().getBytes());
                os.flush();
                os.close();
            }

            int nResponseCode = httpURLConnection.getResponseCode();
            httpToolResult.setResponseCode(nResponseCode);

            InputStream is = null;
            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                is = httpURLConnection.getInputStream();
            } else {
                is = httpURLConnection.getErrorStream();
            }

            int nAllSize = 0;
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];

            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bis.available());
            while ((nReadSize = bis.read(buffer)) > 0) {
                nAllSize += nReadSize;
                baos.write(buffer, 0, nReadSize);
            }

            is.close();
            bis.close();
            baos.close();

            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                httpToolResult.setSuccess(true);
                httpToolResult.setHeaderFieldMap(httpURLConnection.getHeaderFields());
                if (httpToolTask.mIsNeedEncrypt) {
                    httpToolResult.setBuffer(UtilsEncrypt.decryptByBlowFish(UtilsEncrypt.stringHexToByte(new String(baos.toByteArray())), null));
                } else {
                    httpToolResult.setBuffer(baos.toByteArray());
                }
            } else {
                httpToolResult.setException(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpToolResult.setException(e.toString());
        }
    }

    private void uploadFile(String strURL, Map<String, String> mapParam, List<IHttpToolUploadFile> listHttpToolUploadFile, Map<String, String> mapRequestProperty, HttpToolResult httpToolResult, int nConnectTimeout, int nReadTimeout, boolean bIsNeedEncrypt) {
        if (TextUtils.isEmpty(strURL) || null == httpToolResult)
            return;

        String strTwoHyphens = "--";
        String strBoundary = "*****";
        String strEnd = "\r\n";

        try {
            URL url = new URL(strURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setConnectTimeout(null == listHttpToolUploadFile ? nConnectTimeout : nConnectTimeout + nConnectTimeout * listHttpToolUploadFile.size());
            httpURLConnection.setReadTimeout(null == listHttpToolUploadFile ? nReadTimeout : nReadTimeout + nReadTimeout * listHttpToolUploadFile.size());

            if (null != mapRequestProperty && !mapRequestProperty.isEmpty()) {
                for (Map.Entry<String, String> entry : mapRequestProperty.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            if (null != mapRequestProperty && !mapRequestProperty.isEmpty() && mapRequestProperty.containsKey("Content-Type")) {
                String strContentType = mapRequestProperty.get("Content-Type");
                int nIndex = strContentType.indexOf("boundary=");
                if (nIndex > 0) {
                    strBoundary = strContentType.substring(nIndex + "boundary=".length());
                }
            } else {
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + strBoundary);
            }

            httpURLConnection.connect();

            OutputStream os = httpURLConnection.getOutputStream();
            StringBuffer sb = new StringBuffer();
            if (null != mapParam && !mapParam.isEmpty()) {
                for (Map.Entry<String, String> entry : mapParam.entrySet()) {
                    sb.append(strTwoHyphens + strBoundary + strEnd);
                    sb.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + strEnd);
                    sb.append(strEnd);
                    if (bIsNeedEncrypt) {
                        sb.append(UtilsEncrypt.byteToHexString(UtilsEncrypt.encryptByBlowFish(entry.getValue().getBytes(), null)));
                    } else {
                        sb.append(entry.getValue());
                    }

                    sb.append(strEnd);
                }

                os.write(sb.toString().getBytes());
            }

            if (null != listHttpToolUploadFile) {
                for (int nIndex = 0; nIndex < listHttpToolUploadFile.size(); nIndex++) {
                    IHttpToolUploadFile iHttpToolUploadFile = listHttpToolUploadFile.get(nIndex);
                    if (null == iHttpToolUploadFile)
                        continue;

                    if (TextUtils.isEmpty(iHttpToolUploadFile.getFilePath()) || TextUtils.isEmpty(iHttpToolUploadFile.getName()))
                        continue;

                    sb = new StringBuffer();
                    sb.append(strTwoHyphens + strBoundary + strEnd);
                    sb.append("Content-Disposition: " + (TextUtils.isEmpty(iHttpToolUploadFile.getContentDisposition()) ? "form-data" : iHttpToolUploadFile.getContentDisposition()) + "; ");
                    sb.append("name=\"" + iHttpToolUploadFile.getName() + "\"; ");
                    sb.append("filename=\"" + (TextUtils.isEmpty(iHttpToolUploadFile.getFileName()) ? UtilsFile.getTargetName(iHttpToolUploadFile.getFilePath()) : iHttpToolUploadFile.getFileName()) + "\"");
                    sb.append(strEnd);
                    sb.append("Content-Type: " + (TextUtils.isEmpty(iHttpToolUploadFile.getContentType()) ? UtilsMediaStore.getMimeType(iHttpToolUploadFile.getFilePath()) : iHttpToolUploadFile.getContentType()));
                    sb.append(strEnd);
                    sb.append(strEnd);
                    os.write(sb.toString().getBytes());

                    FileInputStream fis = new FileInputStream(iHttpToolUploadFile.getFilePath());
                    int nReadSize = 0;
                    byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
                    while ((nReadSize = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, nReadSize);
                    }

                    fis.close();
                    os.write(strEnd.getBytes());
                }
            }

            os.write((strTwoHyphens + strBoundary + strTwoHyphens + strEnd).getBytes());
            os.flush();
            os.close();

            int nResponseCode = httpURLConnection.getResponseCode();
            httpToolResult.setResponseCode(nResponseCode);

            InputStream is = null;
            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                is = httpURLConnection.getInputStream();
            } else {
                is = httpURLConnection.getErrorStream();
            }

            int nAllSize = 0;
            int nReadSize = 0;
            byte[] buffer = new byte[UtilsEnv.VALUE_INT_BUFFER_SIZE];
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(bis.available());
            while ((nReadSize = bis.read(buffer)) > 0) {
                nAllSize += nReadSize;
                baos.write(buffer, 0, nReadSize);
            }

            is.close();
            bis.close();
            baos.close();

            if (nResponseCode == HttpURLConnection.HTTP_OK
                    || nResponseCode == HttpURLConnection.HTTP_CREATED
                    || nResponseCode == HttpURLConnection.HTTP_ACCEPTED) {
                httpToolResult.setSuccess(true);
                httpToolResult.setHeaderFieldMap(httpURLConnection.getHeaderFields());
                if (bIsNeedEncrypt) {
                    httpToolResult.setBuffer(UtilsEncrypt.decryptByBlowFish(UtilsEncrypt.stringHexToByte(new String(baos.toByteArray())), null));
                } else {
                    httpToolResult.setBuffer(baos.toByteArray());
                }
            } else {
                httpToolResult.setException(baos.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            httpToolResult.setException(e.toString());
        }
    }

    class HttpToolTask {
        public int mRequestType = VALUE_INT_REQUEST_GET_TYPE;
        public int mConnectTimeout = VALUE_INT_CONNECT_TIMEOUT;
        public int mReadTimeout = VALUE_INT_READ_TIMEOUT;
        public String mURL = null;
        public Map<String, String> mMapParam = null;
        public Map<String, String> mMapRequestProperty = null;
        public boolean mIsNeedEncrypt = false;

        public static final int VALUE_INT_REQUEST_GET_TYPE = 0x1000;
        public static final int VALUE_INT_REQUEST_POST_TYPE = 0x1001;
    }
}
