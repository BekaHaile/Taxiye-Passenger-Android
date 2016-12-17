package product.clicklabs.jugnoo.retrofit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by ankit on 17/12/16.
 */

class AddPostParamRequestBody extends RequestBody {

	final RequestBody body;
	final String parameter;

	AddPostParamRequestBody(RequestBody body, Map<String, String> params) {
		this.body = body;
		//this.parameter = "&" + name + "=" + value;
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append("&" + entry.getKey() + "=" + entry.getValue());
		}
		this.parameter = sb.toString();
	}

	@Override
	public long contentLength() throws IOException {
		return body.contentLength() + parameter.length();
	}

	@Override
	public MediaType contentType() {
		return body.contentType();
	}

	@Override
	public void writeTo(BufferedSink bufferedSink) throws IOException {
		body.writeTo(bufferedSink);
		bufferedSink.writeString(parameter, Charset.forName("UTF-8"));
	}

}
