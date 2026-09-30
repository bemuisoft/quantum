/*
Copyright 2026 Benno Muilwijk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.bemuisoft.qm.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.bemuisoft.math.complex.ComplexBase;
import org.bemuisoft.math.complex.DiagonalMatrix;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.quantum.api.IQuantumState;

/**
 * A quantum system can be pure or mixed.
 * In both cases its state can be represented
 * by a density matrix, usually referred to
 * by the Greek letter rho.
 * <p>
 * Subsystems (with less qubits, maybe even one)
 * are also quantum systems in their own right.
 * Their states can be represented by a
 * reduced density matrix, which can be
 * derived from the density matrix of the
 * parent system, by tracing out information
 * about qubits outside the subsystem.
 * 
 * @author Benno Muilwijk
 */
public class QuantumSystem extends ComplexBase {

	private final int qubits;						// number of qubits
	private final boolean rightToLeft;				// direction of qubit label assignment
	private final QuantumSystem parentSystem;		// if not null, this is a subsystem of parentSystem
	private final List<Integer> qIndexList;			// qIndexList.get(thisQubitIndex) == parentQubitIndex
	private final DiagonalMatrix identity;			// identity matrix
	private Matrix rho;								// density matrix
	
	/** Modification count, is incremented with each state change.  */
	protected int modCount;
	/** Modification count of last reset, is updated only by reset. */
	protected int modCount0;

	/**
	 * Constructs a quantum system for
	 * the specified number of qubits.
	 * <p>
	 * The qubits are labeled A, B, ...
	 * from left to right.
	 * <p>
	 * The quantum state is initialized to |0⟩.
	 * 
	 * @param qubits	the number of qubits
	 */
	public QuantumSystem(int qubits) {
		this(qubits, false);
	}

	/**
	 * Constructs a quantum state for
	 * the specified number of qubits.
	 * <p>
	 * The qubits are labeled A, B, ...
	 * from left to right or from right to left,
	 * as specified.
	 * <p>
	 * The quantum state is initialized to |0⟩.
	 * 
	 * @param qubits		the number of qubits
	 * @param rightToLeft	{@code true} for right-to-left labels,
	 * 						{@code false} for left-to-right labels
	 */
	public QuantumSystem(int qubits, boolean rightToLeft) {
		this(qubits, rightToLeft, null, null);
		setRho(initialState());
	}

	/**
	 * Constructs a quantum state for the
	 * subsystem of the specified parent system,
	 * including only the specified qubit(s).
	 * 
	 * @param parent	the parent system
	 * @param qIndex	the qubit index or indices to include
	 * @see				#subsystem(Integer...)
	 */
	public QuantumSystem(QuantumSystem parent, Integer... qIndex) {
		this(qIndex.length, parent.isRightToLeft(), parent, qIndex);
		setRho(parent.reducedDensityMatrix(qIndex));
	}

	/**
	 * Constructs a quantum state for
	 * the specified number of qubits.
	 * <p>
	 * The qubits are labeled A, B, ...
	 * from left to right or from right to left,
	 * as specified.
	 * <p>
	 * The quantum state is initialized to rho.
	 * 
	 * @param qubits		the number of qubits
	 * @param rightToLeft	{@code true} for right-to-left labels,
	 * 						{@code false} for left-to-right labels
	 * @param rho			the initial state as density matrix, or null
	 * @see					#setRho(Matrix)
	 */
	protected QuantumSystem(int qubits, boolean rightToLeft, Matrix rho) {
		this(qubits, rightToLeft, null, null);
		setRho(rho);
	}

	//
	// private constructor - initializes final instance variables, so not rho
	//
	private QuantumSystem(int n, boolean rtl, QuantumSystem parent, Integer[] qIndex) {
		qubits = n;
		rightToLeft = rtl;
		parentSystem = parent;
		if (parent == null)
			qIndexList = null;
		else {
			qIndexList = Arrays.asList(qIndex);
			qIndexList.sort(null);
		}
		identity = Matrix.identity(1 << qubits).setFinal();		// 1 << n == 1 * Math.pow(2, n) == 2^n
	}

	private Matrix initialState() {
		// return a density matrix that represents quantum state |0⟩
		// that is, with all qubits set to |0⟩
		Matrix initialState = Matrix.zero(size());
		initialState.set(0, 0, ONE);
		return initialState;
	}

	/**
	 * Returns the number of qubits in this quantum system.
	 * 
	 * @return	the number of qubits
	 */
	public final int qubits() {
		return qubits;
	}

	/**
	 * Returns the size of the density matrix.
	 * <p>
	 * This is the number of rows and columns
	 * in the density matrix.
	 * 
	 * @return	the size of the density matrix.
	 */
	public final int size() {
		// the identity matrix has the same size as the density matrix and is never null
		return identity.rows();
	}

	/**
	 * Returns this (sub)system's parent system,
	 * if it has one.
	 * 
	 * @return	the parent system, or null
	 * @see		#getRootSystem()
	 */
	public final QuantumSystem getParentSystem() {
		return parentSystem;
	}

	/**
	 * Returns this (sub)system's root system.
	 * <p>
	 * A root system in this context is a system
	 * that is not a subsystem of another system.
	 * If this system is a subsystem, the chain
	 * of parent systems is followed until a parent
	 * system without further parent is found.
	 * 
	 * @return	the root system
	 * @see		#getParentSystem()
	 */
	public final QuantumSystem getRootSystem() {
		QuantumSystem qs = this;
		while (qs.parentSystem != null) {
			qs = qs.parentSystem;
		}
		return qs;
	}

	/**
	 * Returns a boolean value which indicates if qubit labels
	 * are assigned from left to right or from right to left.
	 * 
	 * @return	{@code true} for right to left,
	 * 			{@code false} for left to right
	 * @see		IQuantumState#isRightToLeft()	
	 */
	public final boolean isRightToLeft() {
		return rightToLeft;
	}

	/**
	 * Returns the index for a qubit identified by a capital.
	 * <p>
	 * 'A' -> 0, 'B' -> 1, etc.
	 * <p>
	 * This is reversed for right-to-left labeling: 'A' -> highest index.
	 * 
	 * @param q		the qubit's character (short label) 
	 * @return		the qubit index
	 */
	public final int qubitIndex(char q) {
		if (parentSystem != null) {
			return qIndexList.indexOf(parentSystem.qubitIndex(q));
		}
		// qubit index is left to right, i.e. qubit with index 0 is leftmost (most significant) bit
		if (rightToLeft) {
			// qubit A is rightmost (least significant) bit
			return qubits - (q - '@');
		}
		// qubit A is leftmost (most significant) bit
		return q - 'A';
	}

	/**
	 * Returns the identifying character for the qubit at the specified index.
	 * <p>
	 * 0 -> 'A', 1 -> 'B', etc.
	 * <p>
	 * This is reversed for right-to-left labeling: highest index -> 'A'.
	 * 
	 * @param qIndex	the qubit index 
	 * @return			the qubit's character (short label)
	 */
	public final char qubitChar(int qIndex) {
		if (parentSystem != null) {
			return parentSystem.qubitChar(qIndexList.get(qIndex));
		}
		if (rightToLeft) {
			// qubit A is rightmost (least significant) bit
			return (char) ('@' + qubits - qIndex);
		}
		// qubit A is leftmost (most significant) bit
		return (char) ('A' + qIndex);
	}

	/**
	 * Returns the qubit index for this system,
	 * given the qubit index for the root system.
	 * <p>
	 * If the specified qubit in the root system
	 * is not included in this subsystem,
	 * this method returns -1.
	 * 
	 * @param rootIndex	qubit index in root system
	 * @return			qubit index in this system, or -1
	 * @see				#getRootSystem()
	 * @see				#indexOf(int, QuantumSystem)
	 */
	public final int indexOf(int rootIndex) {
		if (parentSystem == null) {
			return rootIndex;
		}
		return qIndexList.indexOf(parentSystem.indexOf(rootIndex));
	}

	/**
	 * Returns the qubit index for this system,
	 * given the qubit index for another system,
	 * that has the same root system as this system.
	 * <p>
	 * If the specified qubit of the other system
	 * is not included in this system,
	 * this method returns -1.
	 * 
	 * @param qIndex	qubit index in other system
	 * @param system	the other system
	 * @return			qubit index in this system, or -1
	 * @see				#getRootSystem()
	 * @see				#indexOf(int)
	 */
	public final int indexOf(final int qIndex, final QuantumSystem system) {
		// try to resolve trivial cases
		if (system == null) {
			return indexOf(qIndex);
		}
		if (system == this) {
			return qIndex;
		}
		if (system == parentSystem) {
			return qIndexList.indexOf(qIndex);
		}
		
		// try to resolve in case argument system is a subsystem of this system
		int qi = qIndex;
		QuantumSystem qs = system;
		while (qs.parentSystem != null) {
			qs = qs.parentSystem;
			qi = qIndexList.get(qi);
			if (qs == this) {
				return qi;
			}
		}
		// found argument system's root system
		QuantumSystem rootSystem = qs;
		int rootIndex = qi;
		
		// try to resolve in case this system is a subsystem of argument system
		qs = this;
		while (qs.parentSystem != null) {
			qs = qs.parentSystem;
			qi = qIndexList.get(qi);
			if (qs == system) {
				return qi;
			}
		}
		// found this system's root system
		if (qs == rootSystem) {
			return indexOf(rootIndex);
		}
		// different root systems
		return -1;
	}

	/**
	 * Checks if the given qubit index is valid for this quantum system
	 * and throws an exception if it is not valid.
	 * 
	 * @param qIndex	the qubit index to check
	 * @throws IndexOutOfBoundsException	if the qubit index is invalid
	 */
	protected void checkQubitIndex(int qIndex) throws IndexOutOfBoundsException {
		if (qIndex < 0 || qIndex >= qubits) {
			throw new IndexOutOfBoundsException("Incorrect qubit index " + qIndex);
		}
	}

	/**
	 * Checks if all given qubit indices are valid for this quantum system
	 * and throws an exception if any is not valid.
	 * 
	 * @param qIndex	the array of qubit indices to check
	 * @throws IndexOutOfBoundsException	if the qubit index is invalid
	 */
	private void checkQubitIndex(Integer[] qIndex) throws IndexOutOfBoundsException {
		for (Integer qIdx : qIndex) {
			checkQubitIndex(qIdx.intValue());
		}
	}

	/**
	 * Returns the current state identifier.
	 * <p>
	 * If the returned value is the same as
	 * from a previous invocation, that means
	 * the quantum state has not changed
	 * in between those two invocations.
	 * Otherwise, it has (possibly) changed.
	 * 
	 * @return	the current state identifier
	 */
	public int stateIdentifier() {
		return modCount;
	}

	/**
	 * Returns the density matrix of the current state.
	 * 
	 * @return	the current density matrix
	 */
	public Matrix densityMatrix() {
		return rho.setFinal();
	}

	/**
	 * Returns the density matrix of the current state,
	 * which can be {@code null}.
	 * The subclass must handle this situation.
	 * 
	 * @return	the density matrix, or null
	 */
	protected Matrix getRho() {
		return rho;
	}

	/**
	 * Sets the density matrix for this system unverified,
	 * so it can be null.
	 * The subclass must handle this situation.
	 * 
	 * @param rho	the density matrix, or null
	 */
	protected void setRho(Matrix rho) {
		this.rho = rho;
		modCount++;
	}

	/**
	 * Sets the density matrix for this system.
	 * 
	 * @param rho	the density matrix to set
	 */
	public void setDensityMatrix(Matrix rho) {
		check(rho.rows() == this.size() && rho.isHermitian() && rho.trace().isCloseTo(ONE), "Invalid density matrix.");
		setRho(rho.setFinal());
	}

	/**
	 * Resets this quantum system to initial state |0⟩.
	 * 
	 * @return	this quantum system
	 */
	public QuantumSystem reset() {
		if (modCount != modCount0) {
			setRho(initialState());
			modCount0 = modCount;
		}
		return this;
	}

	/**
	 * Answers whether this quantum system is (almost) pure.
	 * <p>
	 * A quantum system is pure if the trace of the square of
	 * its density matrix is equal to one.
	 * This method allows for rounding errors and returns
	 * {@code true} if that trace is equal or close to one.
	 * 
	 * @return	{@code true} if this system is (almost) pure,
	 * 			{@code false} otherwise
	 */
	public boolean isPure() {
		return isPure(this.rho);
	}

	/**
	 * Answers whether the trace of the square of the
	 * given density matrix is equal or close to one.
	 * 
	 * @param rho	the density matrix to check
	 * @return		{@code true} if the trace of rho² is close to one,
	 * 				{@code false} otherwise
	 */
	protected boolean isPure(Matrix rho) {
		return Matrix.product(rho, rho).trace().isCloseTo(ONE);
	}

	/**
	 * Answers whether the given matrix is a valid operation matrix
	 * for this quantum system.
	 * 
	 * @param operation	the matrix to validate
	 * @return			{@code true} if it is a valid operation matrix,
	 * 					{@code false} otherwise
	 */
	public boolean isValidOperation(Matrix operation) {
		return operation.rows() == this.size()
			&& operation.isSquare()
			&& Matrix.product(operation.transpose(true), operation).isCloseTo(identity);	// unitary check
	}

	private void applyInternal(Matrix operation) {
		// apply directly to density matrix
		setRho(Matrix.product(operation, rho, operation.transpose(true)));
	}

	/**
	 * Applies the given operation to the current state.
	 * 
	 * @param operation	the operation matrix
	 * @return			this quantum system
	 * @throws			IllegalArgumentException if the operation matrix is not valid
	 */
	public QuantumSystem apply(Matrix operation) {
		check(isValidOperation(operation), "Invalid operation matrix.");
		applyInternal(operation);
		return this;
	}

	/**
	 * Applies the given gate to the specified qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param qIndex	the target qubit index
	 * @return			this quantum system
	 */
	public QuantumSystem apply(Gate gate, int qIndex) {
		applyInternal(operation(gate, qIndex));
		return this;
	}

	/**
	 * Applies the given gate to the specified qubit
	 * with one control qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param cIndex	the control qubit index
	 * @param qIndex	the target qubit index
	 * @return			this quantum system
	 */
	public QuantumSystem apply(Gate gate, int cIndex, int qIndex) {
		applyInternal(operation(gate, cIndex, qIndex));
		return this;
	}

	/**
	 * Applies the given gate to the specified qubit
	 * with more than one control qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param cIndices	a collection of control qubit indices
	 * @param qIndex	the target qubit index
	 * @return			this quantum system
	 */
	public QuantumSystem apply(Gate gate, Collection<Integer> cIndices, int qIndex) {
		applyInternal(operation(gate, cIndices, qIndex));
		return this;
	}

	/**
	 * Returns an operation matrix to apply the
	 * given gate to the specified qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param qIndex	the target qubit index
	 * @return			the operation matrix
	 */
	public Matrix operation(Gate gate, int qIndex) {
		checkQubitIndex(qIndex);
		Matrix oper = initial;
		for (int q = 0; q < qubits; q++) {
			oper = Matrix.tensor(oper, (q == qIndex) ? gate : Gate.I);
		}
		return oper;
	}

	/**
	 * Returns an operation matrix to apply the
	 * given gate to the specified qubit
	 * with one control qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param cIndex	the control qubit index
	 * @param qIndex	the target qubit index
	 * @return			the operation matrix
	 */
	public Matrix operation(Gate gate, int cIndex, int qIndex) {
		checkQubitIndex(cIndex);
		checkQubitIndex(qIndex);
		Matrix gateMinusI = gate.copy().subtract(Gate.I);
		Matrix oper = initial;
		for (int q = 0; q < qubits; q++) {
			if (q == qIndex) {
				// target qubit
				oper = Matrix.tensor(oper, gateMinusI);
			} else {
				oper = Matrix.tensor(oper, (q == cIndex) ? rho1 : Gate.I);
			}
		}
		return oper.add(identity);
	}

	/**
	 * Returns an operation matrix to apply the
	 * given gate to the specified qubit
	 * with more than one control qubit.
	 * 
	 * @param gate		the gate to apply
	 * @param cIndices	a collection of control qubit indices
	 * @param qIndex	the target qubit index
	 * @return			the operation matrix
	 */
	public Matrix operation(Gate gate, Collection<Integer> cIndices, int qIndex) {
		cIndices.forEach(cIndex -> checkQubitIndex(cIndex));
		checkQubitIndex(qIndex);
		Matrix gateMinusI = gate.copy().subtract(Gate.I);
		Matrix oper = initial;
		for (int q = 0; q < qubits; q++) {
			if (q == qIndex) {
				// target qubit
				oper = Matrix.tensor(oper, gateMinusI);
			} else {
				oper = Matrix.tensor(oper, (cIndices.contains(q)) ? rho1 : Gate.I);
			}
		}
		return oper.add(identity);
	}

	/**
	 * Returns the reduced density matrix for
	 * a subsystem of the specified qubit(s).
	 * 
	 * @param qIndex	the qubit index or indices to include
	 * @return			the reduced density matrix
	 */
	public Matrix reducedDensityMatrix(Integer... qIndex) {
		// caller has to ensure that qIndex does not contain duplicate entries
		int size = 1 << qIndex.length;
		Matrix rdm = Matrix.zero(size);
		// add each rdm component to the rdm
		buildComponents(qIndex, comp -> rdm.add(comp));
		return rdm;
	}

	/**
	 * Returns a list of reduced density matrix components for
	 * a subsystem of the specified qubit(s).
	 * <p>
	 * The sum of these components gives the reduced density matrix.
	 * 
	 * @param qIndex	the qubit index or indices to include
	 * @return			the list of reduced density matrix components
	 */
	public List<Matrix> reducedDensityMatrixComponents(Integer... qIndex) {
		// caller has to ensure that qIndex does not contain duplicate entries
		ArrayList<Matrix> rdmComponents = new ArrayList<>(size() >> qIndex.length);		// x >> n == x / 2^n
		// add each rdm component to the list
		buildComponents(qIndex, comp -> rdmComponents.add(comp));
		return rdmComponents;
	}

	/**
	 * Performs the consumer operation on each
	 * reduced density matrix component for
	 * a subsystem of the specified qubit(s).
	 * 
	 * @param qIndex	the qubit index or indices to include
	 * @param consumer	the consumer operation
	 */
	public void reducedDensityMatrixComponents(Integer[] qIndex, Consumer<Matrix> consumer) {
		// caller has to ensure that qIndex does not contain duplicate entries
		// perform the consumer operation on each rdm component
		buildComponents(qIndex, consumer);
	}

	/**
	 * Returns the Bloch vector for the specified qubit.
	 * 
	 * @param qIndex	the qubit index
	 * @return			the Bloch vector
	 */
	public SimpleBlochVector blochVector(int qIndex) {
		SimpleBlochVector bloch = new SimpleBlochVector(String.valueOf(qubitChar(qIndex)));
		// add each Bloch vector component to the Bloch vector
		blochVectorComponents(qIndex, comp -> bloch.add(comp));
		return bloch;
	}

	/**
	 * Returns a list of Bloch vector components for
	 * the specified qubit.
	 * <p>
	 * The sum of these components gives the Bloch vector.
	 * 
	 * @param qIndex	the qubit index
	 * @return			the list of Bloch vector components
	 */
	public List<SimpleBlochVector> blochVectorComponents(int qIndex) {
		ArrayList<SimpleBlochVector> blochComponents = new ArrayList<>(size()/2);
		// add each Bloch vector component to the list
		blochVectorComponents(qIndex, comp -> blochComponents.add(comp));
		return blochComponents;
	}

	/**
	 * Performs the consumer operation on each
	 * Bloch vector component for
	 * the specified qubit.
	 * 
	 * @param qIndex	the qubit index
	 * @param consumer	the consumer operation
	 */
	public void blochVectorComponents(int qIndex, Consumer<SimpleBlochVector> consumer) {
		// perform the consumer operation on each Bloch vector component
		buildBlochComponents(consumer, qIndex);
	}

	private void buildBlochComponents(Consumer<SimpleBlochVector> consumer, Integer... qIndex) {
		// convert each rdm component to a Bloch vector component
		// and perform the consumer operation on that
		buildComponents(qIndex, rdm -> consumer.accept(new SimpleBlochVector(rdm)));
	}

	private synchronized void buildComponents(Integer[] qIndex, Consumer<Matrix> consumer) {
		checkQubitIndex(qIndex);
		// calculate each reduced density matrix component using
		// tensor product of alternating combinations of bra and kets,
		// with identity matrix I at the specified qIndex
		// (⟨0|⊗...⊗I⊗...⊗⟨0|)rho(|0⟩⊗...⊗I⊗...⊗|0⟩)
		// ...
		// (⟨1|⊗...⊗I⊗...⊗⟨1|)rho(|1⟩⊗...⊗I⊗...⊗|1⟩)
		build(initial, initial, -1, Arrays.asList(qIndex), consumer);
	}

	private void build(final Matrix left, final Matrix right, int q, List<Integer> qIndex, Consumer<Matrix> consumer) {
		// ensure that rho is initialized (possibly by subclass!)
		final Matrix rho = densityMatrix();
		if (right.rows() < rho.rows()) {
			// keep building left and right
			q += 1;
			if (qIndex.contains(q)) {
				build(Matrix.tensor(left, Gate.I), Matrix.tensor(right, Gate.I), q, qIndex, consumer);
			} else {
				// split the tree recursively, giving 2^n components (n = qubits - qIndex.length) 
				build(Matrix.tensor(left, bra0), Matrix.tensor(right, ket0), q, qIndex, consumer);
				build(Matrix.tensor(left, bra1), Matrix.tensor(right, ket1), q, qIndex, consumer);
			}
		} else {
			// calculate reduced density matrix component and perform the consumer operation on it
			Matrix component = Matrix.product(left, rho, right);
			consumer.accept(component);
		}
	}

	/**
	 * Returns the probability of a specific combination of measurement
	 * outcomes to come true, if all qubits are to be measured now.
	 * 
	 * @param index	state dimension index (identifies the outcomes)
	 * @return		the probability of the specified outcomes
	 * @see			IQuantumState#getProbability(int)
	 */
	public double getProbability(int index) {
		return getRho().get(index, index).realPart();
	}

	/**
	 * Returns the probability of measuring the
	 * specified qubit as |0⟩ along the Z axis.
	 * 
	 * @param qIndex	the qubit index
	 * @return			the probability of measuring |0⟩
	 */
	public double getProbability0(int qIndex) {
		return 1.0 - getProbability1(qIndex);
	}

	/**
	 * Returns the probability of measuring the
	 * specified qubit as |1⟩ along the Z axis.
	 * 
	 * @param qIndex	the qubit index
	 * @return			the probability of measuring |1⟩
	 */
	public double getProbability1(int qIndex) {
		checkQubitIndex(qIndex);
		int qMask = size() >> (qIndex + 1);
		
		double p1 = 0.0;
		for (int i = 0; i < size(); i++) {
			if ((i & qMask) == qMask) {
				p1 += getProbability(i);
			}
		}
		return p1;
	}

	/**
	 * Returns a subsystem of this quantum system,
	 * including only the specified qubit(s).
	 * <p>
	 * State changes in this quantum system are <b>not</b>
	 * automatically reflected in the subsystem, or vice versa.
	 * Use {@link #refreshFromParent()} to refresh the
	 * subsystem's state from its parent when needed.
	 * 
	 * @param qIndex	the qubit index or indices to include
	 * @return			the subsystem
	 */
	public QuantumSystem subsystem(Integer... qIndex) {
		return new QuantumSystem(this, qIndex);
	}

	/**
	 * Refreshes the state of this (sub)system from its parent,
	 * if it has one.
	 * Nothing happens if this system has no parent.
	 * <p>
	 * Use {@code setDensityMatrix(qs.reducedDensityMatrix(qIndex...))}
	 * to refresh from a different system ({@code qs}.
	 * 
	 * @return	this quantum system
	 * @see		#setDensityMatrix(Matrix)
	 */
	public QuantumSystem refreshFromParent() {
		if (parentSystem != null) {
			setRho(parentSystem.reducedDensityMatrix(qIndexList.toArray(new Integer[0])));
		}
		return this;
	}

	/** 1x1 matrix (1) */
	protected static final Matrix initial = new Matrix(row(1)); 
	/** 1x2 matrix (1 0) = ⟨0| */
	protected static final Matrix bra0 = new Matrix(row(1, 0)); 
	/** 1x2 matrix (0 1) = ⟨1| */
	protected static final Matrix bra1 = new Matrix(row(0, 1)); 
	/** 2x1 matrix (1,0) = |0⟩ */
	protected static final Matrix ket0 = new Matrix(row(1), row(0)); 
	/** 2x1 matrix (0,1) = |1⟩ */
	protected static final Matrix ket1 = new Matrix(row(0), row(1)); 
	/** 2x2 matrix (1 0,0 0) = |0⟩⟨0| */
	protected static final Matrix rho0 = Matrix.product(ket0, bra0); 
	/** 2x2 matrix (0 0,0 1) = |1⟩⟨1| */
	protected static final Matrix rho1 = Matrix.product(ket1, bra1); 

}
