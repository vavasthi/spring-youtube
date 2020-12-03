package in.springframework.learning.tutorial;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class TutorialJettyCustomizer {
    @Bean
    WebServerFactoryCustomizer<JettyServletWebServerFactory>
    containerCustomizer(@Value("${server.port:8080}") final int port,
                        @Value("${jetty.acceptqueuesize:5000}") final int acceptQueueSize,
                        @Value("${jetty.threadPool.maxThread:2000}") final int maxThreads,
                        @Value("${jetty.threadPool.minThread:8}") final int minThreads,
                        @Value("${jetty.threadPool.idleTimeout:600000}") final int idleTimeout,
                        @Value("${logging.path:/tmp}") final String loggingPath) {

        return container -> {
            container.addServerCustomizers((JettyServerCustomizer)server -> {
                /** Define and enable logging path.
                 *
                 */
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-H");
                String timestamp = LocalDateTime.now().format(dateTimeFormatter);
                String filename = String.format("%s/%s-%s.access.log", loggingPath, "tutorial", timestamp);
                File logFile = new File(filename);
                if (!logFile.getParentFile().exists()) {
                    logFile.getParentFile().mkdirs();
                }
                CustomRequestLog requestLog = new CustomRequestLog(filename);
                RequestLogHandler rlh = new RequestLogHandler();
                rlh.setRequestLog(requestLog);
                Handler[] handlers = server.getHandlers();
                if (handlers == null || handlers.length == 0) {
                    server.setHandler(rlh);
                }
                else {
                    HandlerCollection handlerCollection = new HandlerCollection();
                    for (int i = 0; i < handlers.length; ++i) {
                        handlerCollection.addHandler(handlers[i]);
                    }
                    handlerCollection.addHandler(rlh);
                    server.setHandler(handlerCollection);
                }
                final QueuedThreadPool threadPool = server.getBean(QueuedThreadPool.class);
                threadPool.setMaxThreads(maxThreads);
                threadPool.setMinThreads(minThreads);
                threadPool.setIdleTimeout(idleTimeout);
                threadPool.setName("Jetty-Threadpool");

                for (Connector connector : server.getConnectors()) {
                    ServerConnector serverConnector = (ServerConnector)connector;
                    serverConnector.setPort(port);
                    serverConnector.setAcceptQueueSize(acceptQueueSize);
                    serverConnector.setIdleTimeout(idleTimeout);
                    HttpConnectionFactory httpConnectionFactory
                            = connector.getConnectionFactory(HttpConnectionFactory.class);
                    httpConnectionFactory.getHttpConfiguration().setSendServerVersion(false);
                }
            });
        };
    }
}
