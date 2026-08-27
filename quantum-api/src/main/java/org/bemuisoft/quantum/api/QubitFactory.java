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

package org.bemuisoft.quantum.api;

import java.lang.reflect.Constructor;

/**
 * A common factory that produces
 * qubit implementations of some type.
 * <p>
 * The implementing class &lt;Q&gt; must be
 * passed as an argument to the constructor
 * of this factory.
 * 
 * @param <Q>	the type of qubit produced by this factory
 * 
 * @author Benno Muilwijk
 * @see IQubitFactory
 */
public class QubitFactory<Q> implements IQubitFactory<Q> {

	/** The implementing class Q. */
	protected Class<Q> implClass;
	/** A shared quantum state. */
	protected IQuantumState qs;

	/**
	 * Factory constructor for qubit implementations that do
	 * not require a reference to a shared quantum state.
	 * 
	 * @param implClass	the class Q that implements the qubit
	 */
	public QubitFactory(Class<Q> implClass) {
		this.implClass = implClass;
	}

	/**
	 * Factory constructor for qubit implementations that
	 * require a reference to a shared quantum state.
	 * 
	 * @param qs		a quantum state object
	 * @param implClass	the class Q that implements the qubit
	 * @see IQuantumState
	 */
	public QubitFactory(IQuantumState qs, Class<Q> implClass) {
		this.implClass = implClass;
		this.qs = qs;
	}

	/**
	 * Returns an object of type Q that implements
	 * (a simulation of) qubit behavior.
	 * <p>
	 * Class Q must have at least one of the following constructors:
	 * <ul>
	 * <li>Q(String label) if no quantum state object was passed to this factory.</li>
	 * <li>Q(QS quantumState, String label) if a quantum state object of type QS
	 *		 was passed to this factory.</li>
	 * </ul>
	 * 
	 * @param label		a label that identifies the qubit
	 * @return			a new qubit instance of type Q
	 */
	@Override
	public Q newQubit(String label) {
		try {
			if (qs == null) {
				return implClass.getDeclaredConstructor(String.class).newInstance(label);
			}
			Constructor<Q> constructor = implClass.getDeclaredConstructor(qs.getClass(), String.class);
			return constructor.newInstance(qs, label);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
