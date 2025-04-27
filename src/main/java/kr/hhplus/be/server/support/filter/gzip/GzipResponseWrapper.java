package kr.hhplus.be.server.support.filter.gzip;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class GzipResponseWrapper extends HttpServletResponseWrapper {
    private GzipResponseStream gzipStream;
    private PrintWriter printWriter;
    private boolean contentLengthSet = false;

    public GzipResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
    }

    public void setContentLength(int length) {
        contentLengthSet = true;
    }

    @Override
    public void setContentLengthLong(long length) {
        contentLengthSet = true;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (printWriter != null) {
            throw new IllegalStateException("이미 getWriter()가 호출되었습니다");
        }

        if (gzipStream == null) {
            gzipStream = new GzipResponseStream((HttpServletResponse) getResponse());
        }

        return gzipStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (gzipStream != null) {
            throw new IllegalStateException("이미 getOutputStream()이 호출되었습니다");
        }

        if (printWriter == null) {
            gzipStream = new GzipResponseStream((HttpServletResponse) getResponse());
            printWriter = new PrintWriter(new OutputStreamWriter(gzipStream, getCharacterEncoding()));
        }

        return printWriter;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (printWriter != null) {
            printWriter.flush();
        } else if (gzipStream != null) {
            gzipStream.flush();
        }

        super.flushBuffer();
    }

    public void finish() throws IOException {
        if (printWriter != null) {
            printWriter.close();
        } else if (gzipStream != null) {
            gzipStream.close();
        }
    }
}