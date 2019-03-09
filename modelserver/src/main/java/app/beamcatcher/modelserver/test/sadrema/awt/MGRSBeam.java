package app.beamcatcher.modelserver.test.sadrema.awt;

import java.util.UUID;

public class MGRSBeam {

	private UUID uuid = UUID.randomUUID();

	private Integer x;
	private Integer y;

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MGRSBeam) {
			return this.uuid.equals(((MGRSBeam) obj).uuid);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

}
