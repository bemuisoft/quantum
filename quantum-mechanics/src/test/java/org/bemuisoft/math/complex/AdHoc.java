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

import org.bemuisoft.quantum.api.Base;
import org.ojalgo.matrix.decomposition.Eigenvalue;
import org.ojalgo.matrix.store.GenericStore;
import org.ojalgo.scalar.ComplexNumber;

/**
 * Ad hoc tests for complex math.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class AdHoc implements Base {

	public static void main(String[] args) {
		try {
			AdHoc test = new AdHoc();
			test.ojAlgoTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void ojAlgoTest() {
		GenericStore<ComplexNumber> matrix = GenericStore.C128.make(2, 2);
		matrix.set(0, 0, ComplexNumber.of(0.7, 0.0));
		matrix.set(1, 1, ComplexNumber.of(0.3, 0.0));
		matrix.set(0, 1, ComplexNumber.of(0.2, 0.1));
		matrix.set(1, 0, ComplexNumber.of(0.2, -0.1));
		
		boolean hermitian = true;
		Eigenvalue<ComplexNumber> evd = Eigenvalue.C128.make(hermitian);
		evd.decompose(matrix);
		
		log("eigenvalue 0 = " + round(evd.getEigenpair(0).value.getReal()));
		log("eigenvalue 0 = " + round(evd.getEigenvalues().get(0).getReal()));
		
		log("eigenvalue 1 = " + round(evd.getEigenpair(1).value.getReal()));
		log("eigenvalue 1 = " + round(evd.getEigenvalues().get(1).getReal()));
		
		log("eigenvector 0, row 0.im = " + evd.getEigenpair(0).vector.get(0).getImaginary());
		log("eigenvector 0, row 0.im = " + evd.getV().get(0, 0).getImaginary());
		
		log("eigenvector 0, row 1.im = " + evd.getEigenpair(0).vector.get(1).getImaginary());
		log("eigenvector 0, row 1.im = " + evd.getV().get(1, 0).getImaginary());				// eigenvector 0 is in column 0 of V
		
		log("eigenvector 1, row 0.im = " + evd.getEigenpair(1).vector.get(0).getImaginary());
		log("eigenvector 1, row 0.im = " + evd.getV().get(0, 1).getImaginary());				// eigenvector 1 is in column 1 of V
		
		log("eigenvector 1, row 1.im = " + evd.getEigenpair(1).vector.get(1).getImaginary());
		log("eigenvector 1, row 1.im = " + evd.getV().get(1, 1).getImaginary());
	}

}
