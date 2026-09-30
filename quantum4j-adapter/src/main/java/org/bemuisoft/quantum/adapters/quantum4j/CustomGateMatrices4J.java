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

import com.quantum4j.core.gates.GateMatrices;
import com.quantum4j.core.math.Complex;

/**
 * Defines some gate matrices that are not available
 * in {@link GateMatrices}.
 * 
 * @author Benno Muilwijk
 */
public class CustomGateMatrices4J {

	// ----------------------------------------------------------------------
	// PARAMETERIZED 2-QUBIT GATES (4x4)
	// ----------------------------------------------------------------------
	//
	// TwoQubitGate uses indexing where the *first qubit* argument
	// is the LESS significant bit (LSB) in basis ordering.
	//
	// Basis order inside apply():
	// |00⟩, |01⟩, |10⟩, |11⟩
	// where (control = q0), (target = q1).
	//
	// ----------------------------------------------------------------------

	/**
	 * Returns a controlled phase shift matrix
	 * with the specified angle.
	 * <pre>
     * [	1,	0,	0,	0		]
     * [	0,	1,	0,	0		]
     * [	0,	0,	1,	0		]
     * [	0,	0,	0,	e^iϕ	]
	 * </pre>
	 * 
	 * @param phi	- phase shift angle
	 * @return the CP matrix
	 */
	public static Complex[][] CP(double phi) {
		return new Complex[][] {
				{ c(1), c(0), c(0), c(0) },
				{ c(0), c(1), c(0), c(0) },
				{ c(0), c(0), c(1), c(0) },
				{ c(0), c(0), c(0), ei(phi) }
		};
	}

	/**
	 * Returns a controlled universal matrix
	 * with the specified angles.
	 * <pre>
     * [	1,	0,	0,				 0					]
     * [	0,	1,	0,				 0					]
     * [	0,	0,	cos(θ/2),		-sin(θ/2) e^iλ		]
     * [	0,	0,	sin(θ/2) e^iϕ,	 cos(θ/2) e^i(ϕ+λ)	]
	 * </pre>
	 * 
	 * @param theta		- theta
	 * @param phi		- phi
	 * @param lambda	- lambda
	 * @return the CU matrix
	 */
	public static Complex[][] CU(double theta, double phi, double lambda) {
		double c = Math.cos(theta / 2.0);
		double s = Math.sin(theta / 2.0);
		return new Complex[][] {
				{ c(1), c(0), c(0),		 c(0) },
				{ c(0), c(1), c(0),		 c(0) },
				{ c(0), c(0), c(c),		 e(-s, lambda) },
				{ c(0), c(0), e(s, phi), e(c, phi+lambda) }
		};
	}

	// ----------------------------------------------------------------------
	// Helpers
	// ----------------------------------------------------------------------

	private static Complex c(double r) {
		return new Complex(r, 0);
	}

    private static Complex e(double r, double phase) {
        return new Complex(r*Math.cos(phase), r*Math.sin(phase));
	}

    private static Complex ei(double phase) {
        return new Complex(Math.cos(phase), Math.sin(phase));
    }

}
