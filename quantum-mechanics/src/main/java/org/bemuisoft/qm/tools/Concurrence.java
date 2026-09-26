package org.bemuisoft.qm.tools;

import java.util.Arrays;

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.qm.core.Gate;
import org.bemuisoft.qm.core.PureQuantumSystem;

/**
 * Calculates the concurrence of two qubits.
 * <p>
 * See <a href="https://en.wikipedia.org/wiki/Concurrence_(quantum_computing)">
 * Concurrence on Wikipedia</a>
 * for more information about quantum concurrence.
 * 
 * @author Benno Muilwijk
 */
public class Concurrence extends ComplexBase {

	private static final Matrix YY = Matrix.tensor(Gate.Y, Gate.Y);

	private Matrix rho;		// the 4x4 density matrix for the two qubits
	private double value;	// the calculated correspondence value

	/**
	 * Constructor with qubit labels.
	 * 
	 * @param qs	the quantum system that includes both qubits
	 * @param q1	label of qubit 1
	 * @param q2	label of qubit 2
	 */
	public Concurrence(PureQuantumSystem qs, char q1, char q2) {
		this(qs, qs.qubitIndex(q1), qs.qubitIndex(q2));
	}

	/**
	 * Constructor with qubit labels.
	 * 
	 * @param qs		the quantum system that includes both qubits
	 * @param qIndex1	index of qubit 1
	 * @param qIndex2	index of qubit 2
	 */
	public Concurrence(PureQuantumSystem qs, int qIndex1, int qIndex2) {
		rho = qs.reducedDensityMatrix(qIndex1, qIndex2);
		boolean easy = false;
		value = round(easy ? calculateEasy() : calculateHard(), 14);
//		check(Math.abs(calculateHard() - value) < 1e-8, "Inconsistent result");
	}

	private double calculateEasy() {
		// easy way (works for reduced density matrix of pure state |psi⟩⟨psi|)
		// so it is said, but only it doesn't... :(
		// TODO check citations in https://en.wikipedia.org/wiki/Concurrence_(quantum_computing)
		return Math.sqrt(2.0 * (1.0 - Matrix.product(rho, rho).trace().realPart()));
	}

	private double calculateHard() {
		// hard way (also works for reduced density matrix of mixed state, e.g. due to decoherence)
		Matrix rhoSpinFlipped = Matrix.product(YY, rho.conjugate(), YY);
		Matrix sqrt = rho.sqrt();
		Matrix R2 = Matrix.product(sqrt, rhoSpinFlipped, sqrt);
		Matrix R = R2.sqrt();								// this matrix R is Hermitian, but trace can range from 0 to 1
//		Matrix R = Matrix.product(rho, rhoSpinFlipped);		// this matrix R is not Hermitian
		EigenDecomposition ed = EigenDecomposition.get(R);
		double[] values = new double[ed.size()];
		// get square roots of the eigenvalues
		for (int i = 0; i < values.length; i++) {
			values[i] = Math.sqrt(ed.getEigenvalue(i).realPart());
		}
		Arrays.sort(values);
		return Math.max(0.0, values[3] - values[2] - values[1] - values[0]);
	}

	/**
	 * Returns the concurrence value.
	 * 
	 * @return	the concurrence value
	 */
	public double value() {
		return value;
	}

}
