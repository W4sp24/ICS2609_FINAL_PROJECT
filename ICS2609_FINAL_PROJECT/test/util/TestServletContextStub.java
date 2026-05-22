package util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * Minimal ServletContext stub for unit/integration tests.
 * Only getInitParameter() returns real values; all other methods are no-ops.
 */
public class TestServletContextStub implements ServletContext {

    private final Map<String, String> params = new HashMap<String, String>();
    private final Map<String, Object> attributes = new HashMap<String, Object>();

    public void setParam(String name, String value) {
        params.put(name, value);
    }

    // ── Factory methods ────────────────────────────────────────────────────

    public static TestServletContextStub forDerby() {
        TestServletContextStub ctx = new TestServletContextStub();
        ctx.setParam("Derby_Driver", "org.apache.derby.jdbc.ClientDriver");
        ctx.setParam("Derby_URL",    "jdbc:derby://localhost:1527/LoginDB");
        ctx.setParam("Derby_User",   "app");
        ctx.setParam("Derby_Pass",   "app");
        return ctx;
    }

    public static TestServletContextStub forMySQL() {
        TestServletContextStub ctx = new TestServletContextStub();
        ctx.setParam("MySQL_Driver", "com.mysql.cj.jdbc.Driver");
        ctx.setParam("MySQL_URL",    "jdbc:mysql://localhost:3306/course_management_db");
        ctx.setParam("MySQL_User",   "root");
        ctx.setParam("MySQL_Pass",   "app");
        return ctx;
    }

    public static TestServletContextStub forPostgres() {
        TestServletContextStub ctx = new TestServletContextStub();
        ctx.setParam("Postgres_Driver", "org.postgresql.Driver");
        ctx.setParam("Postgres_URL",    "jdbc:postgresql://localhost:5432/postgres");
        ctx.setParam("Postgres_User",   "postgres");
        ctx.setParam("Postgres_Pass",   "app");
        return ctx;
    }

    public static TestServletContextStub forAll() {
        TestServletContextStub ctx = new TestServletContextStub();
        ctx.setParam("Derby_Driver",    "org.apache.derby.jdbc.ClientDriver");
        ctx.setParam("Derby_URL",       "jdbc:derby://localhost:1527/LoginDB");
        ctx.setParam("Derby_User",      "app");
        ctx.setParam("Derby_Pass",      "app");
        ctx.setParam("MySQL_Driver",    "com.mysql.cj.jdbc.Driver");
        ctx.setParam("MySQL_URL",       "jdbc:mysql://localhost:3306/course_management_db");
        ctx.setParam("MySQL_User",      "root");
        ctx.setParam("MySQL_Pass",      "app");
        ctx.setParam("Postgres_Driver", "org.postgresql.Driver");
        ctx.setParam("Postgres_URL",    "jdbc:postgresql://localhost:5432/postgres");
        ctx.setParam("Postgres_User",   "postgres");
        ctx.setParam("Postgres_Pass",   "app");
        return ctx;
    }

    // ── ServletContext methods ─────────────────────────────────────────────

    @Override public String getInitParameter(String name) { return params.get(name); }
    @Override public Enumeration<String> getInitParameterNames() { return null; }
    @Override public boolean setInitParameter(String name, String value) { params.put(name, value); return true; }

    @Override public Object getAttribute(String name) { return attributes.get(name); }
    @Override public Enumeration<String> getAttributeNames() { return null; }
    @Override public void setAttribute(String name, Object object) { attributes.put(name, object); }
    @Override public void removeAttribute(String name) { attributes.remove(name); }

    @Override public String getContextPath() { return ""; }
    @Override public ServletContext getContext(String uripath) { return null; }
    @Override public int getMajorVersion() { return 3; }
    @Override public int getMinorVersion() { return 1; }
    @Override public int getEffectiveMajorVersion() { return 3; }
    @Override public int getEffectiveMinorVersion() { return 1; }
    @Override public String getMimeType(String file) { return null; }
    @Override public Set<String> getResourcePaths(String path) { return null; }
    @Override public URL getResource(String path) throws MalformedURLException { return null; }
    @Override public InputStream getResourceAsStream(String path) { return null; }
    @Override public RequestDispatcher getRequestDispatcher(String path) { return null; }
    @Override public RequestDispatcher getNamedDispatcher(String name) { return null; }
    @Override public Servlet getServlet(String name) throws ServletException { return null; }
    @Override public Enumeration<Servlet> getServlets() { return null; }
    @Override public Enumeration<String> getServletNames() { return null; }
    @Override public void log(String msg) {}
    @Override public void log(Exception exception, String msg) {}
    @Override public void log(String message, Throwable throwable) {}
    @Override public String getRealPath(String path) { return null; }
    @Override public String getServerInfo() { return "TestStub/1.0"; }
    @Override public String getServletContextName() { return "TestContext"; }
    @Override public ServletRegistration.Dynamic addServlet(String n, String c) { return null; }
    @Override public ServletRegistration.Dynamic addServlet(String n, Servlet s) { return null; }
    @Override public ServletRegistration.Dynamic addServlet(String n, Class<? extends Servlet> c) { return null; }
    @Override public <T extends Servlet> T createServlet(Class<T> c) throws ServletException { return null; }
    @Override public ServletRegistration getServletRegistration(String n) { return null; }
    @Override public Map<String, ? extends ServletRegistration> getServletRegistrations() { return null; }
    @Override public FilterRegistration.Dynamic addFilter(String n, String c) { return null; }
    @Override public FilterRegistration.Dynamic addFilter(String n, Filter f) { return null; }
    @Override public FilterRegistration.Dynamic addFilter(String n, Class<? extends Filter> c) { return null; }
    @Override public <T extends Filter> T createFilter(Class<T> c) throws ServletException { return null; }
    @Override public FilterRegistration getFilterRegistration(String n) { return null; }
    @Override public Map<String, ? extends FilterRegistration> getFilterRegistrations() { return null; }
    @Override public SessionCookieConfig getSessionCookieConfig() { return null; }
    @Override public void setSessionTrackingModes(Set<SessionTrackingMode> modes) {}
    @Override public Set<SessionTrackingMode> getDefaultSessionTrackingModes() { return null; }
    @Override public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() { return null; }
    @Override public void addListener(String className) {}
    @Override public <T extends EventListener> void addListener(T t) {}
    @Override public void addListener(Class<? extends EventListener> c) {}
    @Override public <T extends EventListener> T createListener(Class<T> c) throws ServletException { return null; }
    @Override public JspConfigDescriptor getJspConfigDescriptor() { return null; }
    @Override public ClassLoader getClassLoader() { return getClass().getClassLoader(); }
    @Override public void declareRoles(String... roleNames) {}
    public String getVirtualServerName() { return "localhost"; }
}
