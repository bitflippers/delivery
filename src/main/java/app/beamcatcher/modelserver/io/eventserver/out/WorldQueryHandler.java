package app.beamcatcher.modelserver.io.eventserver.out;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import app.beamcatcher.modelserver.io.WorldSemaphore;
import app.beamcatcher.modelserver.model.World;
import app.beamcatcher.modelserver.persistence.WorldSingleton;
import app.beamcatcher.modelserver.util.Util;

public class WorldQueryHandler extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2522823514489418182L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);

		System.out.println("WorldQueryHandler requesting lock...");
		try {
			WorldSemaphore.semaphore.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("WorldQueryHandler acquired lock !!!");
		System.out.println("WorldQueryHandler getting to work...");
		final World world = WorldSingleton.INSTANCE;
		response.getWriter().println(Util.getJSON(world));
		System.out.println("WorldQueryHandler finished work ! realising lock !!!");
		WorldSemaphore.semaphore.release();
	}

}
