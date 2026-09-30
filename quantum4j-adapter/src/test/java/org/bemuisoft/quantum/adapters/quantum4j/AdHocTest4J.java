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

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;

import org.bemuisoft.quantum.api.Base;

/**
 * Ad hoc tests for quantum4j.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class AdHocTest4J implements Base {

	public static void main(String[] args) {
		try {
			AdHocTest4J test = new AdHocTest4J();	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void run(int x) {
		hello(x);
//		bell(x);
//		toffoli(x);
//		qasm();
	}

	void hello(int x) {
		QuantumCircuit qc = QuantumCircuit.create(2)
				.h(0)
				.measureAll();
		Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(x));
		log("" + r.getCounts());
	}

	void bell(int x) {
		QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(x));
        log("" + r.getCounts());
	}

	void toffoli(int x) {
		QuantumCircuit qc = QuantumCircuit.create(3)
			    .x(0)
			    .x(1)
			    .ccx(0, 1, 2)
			    .measureAll();

		Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(x));
		log("" + r.getCounts());
	}

	void qasm() {
		QuantumCircuit qc = QuantumCircuit.create(2)
			    .h(0)
			    .cx(0, 1)
			    .measureAll();

		String qasm = QasmExporter.toQasm(qc);
		log(qasm);
	}

}
