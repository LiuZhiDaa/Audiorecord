package ulric.li.tool.intf;

import java.util.List;
import java.util.Map;

import ulric.li.xlib.intf.IXObject;

public interface IHttpToolResult extends IXObject {
    boolean isSuccess();

    int getResponseCode();

    byte[] getBuffer();

    Map<String, List<String>> getHeaderFieldMap();

    String getException();

    <T> T getObject(Class<T> cls);
}
