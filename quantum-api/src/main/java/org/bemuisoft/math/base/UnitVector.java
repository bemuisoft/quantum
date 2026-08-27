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

package org.bemuisoft.math.base;

/**
 * A unit vector is defined as a vector with norm 1.
 * A 2-dimensional unit vector [x,y] has x² + y² = 1,
 * a 3-dimensional unit vector [x,y,z] has x² + y² + z² = 1,
 * et cetera.
 * <p>
 * It defines a direction in n-dimensional space.
 * Implementing classes must ensure that the norm is indeed 1.
 * 
 * @author Benno Muilwijk
 */
public interface UnitVector {

	/**
	 * Returns the norm, or length, of this vector.
	 * <p>
	 * The norm of a unit vector is always 1.0, by definition.
	 * 
	 * @return the norm
	 */
	public default double norm() {
		return 1.0;
	}

}
