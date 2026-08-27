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
 * An instance of this class represents a vector in R³.
 * <p>
 * This class does not implement any method to change the vector
 * after instantiation, but subclasses may do so.
 * 
 * @author Benno Muilwijk
 */
public class SpatialVector {
	
	/** The Euclidian distances along the X, Y and Z axis. */
	protected double x, y, z;

	/**
	 * Constructs a zero vector.
	 */
	public SpatialVector() {
		this(0.0, 0.0, 0.0);
	}

	/**
	 * Constructs an instance from Cartesian coordinates x, y and z.
	 * 
	 * @param x		- x
	 * @param y		- y
	 * @param z		- z
	 */
	public SpatialVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Returns the dot product of this spatial vector and another.
	 * 
	 * @param other	- the other vector
	 * @return the dot product this ⋅ other
	 */
	public double dotProduct(SpatialVector other) {
		return getX()*other.getX() + getY()*other.getY() + getZ()*other.getZ();
	}

	/**
	 * Returns the value of the x-dimension.
	 * 
	 * @return the value of x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Returns the value of the y-dimension.
	 * 
	 * @return the value of y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Returns the value of the z-dimension.
	 * 
	 * @return the value of z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * Returns a new {@code SpatialVector} object
	 * which is the cross product of the given vectors.
	 * 
	 * @param a		- vector a
	 * @param b		- vector b
	 * @return	the cross product a × b
	 */
	public static SpatialVector crossProduct(SpatialVector a, SpatialVector b) {
		final double a1 = a.getX();
		final double a2 = a.getY();
		final double a3 = a.getZ();
		final double b1 = b.getX();
		final double b2 = b.getY();
		final double b3 = b.getZ();
		SpatialVector v = new SpatialVector();
		v.x = a2*b3 - a3*b2;
		v.y = a3*b1 - a1*b3;
		v.z = a1*b2 - a2*b1;
		return v;
	}

}
