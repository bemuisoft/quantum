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

import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.core.gates.TwoQubitGate;

/**
 * Defines some gates that are not available
 * in {@link StandardGates}.
 * 
 * @author Benno Muilwijk
 */
public class CustomGates4J {

	// ----------------------------------------------------------------------
	// 2-qubit gates
	// ----------------------------------------------------------------------

	/**
	 * Controlled phase shift gate.
	 */
	public static final class CPGate extends TwoQubitGate {
		private final double phi;

		/**
		 * Constructs a controlled phase shift gate
		 * with the specified angle.
		 * 
		 * @param phi	- phase shift angle
		 */
		public CPGate(double phi) {
			super(CustomGateMatrices4J.CP(phi));
			this.phi = phi;
		}

		/**
		 * Returns phi.
		 * 
		 * @return phi
		 */
		public double getPhi() {
			return phi;
		}

		@Override
		public String name() {
			return "cp";
		}
	}

	/**
	 * Controlled universal gate.
	 */
	public static final class CUGate extends TwoQubitGate {
		private final double theta;
		private final double phi;
		private final double lambda;

		/**
		 * Constructs a controlled universal gate
		 * with the specified angles.
		 * 
		 * @param theta		- theta
		 * @param phi		- phi
		 * @param lambda	- lambda
		 */
		public CUGate(double theta, double phi, double lambda) {
			super(CustomGateMatrices4J.CU(theta, phi, lambda));
			this.theta = theta;
			this.phi = phi;
			this.lambda = lambda;
		}

		/**
		 * Returns theta.
		 * 
		 * @return theta
		 */
		public double getTheta() {
			return theta;
		}

		/**
		 * Returns phi.
		 * 
		 * @return phi
		 */
		public double getPhi() {
			return phi;
		}

		/**
		 * Returns lambda.
		 * 
		 * @return lambda
		 */
		public double getLambda() {
			return lambda;
		}

		@Override
		public String name() {
			return "cu";
		}
	}

}
