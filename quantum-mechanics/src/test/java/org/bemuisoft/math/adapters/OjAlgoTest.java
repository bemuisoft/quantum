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
package org.bemuisoft.math.adapters;

import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.EigenTestBase;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.math.complex.TestMatrix;

/**
 * Testing eigen decomposition by the ojAlgo package.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class OjAlgoTest extends EigenTestBase {

	public static void main(String[] args) {
		try {
			OjAlgoTest test = new OjAlgoTest();
			test.runAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runAll() {
		testRhoA();
		testRhoAB();
	}

	void testRhoA() {
		Matrix rhoA = TestMatrix.rho1();
		EigenDecomposition eigA = OjAlgo.eigenDecomposition(rhoA);
		
		log("eigenvalue 0 = " + asString(eigA.getEigenvalue(0)));
		log("eigenvalue 1 = " + asString(eigA.getEigenvalue(1)));
		log("eigenvector 0 = " + asString(eigA.getEigenvector(0)));
		log("eigenvector 1 = " + asString(eigA.getEigenvector(1)));
		
		verify(rhoA, eigA);
	}

	void testRhoAB() {
		Matrix rhoAB = TestMatrix.rhoAB();
		EigenDecomposition eigAB = OjAlgo.eigenDecomposition(rhoAB);
		
		log("eigenvalue 0 = " + asString(eigAB.getEigenvalue(0)));
		log("eigenvalue 1 = " + asString(eigAB.getEigenvalue(1)));
		log("eigenvalue 2 = " + asString(eigAB.getEigenvalue(2)));
		log("eigenvalue 3 = " + asString(eigAB.getEigenvalue(3)));
		log("eigenvector 0 = " + asString(eigAB.getEigenvector(0)));
		log("eigenvector 1 = " + asString(eigAB.getEigenvector(1)));
		log("eigenvector 2 = " + asString(eigAB.getEigenvector(2)));
		log("eigenvector 3 = " + asString(eigAB.getEigenvector(3)));
		
		verify(rhoAB, eigAB);
	}

}
