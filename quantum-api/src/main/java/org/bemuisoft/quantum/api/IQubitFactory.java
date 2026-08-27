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

/**
 * A generic interface for factories that produce
 * qubit implementations of some type.
 * <p>
 * It is recommended that the implementing class &lt;Q&gt;
 * has a static method {@code factory} which returns
 * a factory that implements this interface.
 * 
 * @param <Q>	the type of qubit produced by this factory
 * 
 * @author Benno Muilwijk
 */
public interface IQubitFactory<Q> {

	/**
	 * Returns an object of type Q that implements
	 * (a simulation of) qubit behavior.
	 * <p>
	 * Class Q should typically implement either
	 * {@link IQubit} or {@link IQubitAnalyzer}.
	 * 
	 * @param label		a label that identifies the qubit
	 * @return			a new qubit instance of type Q
	 */
	public Q newQubit(String label);

}
