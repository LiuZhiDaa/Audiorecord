package ulric.li.tool.intf;

import java.util.List;
import java.util.Map;

public abstract class IHttpToolListener {
    public void onRequestToBufferByGetAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult){};

    public void onRequestToBufferByPostAsyncComplete(String strURL, Map<String, String> mapParam, Object objectTag, IHttpToolResult iHttpToolResult){};

    public void onUploadFileByPostAsyncComplete(String strURL, Map<String, String> mapParam, List<IHttpToolUploadFile> listUploadFile, Object objectTag, IHttpToolResult iHttpToolResult){};
}
