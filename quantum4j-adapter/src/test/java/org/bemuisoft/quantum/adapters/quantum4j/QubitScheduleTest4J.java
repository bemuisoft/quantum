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
 * Tests {@link QubitSchedule4J}.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class QubitScheduleTest4J implements Base {

	public static void main(String[] args) {
		try {
			QubitScheduleTest4J test = new QubitScheduleTest4J();	// test with n qubits
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void run(int shots) {
		qasm(shots);
	}

	void qasm(int shots) {
		QuantumCircuit qc = QuantumCircuit.create(2);
		QubitSchedule4J qa = new QubitSchedule4J(qc, 'A');
		QubitSchedule4J qb = new QubitSchedule4J(qc, 'B');
		
		qa.ry(PI/3);
		qb.cx(qa);
		
		String qasm = QasmExporter.toQasm(qc);
		log(qasm);
		log(qc.drawAscii());
		
		Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(shots));
		log("" + r.getCounts());
	}

}
