package app.beamcatcher.modelserver.io.eventserver.out;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

import app.beamcatcher.modelserver.configuration.Configuration;

public class HTTPEndpointHandler {
	private Server server;

	public void start() throws Exception {
		server = new Server();
		ServerConnector connector = new ServerConnector(server);
		connector.setPort(Configuration.EVENT_SERVER_HTTP_ENDPOINT_PORT);
		server.setConnectors(new Connector[] { connector });

		ServletHandler servletHandler = new ServletHandler();
		server.setHandler(servletHandler);

		servletHandler.addServletWithMapping(WorldQueryHandler.class, "/world");
		servletHandler.addServletWithMapping(SadremaLogsHandler.class, "/sadremalogs");
		servletHandler.addServletWithMapping(SystemLogsHandler.class, "/systemlogs");
		servletHandler.addServletWithMapping(EventLogsHandler.class, "/eventlogs");

		server.start();

	}

	public void stop() throws Exception {
		server.stop();
	}
}
