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
package org.bemuisoft.qm.core;

import org.bemuisoft.quantum.api.Base;

/**
 * A base interface for tests.
 * 
 * @author Benno Muilwijk
 */
public interface TestBase extends Base {

	public default void log(SimpleBlochVector v) {
		log(v.toString());
	}

	public default double random2Pi() {
		return PI * randomCos();
	}

	public default double randomTheta() {
		return Math.acos(randomCos());
	}

}
