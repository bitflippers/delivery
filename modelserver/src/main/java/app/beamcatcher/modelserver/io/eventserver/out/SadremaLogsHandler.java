package app.beamcatcher.modelserver.io.eventserver.out;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.ReversedLinesFileReader;

import app.beamcatcher.modelserver.configuration.Configuration;

public class SadremaLogsHandler extends HttpServlet {

	private static final long serialVersionUID = -2522823514489418182L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
		response.setHeader("Content-Type:", "text/html; charset=utf-8");
		File file = new File(Configuration.SADREMA_LOG_FILE);
		final Integer numberOfLines = 10;
		Integer counter = 0;
		final Charset charset = Charset.defaultCharset();
		final ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(file, charset);
		final StringBuffer stringBuffer = new StringBuffer();
		while (counter < numberOfLines) {
			final String line = reversedLinesFileReader.readLine();
			stringBuffer.append(line + "</br>");
			counter++;
		}

		final String result = stringBuffer.toString();

		reversedLinesFileReader.close();

		response.getWriter().println(result);

	}

}
