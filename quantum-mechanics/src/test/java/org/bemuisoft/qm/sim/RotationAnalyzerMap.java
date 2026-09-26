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

import java.util.LinkedHashMap;

import org.bemuisoft.quantum.api.Base;

/**
 * A map for {@link RotationAnalyzer} objects.
 * 
 * @author Benno Muilwijk
 */
public class RotationAnalyzerMap extends LinkedHashMap<String, RotationAnalyzer> implements Base {

	public final int steps;

	public RotationAnalyzerMap(int steps) {
		this.steps = steps;
	}

	public RotationAnalyzer get(String label) {
		RotationAnalyzer ra = super.get(label);
		if (ra == null) {
			ra = new RotationAnalyzer(label, steps + 1);
			super.put(label, ra);
		}
		return ra;
	}

	public void logRotationAxes() {
		// log rotation exis for each analyzer in the map
		for (RotationAnalyzer ra : values()) {
			log(ra.getLabel() + " " + ra.getRotationAxis());
		}
	}

	public void logExtremesFromCenter() {
		// log extremes for each analyzer in the map
		for (RotationAnalyzer ra : values()) {
			double stepSize = round(2.0 / (ra.getSamples().size() - 1), 5);
			log(ra.getLabel() + " extremes relative to " + ra.getCenter() + ", step size = " + stepSize + 'π');
			ra.logExtremes();
		}
	}

	public void logExtremeLengths(String labelPrefix) {
		RotationAnalyzer ra0 = get(labelPrefix + '0');
		RotationAnalyzer ra1 = get(labelPrefix + '1');
		int size = ra0.getSamples().size();
		assert size == ra1.getSamples().size();
		double minLength = 2.0;
		double maxLength = 0.0;
		for (int i = 0; i < size; i++) {
			double length = ra0.getSample(i).length() + ra1.getSample(i).length();
			if (minLength > length) minLength = length;
			if (maxLength < length) maxLength = length;
		}
		log("|" + ra0.getLabel() + "| + |" + ra1.getLabel() + "| min = " + minLength + ", max = " + maxLength);
	}

}
