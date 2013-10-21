package com.vectorcat.ingamus.example.particles;


public interface Particle {

	public static class F {
		public double x, y, dx, dy, ax, ay, lifeSeconds;

		public F() {

		}

		public F(Particle particle) {
			get(particle);
		}

		public void get(Particle particle) {
			x = particle.getX();
			y = particle.getY();
			dx = particle.getDx();
			dy = particle.getDy();
			ax = particle.getAx();
			ay = particle.getAy();
			lifeSeconds = particle.getLifeSeconds();
		}

		public void set(Particle particle) {
			particle.setPosition(x, y);
			particle.setVelocity(dx, dy);
			particle.setAcceleration(ax, ay);
			particle.setLifeSeconds(lifeSeconds);
		}
	}

	public void dispose();

	public double getAx();

	public double getAy();

	public double getDx();

	public double getDy();

	public double getLifeSeconds();

	public double getX();

	public double getY();

	public void setAcceleration(double ax, double ay);

	public void setAx(double ax);

	public void setAy(double ay);

	public void setDx(double dx);

	public void setDy(double dy);

	public void setLifeSeconds(double lifeSeconds);

	public void setPosition(double x, double y);

	public void setVelocity(double dx, double dy);

	public void setX(double x);

	public void setY(double y);

}
