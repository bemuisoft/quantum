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

import java.util.ArrayList;
import java.util.List;

import org.bemuisoft.qm.core.SimpleBlochVector;
import org.bemuisoft.qm.core.BlochVectorAnalyzer;
import org.bemuisoft.qm.core.TestBase;

/**
 * Helper class for analysis of Bloch vector component rotations,
 * used by several test classes.
 * 
 * @author Benno Muilwijk
 */
public class RotationAnalyzer implements TestBase {

	private String label;
	private BlochVectorAnalyzer center;
	private List<SimpleBlochVector> samples;

	public RotationAnalyzer(String label) {
		this(label, 49);
	}

	public RotationAnalyzer(String label, int samples) {
		this.label = label;
		this.samples = new ArrayList<>(samples);
	}

	public String getLabel() {
		return label;
	}

	public void addSample(SimpleBlochVector v) {
		samples.add(v);
	}

	private void checkSamples() {
		// - last sample must be complete total rotation of 2π, and thus be equal to first sample
		// - rotation between two adjacent samples must be (and is assumed to be) 2π / index of last sample,
		//	 so total rotation of each sample is index of the sample * 2π / index of last sample
		// - there must be an odd number of samples, so the last valid index is even,
		//	 so each sample is matched by a counter sample at the opposite side of the center (rotated exactly π)
		final int first = 0;
		final int last = samples.size() - 1;
		final double margin = 1e-12;
		if (!samples.get(last).isCloseTo(samples.get(first), margin)) {
			throw new IllegalStateException("Last sample must be equal to first after total rotation of 2π, but it is not.");
		}
		if (last % 2 != 0) {
			throw new IllegalStateException("Opposite sample is missing; a full (2π) rotation must be sampled in an even number of regular steps.");
		}
		final int opposite = last / 2;
		center = BlochVectorAnalyzer.sum(label + " center", samples.get(first), samples.get(opposite)).scale(2);
		for (int i = 1; i < opposite; i++) {
			if (!BlochVectorAnalyzer.sum("center", samples.get(i), samples.get(i + opposite)).scale(2).isCloseTo(center, margin)) {
				throw new IllegalStateException("Center of rotation for " + label + " is ambiguous.");
			}
		}
	}

	public List<SimpleBlochVector> getSamples() {
		return samples;
	}

	public SimpleBlochVector getSample(int index) {
		return samples.get(index);
	}

	public BlochVectorAnalyzer getCenter() {
		if (center == null) {
			checkSamples();		// this will set and verify center
		}
		return center;
	}

	public BlochVectorAnalyzer getRotationAxis() {
		BlochVectorAnalyzer axis = BlochVectorAnalyzer.crossProduct("rotation axis", fromCenter(0), fromCenter(samples.size() / 4));
		return axis.scale(axis.length());
	}

	/**
	 * Returns how much the source qubit must rotate from sample(0)
	 * to give the major axis for this rotation analyzer.
	 * 
	 * @return angular difference of source qubit
	 */
	public double getPhiMajor() {
		// exact calculation of source qubit angular offset to get semi major axis
		// number of steps in full circle must be multiple of 4 for this to work
		int quarter = samples.size() / 4;
		SimpleBlochVector u = fromCenter(0);
		SimpleBlochVector v = fromCenter(quarter);
		double d = u.dotProduct(u) - v.dotProduct(v);					// |u|² - |v|²
		double base = 0.0;
		if (Math.abs(d) < 1e-15 && samples.size() > 7) {
			// could be circle or special case of intersection with a circle
			// check using different samples
			int eighth = quarter / 2;				// e.g. quarter = 3 -> eighth = 3 / 2 == 1 (truncated)
			u = fromCenter(eighth);
			v = fromCenter(eighth + quarter);
			d = u.dotProduct(u) - v.dotProduct(v);
			if (Math.abs(d) < 1e-15) {
				// it's a circle
				return 0.0;
			}
			base = (PI * eighth) / (2*quarter);		// eighth * step size == eighth * (2π / steps) == eighth * (2π / 4quarter)
		}
		double phiMajor = 0.5 * Math.atan2(2.0 * u.dotProduct(v), d);	// atan(2*u· v / (|u|² - |v|²)) / 2
		return phiMajor + base;
	}

	/**
	 * Returns how much the source qubit must rotate from sample(0)
	 * to give the minor axis for this rotation analyzer.
	 * 
	 * @return angular difference of source qubit
	 */
	public double getPhiMinor() {
		// exact calculation of source qubit angular offset to get semi minor axis
		return getPhiMajor() + HALF_PI;
	}

	/**
	 * Returns the .
	 * 
	 * @param direction		
	 * @return direction scaled to the proper length of the spheroid semi major axis
	 */
	public SimpleBlochVector getSpheroidSemiMajor(SimpleBlochVector direction) {
		// first scale to unit length
		BlochVectorAnalyzer axis = new BlochVectorAnalyzer(direction.getLabel(), direction).scale(direction.length());
		double a = 0.0;
		if (getLabel() != null && Character.isDigit(getLabel().charAt(1))) {
			// this is a rotation analyzer for a single elementary component
			a = (getSample(0).length() + getSample(samples.size()/2).length()) / 2.0;
			// should always work, but continue to verify for now
		}
		// get semi major and minor axis of ellipse, which is the intersection of the plane of rotation and the spheroid
		final List<BlochVectorAnalyzer> extremes = getExtremes();
		int n = extremes.size();
		assert n == 4 || n == 0 : "Unexpected number of extremes => not an ellipse.";
		// calculate length a of the spheroid semi major axis
		if (n == 0) {
			// rotation is a circle
			if (BlochVectorAnalyzer.crossProduct(null, axis, getRotationAxis()).length() < 1e-12) {
				// plane of circle is perpendicular to spheroid major axis; can't calculate the length
				a = 0.0;	// will give length NaN after scaling
			} else {
				// spheroid is a sphere with same radius
				a = fromCenter(0).length(); 
			}
		}
		if (extremes.size() == 4) {
			BlochVectorAnalyzer semiMajorAxis = extremes.get(0);
			BlochVectorAnalyzer semiMinorAxis = extremes.get(1);
			if (semiMajorAxis.length() < semiMinorAxis.length()) {
				semiMajorAxis = extremes.get(1);
				semiMinorAxis = extremes.get(0);
			}
			double b = semiMinorAxis.length();			// of ellipse and half the smallest diameter of the spheroid
			double r = semiMajorAxis.length();			// of ellipse
			if (b < 1e-5) {
				// straight line, no reliable way to calculate length of spheroid major axis
				// following solution works only for single elementary component
				a = (getSample(0).length() + getSample(samples.size()/2).length()) / 2.0;
			} else {
				double rCosAlpha = Math.abs(semiMajorAxis.dotProduct(axis));
				double rSinAlpha = Math.sqrt(round(r*r - rCosAlpha*rCosAlpha, 12));
				double sinTheta = rSinAlpha / b;
				double cosTheta = Math.sqrt(1 - sinTheta*sinTheta);
				if (a == 0.0) {
					a = rCosAlpha / cosTheta;				// length of the spheroid semi major axis
				} else {
					assert Math.abs(a - rCosAlpha / cosTheta) < 1e-8;
				}
			}
		}
		// return the axis with the calculated length
		return axis.scale(1.0 / a);
	}

	public List<BlochVectorAnalyzer> getExtremes() {
		// create work array with samples relative to center, and first two entries the same as the last two
		BlochVectorAnalyzer[] work = new BlochVectorAnalyzer[samples.size() + 1];
		work[1] = fromCenter(0);												// first sample is same as last sample (mod 2π)
		work[0] = fromCenter(samples.size() - 2);								// penultimate sample is the one before the last and first
		work[work.length - 1] = work[1];										// last sample is same as first sample (mod 2π)
		work[work.length - 2] = work[0];										// penultimate sample is the one before the last
		for (int i = 2; i < work.length - 2; i++) {
			work[i] = fromCenter(i - 1);
		}
		// determine the extremes
		List<BlochVectorAnalyzer> extremes = new ArrayList<>(4);
		BlochVectorAnalyzer prev, curr, next;
		curr = work[0];
		next = work[1];
		final double margin = 1e-12;
//		final double stepSize = 2.0 / (getSamples().size() - 1);				// as fraction of π
		for (int n = 2; n < work.length; n++) {
			prev = curr;
			curr = next;
			next = work[n];
			double r = curr.length() - margin;
			if (r > prev.length() && r > next.length()) {
				// add maximum
				extremes.add(curr);
//				log(label + " maximum at step " + (n-2) + " (" + round(stepSize * (n-2), 5) + "π): " + curr);
			}
			r = curr.length() + margin;
			if (r < prev.length() && r < next.length()) {
				// add minimum
				extremes.add(curr);
//				log(label + " minimum at step " + (n-2) + " (" + round(stepSize * (n-2), 5) + "π): " + curr);
			}
		}
		return extremes;
	}

	public void logExtremes() {
		final List<BlochVectorAnalyzer> extremes = getExtremes();
		for (BlochVectorAnalyzer v : extremes) {
			log(v);
		}
		if (extremes.size() == 0) {
			log(label + " rotates with constant radius = " + round(fromCenter(0).length(), 12));
			log(BlochVectorAnalyzer.crossProduct("rotation axis", fromCenter(0), fromCenter(samples.size() / 4)));
		}
		if (extremes.size() == 4) {
			log(BlochVectorAnalyzer.crossProduct("rotation axis", extremes.get(0), extremes.get(1)));
//			log("length (r) should equal min.r * max.r = " + (a*b));
			// this is an intrinsic property of an ellipse:
			// x1 = a cos(phi), y1 = b sin(phi)
			// x2 = a cos(phi + π/2), y2 = b sin(phi + π/2)
			// (x1, y1, 0) x (x2, y2, 0) = (0, 0, x1*y2 - x2*y1) = (0, 0, ab cos(phi)sin(phi + π/2) - ab cos(phi + π/2) sin(phi))
			// as sin(phi + π/2) = cos(phi) and cos(phi + π/2) = -sin(phi) =>
			// the length of this cross product = ab cos²(phi) + ab sin²(phi) = ab(cos²(phi) + sin²(phi)) = ab
			// for every value of phi.
			// also note:
			// x1² + x2² = a²(cos²(phi) + cos²(phi + π/2)) = a²(cos²(phi) + sin²(phi)) = a²
			// y1² + y2² = b²(sin²(phi) + sin²(phi + π/2)) = b²(sin²(phi) + cos²(phi)) = b²
			
			// is semi minor axis perpendicular to center vector? Usually not
//			if (Math.abs(semiMinorAxis.dotProduct(getCenter())) > 1e-3) {
//				log("dot product semi minor axis and center vector = " + (semiMinorAxis.dotProduct(getCenter())));
//			}
			// check exact calculation of semi major and minor axis
			log("phi major = " + toPi(getPhiMajor()));
		}
	}

	public BlochVectorAnalyzer fromCenter(int sampleIndex) {
		return new BlochVectorAnalyzer(samples.get(sampleIndex)).subtract(getCenter());
	}

}
