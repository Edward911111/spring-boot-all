package com.lance.shiro.util;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
/**
 * An example Java client to authenticate against CAS using REST services.
 * Please ensure you have followed the necessary setup found on the <a
 * href="http://www.ja-sig.org/wiki/display/CASUM/RESTful+API">wiki</a>.
 *
 * @author <a href="mailto:jieryn@gmail.com">jesse lauren farinacci</a>
 * @since 3.4.2
 */
public final class CASUtility {
    private static final Logger LOG = Logger.getLogger(CASUtility.class.getName());
    private static final String CAS_SERVVER_DOMAIN = "htttps://master:8443/cas";
    
//    public static PropertiesReader pr = null;
    private CASUtility() {
        // static-only access
    }
    public static String getTicket(final HttpServletRequest request,
            final HttpServletResponse response, final String server,
            final String username, final String password, final String service) {
                notNull(server, "server must not be null");
                notNull(username, "username must not be null");
                notNull(password, "password must not be null");
                notNull(service, "service must not be null");
        return getServiceTicket(request, response, server, getTicketGrantingTicket(response, server, username, password), service);
    }
    private static String getServiceTicket(final HttpServletRequest request,
            final HttpServletResponse res, final String server,
            final String ticketGrantingTicket, final String service) {
        if (ticketGrantingTicket == null)
            return null;
        res.setHeader("P3P","CP=CAO PSA OUR");
                                                                      
        String broswer = "";
        try {
            String agent = request.getHeader("User-Agent");
            StringTokenizer st = new StringTokenizer(agent,";");
            st.nextToken();
            broswer = st.nextToken();
            LOG.info("User's Browser is "+broswer);
        }catch(Exception e) {
            LOG.info("Fail to get User's Browser Type.");
        }
                                                                      
        LOG.info("ticketGrantingTicket : " + ticketGrantingTicket);
        Cookie c1 = new Cookie("CASTGC", ticketGrantingTicket);
                                                                      
//        pr = PropertiesReader.getInstance("");
//        String domain = pr.getPropertiy("cas.server.domain");
        if (broswer != null && broswer.trim().startsWith("MSIE")){
            c1.setDomain(CAS_SERVVER_DOMAIN);
            c1.setPath("/");
        } else {
            c1.setDomain(CAS_SERVVER_DOMAIN);
            c1.setPath("/cas/");
        }
        res.addCookie(c1);
        final HttpClient client = new HttpClient();
        final PostMethod post = new PostMethod(server + "/" + ticketGrantingTicket);
        post.setRequestBody(new NameValuePair[] { new NameValuePair("service", service) });
        try {
            client.executeMethod(post);
            final String response = post.getResponseBodyAsString();
            switch (post.getStatusCode()) {
            case 200:
                return response;
            default:
                LOG.warning("Invalid response code (" + post.getStatusCode() + ") from CAS server!");
                LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));
                break;
            }
        } catch (final IOException e) {
            LOG.warning(e.getMessage());
        } finally {
            post.releaseConnection();
        }
        return null;
    }
    public static String getTicketGrantingTicket(final HttpServletResponse res,
            final String server, final String username, final String password) {
        final HttpClient client = new HttpClient();
        final PostMethod post = new PostMethod(server);
        post.setRequestBody(new NameValuePair[] {
                new NameValuePair("username", username),
                new NameValuePair("password", password) });
        try {
            client.executeMethod(post);
            final String response = post.getResponseBodyAsString();
            switch (post.getStatusCode()) {
            case 201: {
                final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(response);
                if (matcher.matches())
                    return matcher.group(1);
                LOG.warning("Successful ticket granting request, but no ticket found!");
                LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));
                break;
            }
            default:
                LOG.warning("Invalid response code (" + post.getStatusCode() + ") from CAS server!");
                LOG.info("Response (1k): " + response.substring(0, Math.min(1024, response.length())));
                break;
            }
        }
        catch (final IOException e) {
            LOG.warning(e.getMessage());
        }
        finally {
            post.releaseConnection();
        }
        return null;
    }
    private static void notNull(final Object object, final String message) {
        if (object == null)
            throw new IllegalArgumentException(message);
    }
    
    public static void main(String[] args) {
		
	}
}