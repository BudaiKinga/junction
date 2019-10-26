package request;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.methods.HttpPost;

import java.util.Date;

@Getter
@Setter
public class RawRequest extends Request{
    @Override
    public String getBaseUrl() {
        return BASIC_URL + "raw";
    }
}
