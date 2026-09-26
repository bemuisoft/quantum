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
package org.bemuisoft.qm.sim;

import org.bemuisoft.quantum.test.api.CHSHExperiment;

/**
 * CHSH test for {@link QubitQM}.
 * <p>
 * Not intended for automated testing.
 * Log output should be inspected visually.
 * 
 * @author Benno Muilwijk
 */
public class CHSHTest extends CHSHExperiment<QubitQM> {

	public static void main(String[] args) {
		try {
			QubitQM.debug = false;
			CHSHTest test = new CHSHTest();
			test.run(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CHSHTest() {
		super(QubitQM.factory(2, false));
	}

}
