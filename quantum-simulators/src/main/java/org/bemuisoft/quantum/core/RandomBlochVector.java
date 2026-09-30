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
package org.bemuisoft.quantum.core;

/**
 * This class implements a randomizable Bloch vector.
 * 
 *  @author Benno Muilwijk
 */
public class RandomBlochVector extends BlochVector {

	/**
	 * Constructs a zero vector.
	 */
	public RandomBlochVector() {
		super();
	}

	/**
	 * Constructs a vector with Cartesian coordinates x, y and z.
	 * 
	 * @param x	- length in x-direction
	 * @param y	- length in y-direction
	 * @param z	- length in z-direction
	 */
	public RandomBlochVector(double x, double y, double z) {
		super(x, y, z);
	}

	/**
	 * Randomizes the direction of this vector.
	 * 
	 * @return	this vector randomized
	 */
	public BlochVector randomize() {
		double cos = randomCos();
		double sin = Math.sqrt(1 - cos*cos);
		double phase = Math.random() * TWO_PI;
		return this.setXYZ(sin*Math.cos(phase), sin*Math.sin(phase), cos);
	}

}
