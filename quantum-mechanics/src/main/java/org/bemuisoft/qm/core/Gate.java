package org.bemuisoft.qm.core;

import org.bemuisoft.math.complex.Matrix;

/**
 * A unary quantum gate is mathematically defined by a 2x2 complex matrix.
 * This class implements the definitions of the most commonly used
 * unary gates.
 * Other gates, including multi-qubit gates, can be constructed from these.
 * <p>
 * Essentially, all unary gates perform a rotation of on Bloch vectors.
 * This rotation can also be described as a rotation angle about
 * a rotation axis.
 * For example, the S gate rotates pi/2 about the z-axis
 * and the S-dagger gate rotates -pi/2 about the z-axis.
 * 
 * @author Benno Muilwijk
 */
public class Gate extends Matrix {

	/** Identity gate. */
	public static final Gate I = new Gate(row(1, 0), row(0, 1));
	/** Pauli-X gate, also known as NOT gate. */
	public static final Gate X = new Gate(row(0, 1), row(1, 0));
	/** Pauli-Y gate. */
	public static final Gate Y = new Gate(row(0, i(-1)), row(i(1), 0));
	/** Pauli-Z gate. */
	public static final Gate Z = new Gate(row(1, 0), row(0, -1));
	/** Hadamard gate. */
	public static final Gate H = new Gate(row(SQRT_HALF, SQRT_HALF), row(SQRT_HALF, -SQRT_HALF));
	/** Phase shift S gate. */
	public static final Gate S = new Gate(row(1, 0), row(0, i(1)));
	/** Phase shift S-dagger gate. */
	public static final Gate Sdg  = new Gate(row(1, 0), row(0, i(-1)));
	/** SX gate, also known as square-root NOT gate. */
	public static final Gate SX   = new Gate(row(c(0.5, 0.5), c(0.5, -0.5)), row(c(0.5, -0.5), c(0.5, 0.5)));
	/** SX-dagger gate, also known as square-root NOT-dagger gate. */
	public static final Gate SXdg = new Gate(row(c(0.5, -0.5), c(0.5, 0.5)), row(c(0.5, 0.5), c(0.5, -0.5)));

	/**
	 * Constructs a gate matrix without initializing it.
	 * Proper initialization is required before it can be used.
	 */
	public Gate() {
		super(2, 2);
	}

	/**
	 * Constructs a gate with the specified matrix rows.
	 * 
	 * @param rows	the gate matrix rows
	 */
	public Gate(Number[]... rows) {
		this();
		init(rows);
		setFinal();
	}

	/**
	 * Constructs a gate from the specified matrix.
	 * 
	 * @param m		the gate matrix
	 */
	public Gate(Matrix m) {
		this();
		super.set(m);
		setFinal();
	}

	/**
	 * Returns the inverse gate of this gate.
	 * 
	 * @return	the inverse gate
	 */
	public Gate dagger() {
		// return the conjugate transpose
		Gate gate = new Gate();
		gate.init(0, 0, get(0, 0).conjugate());
		gate.init(0, 1, get(1, 0).conjugate());
		gate.init(1, 0, get(0, 1).conjugate());
		gate.init(1, 1, get(1, 1).conjugate());
		gate.setFinal();
		return gate;
	}

	/**
	 * Returns a new gate based on this gate,
	 * with the same rotation angle, but
	 * with a tilted rotation axis.
	 * <p>
	 * Example 1: if this gate has the z-axis as rotation axis,
	 * then {@code tilt(PI/2, 0)} will return a gate
	 * with the x-axis as rotation axis,
	 * and {@code tilt(PI/2, PI/2)} will return a gate
	 * with the y-axis as rotation axis.
	 * <p>
	 * Example 2: if the rotation axis of this gate is the x-axis,
	 * then {@code tilt(-PI/2, 0)} will return a gate
	 * with the z-axis as rotation axis,
	 * and {@code tilt(0, PI/2)} will return a gate
	 * with the y-axis as rotation axis.
	 * 
	 * @param theta	the relative polar angle
	 * @param phi	the relative azimuthal angle
	 * @return		gate with tilted rotation axis
	 */
	public Gate tilt(double theta, double phi) {
		// rotates the axis of this operation
		Matrix m = this;
		if (theta != 0.0)
			m = Matrix.product(Gate.ry(theta), m, Gate.ry(-theta));
		if (phi != 0.0)
			m = Matrix.product(Gate.p(phi), m, Gate.p(-phi));					// rz also works, but p is more precise
		return new Gate(m);
	}

	@Override
	public void set(int row, int column, Number value) {
		throw new UnsupportedOperationException("Gates may not be modified");
	}

	@Override
	public Matrix set(Matrix m) {
		throw new UnsupportedOperationException("Gates may not be modified");
	}

	//////////////////
	// static methods
	//////////////////

	/**
	 * Returns the identity gate.
	 * 
	 * @return	the identity gate
	 */
	public static Gate i() {
		return I;
	}

	/**
	 * Returns the Pauli-X gate, also known as the NOT gate.
	 * 
	 * @return	the Pauli-X gate
	 */
	public static Gate x() {
		return X;
	}

	/**
	 * Returns the Pauli-Y gate.
	 * 
	 * @return	the Pauli-Y gate
	 */
	public static Gate y() {
		return Y;
	}

	/**
	 * Returns the Pauli-Z gate.
	 * 
	 * @return	the Pauli-Z gate
	 */
	public static Gate z() {
		return Z;
	}

	/**
	 * Returns the Hadamard gate.
	 * 
	 * @return	the Hadamard gate
	 */
	public static Gate h() {
		return H;
	}

	/**
	 * Returns a universal phase shift gate
	 * with the specified angle.
	 * This gate is also known as the U1 gate.
	 * 
	 * @param angle	the phase shift in radians
	 * @return		the phase shift gate
	 */
	public static Gate p(double angle) {
		return new Gate(row(1, 0), row(0, ei(angle)));
	}

	/**
	 * Returns a Rx gate, for a rotation about the x-axis
	 * with the specified angle.
	 * 
	 * @param angle	the rotation angle in radians
	 * @return		the Rx gate
	 */
	public static Gate rx(double angle) {
		double halfAngle = 0.5 * angle;
		double cos = Math.cos(halfAngle);
		double sin = Math.sin(halfAngle);
		return new Gate(row(cos, i(-sin)), row(i(-sin), cos));
	}

	/**
	 * Returns a Ry gate, for a rotation about the y-axis
	 * with the specified angle.
	 * 
	 * @param angle	the rotation angle in radians
	 * @return		the Ry gate
	 */
	public static Gate ry(double angle) {
		double halfAngle = 0.5 * angle;
		double cos = Math.cos(halfAngle);
		double sin = Math.sin(halfAngle);
		return new Gate(row(cos, -sin), row(sin, cos));
	}

	/**
	 * Returns a Rz gate, for a rotation about the z-axis
	 * with the specified angle.
	 * 
	 * @param angle	the rotation angle in radians
	 * @return		the Rz gate
	 */
	public static Gate rz(double angle) {
		double halfAngle = 0.5 * angle;
		return new Gate(row(ei(-halfAngle), 0), row(0, ei(halfAngle)));
	}

	/**
	 * Returns the phase shift S gate.
	 * <p>
	 * This is equivalent to {@code p(PI/2)},
	 * but is more efficient.
	 * 
	 * @return	the S gate
	 */
	public static Gate s() {
		return S;
	}

	/**
	 * Returns the phase shift S-dagger gate.
	 * <p>
	 * This is equivalent to {@code p(-PI/2)},
	 * but is more efficient.
	 * 
	 * @return	the S-dagger gate
	 */
	public static Gate sdg() {
		return Sdg;
	}

	/**
	 * Returns the SX gate, also known as square-root NOT gate.
	 * 
	 * @return	the SX gate
	 */
	public static Gate sx() {
		return SX;
	}

	/**
	 * Returns the SX-dagger gate, also known as square-root NOT-dagger gate.
	 * 
	 * @return	the SX-dagger gate
	 */
	public static Gate sxdg() {
		return SXdg;
	}

	/**
	 * Returns the phase shift T gate.
	 * <p>
	 * This is equivalent to {@code p(PI/4)}.
	 * 
	 * @return	the T gate
	 */
	public static Gate t() {
		return p(QUARTER_PI);
	}

	/**
	 * Returns the phase shift T-dagger gate.
	 * <p>
	 * This is equivalent to {@code p(-PI/4)}.
	 * 
	 * @return	the T gate
	 */
	public static Gate tdg() {
		return p(-QUARTER_PI);
	}

	/**
	 * Returns a universal gate U with the specified angles.
	 * This gate is also known as the U3 gate.
	 * <p>
	 * This is equivalent to
	 * {@code new Gate(Matrix.product(Gate.p(phi), Gate.ry(theta), Gate.p(lamda)))}.
	 * 
	 * @param theta		the relative polar angle
	 * @param phi		the relative azimuthal angle phi
	 * @param lambda	the relative azimuthal angle lambda
	 * @return			the universal gate
	 */
	public static Gate u(double theta, double phi, double lambda) {
		//	| cos(θ/2)		-sin(θ/2)*e^λi		|
		//	| sin(θ/2)*e^ϕi	 cos(θ/2)*e^(ϕ+λ)i	|
		double halfTheta = 0.5 * theta;
		double cos = Math.cos(halfTheta);
		double sin = Math.sin(halfTheta);
		return new Gate(row(cos, e(-sin, lambda)), row(e(sin, phi), e(cos, phi+lambda)));
	}

}
