package kr.hhplus.be.server.support.filter.gzip;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipResponseStream extends ServletOutputStream {
    private GZIPOutputStream gzipOutputStream;
    private ServletOutputStream outputStream;
    private boolean closed = false;

    public GzipResponseStream(HttpServletResponse response) throws IOException {
        this.outputStream = response.getOutputStream();
        this.gzipOutputStream = new GZIPOutputStream(this.outputStream);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        gzipOutputStream.finish();
        gzipOutputStream.close();
        closed = true;
    }

    @Override
    public void flush() throws IOException {
        if (!closed) {
            gzipOutputStream.flush();
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (!closed) {
            gzipOutputStream.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        if (!closed) {
            gzipOutputStream.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (!closed) {
            gzipOutputStream.write(b, off, len);
        }
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        try {
            outputStream.setWriteListener(writeListener);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
    }
}