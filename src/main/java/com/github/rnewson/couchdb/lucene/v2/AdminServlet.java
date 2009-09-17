package com.github.rnewson.couchdb.lucene.v2;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.IndexWriter;

import com.github.rnewson.couchdb.lucene.v2.LuceneGateway.WriterCallback;

/**
 * Allows purge and optimize calls.
 * 
 * <ul>
 * <li>_expunge
 * <li>_optimize
 * <li>_pause
 * <li>_resume
 * </ul>
 * 
 * @author rnewson
 * 
 */
public final class AdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private LuceneGateway holders;

    AdminServlet(final LuceneGateway holders) {
        this.holders = holders;
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {
        if (req.getParameter("index") == null) {
            resp.sendError(400, "Missing index attribute.");
            return;
        }
        final String indexName = req.getParameter("index");

        if ("/_expunge".equals(req.getPathInfo())) {
            holders.withWriter(indexName, new WriterCallback<Void>() {
                @Override
                public Void callback(final IndexWriter writer) throws IOException {
                    writer.expungeDeletes(false);
                    return null;
                }
            });
            resp.setStatus(202);
            return;
        }

        if ("/_optimize".equals(req.getPathInfo())) {
            holders.withWriter(indexName, new WriterCallback<Void>() {
                @Override
                public Void callback(final IndexWriter writer) throws IOException {
                    writer.optimize(false);
                    return null;
                }
            });
            resp.setStatus(202);
            return;
        }

        resp.sendError(400);
    }

}
