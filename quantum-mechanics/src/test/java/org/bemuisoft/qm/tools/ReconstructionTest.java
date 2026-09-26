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
package org.bemuisoft.qm.tools;

import org.bemuisoft.math.complex.EigenDecomposition;
import org.bemuisoft.math.complex.Matrix;
import org.bemuisoft.math.complex.TestMatrix;
import org.bemuisoft.quantum.api.Base;

/**
 * Testing the {@link Reconstruction} class.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class ReconstructionTest implements Base {

	public static void main(String[] args) {
		try {
			ReconstructionTest test = new ReconstructionTest();
			test.runAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void runAll() {
		runDensityMatrixFromEigenDecomposition();
//		log("");
	}

	void runDensityMatrixFromEigenDecomposition() {
		log("runDensityMatrixFromEigenDecomposition");
		Matrix rho1 = TestMatrix.rho1();
		printMatrix(rho1, "Rho1");
		EigenDecomposition ed = EigenDecomposition.get(rho1);
		Matrix rho = Reconstruction.densityMatrix(ed);
		rho = new DensityMatrix(rho);
		printMatrix(rho, "Rho after reconstruction");
		check(rho.isCloseTo(rho1), "Test failed");
	}

	void printMatrix(Matrix m, String description) {
		log(description);
		for (int i = 0; i < m.rows(); i++) {
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < m.columns(); j++) {
				sb.append(m.get(i, j).asString()).append('\t');
			}
			log(sb.toString());
		}
	}

}
