package fm.mixer.gateway.common.filter.model;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;

import java.io.ByteArrayInputStream;

public class CachedBodyServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream stream;

    public CachedBodyServletInputStream(byte[] cachedBody) {
        this.stream = new ByteArrayInputStream(cachedBody);
    }

    @Override
    public int read() {
        return stream.read();
    }

    @Override
    public boolean isFinished() {
        return stream.available() == 0;
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReadListener(final ReadListener readListener) {
        throw new UnsupportedOperationException();
    }
}