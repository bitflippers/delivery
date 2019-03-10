package app.beamcatcher.modelserver.test.sadrema.awt;

import java.awt.Frame;

public class MGRSFrame extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	public MGRSFrame(final MGRSCanvas pMGRSCanvas) {
		this.setSize(pMGRSCanvas.getWidth(), pMGRSCanvas.getHeight());
		this.setVisible(true);
		this.add(pMGRSCanvas);
	}

}
