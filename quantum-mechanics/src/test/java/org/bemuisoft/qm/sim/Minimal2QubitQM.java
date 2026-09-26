package org.bemuisoft.qm.sim;

import org.bemuisoft.quantum.test.api.AbstractExperiment;
import org.bemuisoft.quantum.test.api.QubitTester;

public class Minimal2QubitQM extends AbstractExperiment<QubitTester> {

	public static void main(String[] args) {
		try {
			QubitQM.debug = false;
			Minimal2QubitQM test = new Minimal2QubitQM(3);	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Minimal2QubitQM(int n) {
		super(n, QubitTester.factory(MinimalQubitQM.analyzerFactory(n, true), QubitQM.factory(n, true)));
	}

	@Override
	public void run() {
		randomPure();
		randomMixed();
		multiControl();
//		bell();
		measureAll();
	}

	void randomPure() {
		q(0).ry(randomTheta()).rz(random2Pi());
		q(1).ry(randomTheta()).rz(random2Pi());
		q(2).ry(randomTheta()).rz(random2Pi());
	}

	void randomMixed() {
		entangleRandom(q(0), q(1));
		entangleRandom(q(1), q(2));
		entangleRandom(q(2), q(0));
	}

	void multiControl() {
		q(0).h();
		q(1).h();
		q(2).h();
		q(2).ccp(HALF_PI, q(0), q(1));
		q(2).ccz(q(0), q(1));
		q(2).toffoli(q(0), q(1));
		q(2).c(Z, q(0), q(1));
		q(2).c(X, q(0), q(1));
		q(2).c(Y, q(0), q(1));
		q(2).c(H, q(0), q(1));
		q(2).cu(1, 2, 3, q(0), q(1));
		q(2).cr(Z, 1, q(0), q(1));
		q(2).cr(X, 1, q(0), q(1));
		q(2).cr(Y, 1, q(0), q(1));
		q(2).cr(H, 1, q(0), q(1));
	}

	void bell() {
		QubitQM.debug = true;
		q(0).h();
		q(1).h();
		q(1).cz(q(0));
//		q(1).h();
		q(1).ry(-HALF_PI);
		for (int i = 0; i < 4; i++) {
			q(1).rx(HALF_PI);
			log(q(0), 0);
			log(q(0), 1);
			log(q(1), 0);
			log(q(1), 1);
		}
	}

	void log(QubitTester q, int i) {
		StringBuilder sb = new StringBuilder(q.getLabel());
		if (q.getX(i) != 0.0) {
			sb.append(" x.").append(i).append('=').append(q.getX(i));
		}
		if (q.getY(i) != 0.0) {
			sb.append(" y.").append(i).append('=').append(q.getY(i));
		}
		if (q.getZ(i) != 0.0) {
			sb.append(" z.").append(i).append('=').append(q.getZ(i));
		}
		log(sb.toString());
	}

}
