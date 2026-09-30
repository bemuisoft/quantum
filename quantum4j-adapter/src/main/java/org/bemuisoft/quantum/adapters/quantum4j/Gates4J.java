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
package org.bemuisoft.quantum.adapters.quantum4j;

import com.quantum4j.core.gates.StandardGates.CCXGate;
import com.quantum4j.core.gates.StandardGates.CHGate;
import com.quantum4j.core.gates.StandardGates.CNOTGate;
import com.quantum4j.core.gates.StandardGates.CZGate;
import com.quantum4j.core.gates.StandardGates.HGate;
import com.quantum4j.core.gates.StandardGates.ISWAPGate;
import com.quantum4j.core.gates.StandardGates.RXGate;
import com.quantum4j.core.gates.StandardGates.RYGate;
import com.quantum4j.core.gates.StandardGates.RZGate;
import com.quantum4j.core.gates.StandardGates.SGate;
import com.quantum4j.core.gates.StandardGates.SWAPGate;
import com.quantum4j.core.gates.StandardGates.TGate;
import com.quantum4j.core.gates.StandardGates.U1Gate;
import com.quantum4j.core.gates.StandardGates.U2Gate;
import com.quantum4j.core.gates.StandardGates.U3Gate;
import com.quantum4j.core.gates.StandardGates.XGate;
import com.quantum4j.core.gates.StandardGates.YGate;
import com.quantum4j.core.gates.StandardGates.ZGate;

import org.bemuisoft.quantum.adapters.quantum4j.CustomGates4J.CPGate;
import org.bemuisoft.quantum.adapters.quantum4j.CustomGates4J.CUGate;

/**
 * Provides easy access to quantum4j gates.
 * 
 * @author Benno Muilwijk
 */
public class Gates4J {

	//-------------------
	// Single-qubit gates
	//-------------------

	/**
	 * Returns a Hadamard gate.
	 * 
	 * @return the H gate
	 */
	public static HGate h() {
		return new HGate();
	}

	/**
	 * Returns a Pauli-X gate,
	 * also known as the NOT gate.
	 * 
	 * @return the X gate
	 */
	public static XGate x() {
		return new XGate();
	}

	/**
	 * Returns a Pauli-Y gate.
	 * 
	 * @return the Y gate
	 */
	public static YGate y() {
		return new YGate();
	}

	/**
	 * Returns a Pauli-Z gate.
	 * 
	 * @return the Z gate
	 */
	public static ZGate z() {
		return new ZGate();
	}

	/**
	 * Returns an S gate.
	 * 
	 * @return the S gate
	 */
	public static SGate s() {
		return new SGate();
	}

	/**
	 * Returns a T gate.
	 * 
	 * @return the T gate
	 */
	public static TGate t() {
		return new TGate();
	}

	/**
	 * Returns an RX gate
	 * with the specified rotation angle.
	 * 
	 * @param theta	- theta
	 * @return the RX gate
	 */
	public static RXGate rx(double theta) {
		return new RXGate(theta);
	}

	/**
	 * Returns an RY gate
	 * with the specified rotation angle.
	 * 
	 * @param theta	- theta
	 * @return the RY gate
	 */
	public static RYGate ry(double theta) {
		return new RYGate(theta);
	}

	/**
	 * Returns an RZ gate
	 * with the specified rotation angle.
	 * 
	 * @param phi	- phi
	 * @return the RZ gate
	 */
	public static RZGate rz(double phi) {
		return new RZGate(phi);
	}

	/**
	 * Returns a universal phase shift gate
	 * with the specified angle.
	 * 
	 * @param lambda	- lambda
	 * @return the U1 gate
	 */
	public static U1Gate u1(double lambda) {
		return new U1Gate(lambda);
	}

	/**
	 * Returns a universal gate
	 * with the two specified angles.
	 * 
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @return the U2 gate
	 */
	public static U2Gate u2(double phi, double lambda) {
		return new U2Gate(phi, lambda);
	}

	/**
	 * Returns a universal gate
	 * with the three specified angles.
	 * 
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @return the U3 gate
	 */
	public static U3Gate u3(double theta, double phi, double lambda) {
		return new U3Gate(theta, phi, lambda);
	}

	//-----------------
	// Two-qubit gates
	//-----------------

	/**
	 * Returns a controlled Pauli-X gate,
	 * also known as the controlled NOT gate.
	 * 
	 * @return the CNOT gate
	 */
	public static CNOTGate cnot() {
		return new CNOTGate();
	}

	/**
	 * Returns a controlled Pauli-Z gate.
	 * 
	 * @return the CZ gate
	 */
	public static CZGate cz() {
		return new CZGate();
	}

	/**
	 * Returns a controlled Hadamard gate.
	 * 
	 * @return the CH gate
	 */
	public static CHGate ch() {
		return new CHGate();
	}

	/**
	 * Returns a SWAP gate.
	 * 
	 * @return the SWAP gate
	 */
	public static SWAPGate swap() {
		return new SWAPGate();
	}

	/**
	 * Returns an ISWAP gate.
	 * 
	 * @return the ISWAP gate
	 */
	public static ISWAPGate iswap() {
		return new ISWAPGate();
	}

	/**
	 * Returns a controlled phase shift gate
	 * with the specified angle.
	 * 
	 * @param lambda	- lambda
	 * @return the CP gate
	 */
	public static CPGate cp(double lambda) {
		return new CPGate(lambda);
	}

	/**
	 * Returns a controlled universal gate
	 * with the specified angles.
	 * 
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @return the CU gate
	 */
	public static CUGate cu(double theta, double phi, double lambda) {
		return new CUGate(theta, phi, lambda);
	}

	//-------------------
	// Three-qubit gates
	//-------------------

	/**
	 * Returns a controlled controlled Pauli-X gate,
	 * also known as the Toffoli gate.
	 * 
	 * @return the CCX gate
	 */
	public static CCXGate ccx() {
		return new CCXGate();
	}

}
