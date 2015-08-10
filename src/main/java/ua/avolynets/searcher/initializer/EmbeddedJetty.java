package ua.avolynets.searcher.initializer;

import org.apache.jasper.servlet.JspServlet;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class EmbeddedJetty {

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedJetty.class);
    private static final int DEFAULT_PORT = 8081;
    private static final String CONTEXT_PATH = "/";
    private static final String CONFIG_LOCATION = "ua.avolynets.searcher.config";
    private static final String MAPPING_URL = "/*";
    private static final String DEFAULT_PROFILE = "dev";
    private static final String PROJECT_RELATIVE_PATH_TO_WEBAPP = "src/main/webapp";

    public static void main(String[] args) throws Exception {
        new EmbeddedJetty().startJetty(getPortFromArgs(args));
    }

    private static int getPortFromArgs(String[] args) {
        if (args.length > 0) {
            try {
                return Integer.valueOf(args[0]);
            } catch (NumberFormatException ignore) {
            }
        }
        logger.debug("No server port configured, falling back to {}", DEFAULT_PORT);
        return DEFAULT_PORT;
    }

    private void startJetty(int port) throws Exception {
        logger.debug("Starting server at port {}", port);

        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");

        Server server = new Server(port);
        server.setHandler(getServletContextHandler(getContext()));
        server.start();
        logger.info("Server started at port {}", port);
        server.join();
    }


    private   WebAppContext  getServletContextHandler(WebApplicationContext context) throws IOException {

        WebAppContext webContext = new WebAppContext();
        webContext.setErrorHandler(null);
        webContext.setContextPath(CONTEXT_PATH);
        webContext.setWar(PROJECT_RELATIVE_PATH_TO_WEBAPP);

        webContext.addServlet(new ServletHolder(new DispatcherServlet(context)), MAPPING_URL);
        webContext.addEventListener(new ContextLoaderListener(context));
        webContext.setResourceBase(new ClassPathResource("webapp").getURI().toString());

        //setupJspHandler(webContext);
        return webContext;
    }

    private   void setupJspHandler(WebAppContext context) {

        //Ensure the jsp engine is initialized correctly
        JettyJasperInitializer sci = new JettyJasperInitializer();

        ServletContainerInitializersStarter sciStarter = new ServletContainerInitializersStarter(context);
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        context.setAttribute("org.eclipse.jetty.containerInitializers", initializers);
        context.addBean(sciStarter, true);

        // Set Classloader of Context to be sane (needed for JSTL)
        // JSP requires a non-System classloader, this simply wraps the
        // embedded System classloader in a way that makes it suitable
        // for JSP to use
        ClassLoader jspClassLoader = new URLClassLoader(new URL[0], this.getClass().getClassLoader());
        context.setClassLoader(jspClassLoader);

        // Add JSP Servlet (must be named "jsp")
        ServletHolder holderJsp = new ServletHolder("jsp",JspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "INFO");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.7");
        holderJsp.setInitParameter("compilerSourceVM", "1.7");
        holderJsp.setInitParameter("keepgenerated", "true");
        context.addServlet(holderJsp, "*.jsp");
    }


    private static WebApplicationContext getContext() {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.setConfigLocation(CONFIG_LOCATION);
        context.getEnvironment().setDefaultProfiles(DEFAULT_PROFILE);

        return context;
    }

}
