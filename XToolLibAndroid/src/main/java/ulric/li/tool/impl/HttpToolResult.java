package ulric.li.tool.impl;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Map;

import ulric.li.tool.intf.IHttpToolResult;

public class HttpToolResult implements IHttpToolResult {
    private boolean mSuccess = false;
    private int mResponseCode = 0;
    private byte[] mBuffer = null;
    private Map<String, List<String>> mMapHeaderField = null;
    private String mException = null;

    public HttpToolResult() {
        _init();
    }

    private void _init() {
    }

    @Override
    public boolean isSuccess() {
        return mSuccess;
    }

    @Override
    public int getResponseCode() {
        return mResponseCode;
    }

    @Override
    public byte[] getBuffer() {
        return mBuffer;
    }

    @Override
    public Map<String, List<String>> getHeaderFieldMap() {
        return mMapHeaderField;
    }

    @Override
    public String getException() {
        return mException;
    }

    @Override
    public <T> T getObject(Class<T> cls) {
        if (mBuffer==null) {
            return null;
        }
        T t=null;
        try {
           t = JSON.parseObject(new String(mBuffer), cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public void setSuccess(boolean bSuccess) {
        mSuccess = bSuccess;
    }

    public void setResponseCode(int nResponseCode) {
        mResponseCode = nResponseCode;
    }

    public void setBuffer(byte[] buffer) {
        mBuffer = buffer;
    }

    public void setHeaderFieldMap(Map<String, List<String>> mapHeaderField) {
        mMapHeaderField = mapHeaderField;
    }

    public void setException(String strException) {
        mException = strException;
    }
}
