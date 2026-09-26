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
package org.bemuisoft.math.complex;

/**
 * Testing eigen decomposition.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class EigenTest extends EigenTestBase {

	public static void main(String[] args) {
		try {
			EigenTest test = new EigenTest();
			test.runAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runAll() {
		testRho1();
		log("");
		testRho2();
	}

	void testRho1() {
		log("testRho1");
		Matrix rho = TestMatrix.rho1();
		EigenDecomposition ed = new EigenDecomposition(rho);
		
		log("eigenvalue 0 = " + asString(ed.getEigenvalue(0)));
		log("eigenvalue 1 = " + asString(ed.getEigenvalue(1)));
		log("eigenvector 0 = " + asString(ed.getEigenvector(0)));
		log("eigenvector 1 = " + asString(ed.getEigenvector(1)));
		
		verify(rho, ed);
//		Matrix.product(ed.getEigenvectors(), ed.getEigenvectors().transpose());		// should give identity matrix
//		reconstruct(ed);															// should give rho
	}

	void testRho2() {
		log("testRho2");
		Matrix rho = TestMatrix.rho1().multiply(2.0);								// rho is still hermitian, but trace is 2 instead of 1
		EigenDecomposition ed = new EigenDecomposition(rho);
		
		log("eigenvalue 0 = " + asString(ed.getEigenvalue(0)));
		log("eigenvalue 1 = " + asString(ed.getEigenvalue(1)));
		log("eigenvector 0 = " + asString(ed.getEigenvector(0)));
		log("eigenvector 1 = " + asString(ed.getEigenvector(1)));
		
		verify(rho, ed);
//		Matrix.product(ed.getEigenvectors(), ed.getEigenvectors().transpose());		// should give identity matrix
//		reconstruct(ed);															// should give rho
	}

}
