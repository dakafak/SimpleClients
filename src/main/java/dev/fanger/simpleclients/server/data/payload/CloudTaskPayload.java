package dev.fanger.simpleclients.server.data.payload;

import java.io.Serializable;

public class CloudTaskPayload extends Payload implements Serializable {

    public CloudTaskPayload(Serializable data, String payloadUrl) {
        super(data, payloadUrl);
    }

}
