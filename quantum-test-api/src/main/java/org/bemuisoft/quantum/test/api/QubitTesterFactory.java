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

package org.bemuisoft.quantum.test.api;

import org.bemuisoft.quantum.api.IQubitAnalyzer;
import org.bemuisoft.quantum.api.IQubitFactory;

/**
 * A factory for qubit testers.
 * 
 * @author Benno Muilwijk
 * @see IQubitFactory
 * @see QubitTester
 */
public class QubitTesterFactory implements IQubitFactory<QubitTester> {

	private IQubitFactory<? extends IQubitAnalyzer> qtFactory;
	private IQubitFactory<? extends IQubitAnalyzer> qvFactory;

	/**
	 * Factory constructor for qubit testers without verification qubits.
	 * 
	 * @param qtFactory	factory for the qubits to be tested
	 */
	public QubitTesterFactory(IQubitFactory<? extends IQubitAnalyzer> qtFactory) {
		this(qtFactory, null);
	}

	/**
	 * Factory constructor for qubit testers with verification qubits.
	 * 
	 * @param qtFactory	factory for the qubits to be tested
	 * @param qvFactory	factory for the verification qubits
	 */
	public QubitTesterFactory(
			IQubitFactory<? extends IQubitAnalyzer> qtFactory,
			IQubitFactory<? extends IQubitAnalyzer> qvFactory)
	{
		this.qtFactory = qtFactory;
		this.qvFactory = qvFactory;
	}

	@Override
	public QubitTester newQubit(String label) {
		IQubitAnalyzer qt = qtFactory.newQubit(label);
		IQubitAnalyzer qv = (qvFactory == null) ? null : qvFactory.newQubit(label);
		return new QubitTester(qt, qv);
	}

}
