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
package org.bemuisoft.quantum.adapters.quantum4j.junit;

import org.bemuisoft.quantum.adapters.quantum4j.Qubit4J;
import org.bemuisoft.quantum.api.IQubit;
import org.bemuisoft.quantum.api.IQubitFactory;
import org.bemuisoft.quantum.test.junit.OneQubitTest;

/**
 * Tests the {@link IQubit} implementation of {@link Qubit4J}
 * in a single qubit system.
 * 
 * @author Benno Muilwijk
 * 
 * @see OneQubitTest
 */
class OneQubit4JTest extends OneQubitTest<Qubit4J> {

	@Override
	protected IQubitFactory<Qubit4J> getFactory(int n) {
		return Qubit4J.factory(n);
	}

}
