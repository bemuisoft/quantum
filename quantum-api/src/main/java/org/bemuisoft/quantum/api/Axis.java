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

import org.bemuisoft.math.base.SpatialVector;
import org.bemuisoft.math.base.UnitVector;

/**
 * An instance of this class represents a direction in 3D space,
 * which maps to an axis through the center of a Bloch sphere.
 * <p>
 * This class does not implement any method to change the direction
 * after instantiation, but subclasses may do so.

 * @author Benno Muilwijk
 */
public class Axis extends SpatialVector implements UnitVector, Base {

	// predefined standard axes (these are not mutable, so can be shared)
	
	/** The X-axis. */
	public static Axis X = new Axis(1.0, 0.0, 0.0);
	
	/** The Y-axis. */
	public static Axis Y = new Axis(0.0, 1.0, 0.0);
	
	/** The Z-axis. */
	public static Axis Z = new Axis(0.0, 0.0, 1.0);
	
	/** The Hadamard axis. */
	public static Axis H = new Axis(QUARTER_PI, 0.0);
	
	// instance variables
	private double phi = Double.NaN;
	private double theta = Double.NaN;

	/**
	 * Constructs an instance which is equal to the Z-axis.
	 */
	public Axis() {
		super(0.0, 0.0, 1.0);
	}

	/**
	 * Constructs an instance from Cartesian coordinates x, y and z.
	 * 
	 * @param x		- x
	 * @param y		- y
	 * @param z		- z
	 */
	public Axis(double x, double y, double z) {
		// normalize in case x² + y² + z² != 1
		final double r = Math.sqrt(x*x + y*y + z*z);
		super.x = x/r;
		super.y = y/r;
		super.z = z/r;
	}

	/**
	 * Constructs an instance from spherical coordinates theta and phi.
	 * 
	 * @param theta	- the polar angle
	 * @param phi	- the azimuthal angle
	 */
	public Axis(double theta, double phi) {
		final double sinTheta = Math.sin(theta);
		super.x = sinTheta*Math.cos(phi);
		super.y = sinTheta*Math.sin(phi);
		super.z = Math.cos(theta);
		this.phi = phi;
		this.theta = theta;
	}

	/**
	 * Returns the azimuthal angle relative to the x-axis
	 * after projection onto the XOY plane.
	 * This gives the following relations with x and y:<ul>
	 * <li>cos(phi) = x / sqrt(x²+y²)
	 * <li>sin(phi) = y / sqrt(x²+y²)
	 * <li>tan(phi) = y / x
	 * </ul>
	 * This angle is also referred to as the phase.
	 * 
	 * @return the azimuthal angle phi in radians
	 */
	public double getPhi() {
		if (Double.isNaN(phi)) {
			phi = Math.atan2(getY(), getX());
		}
		return phi;
	}

	/**
	 * Returns the polar angle relative to the z-axis.
	 * This gives the following relation with z:<ul>
	 * <li>cos(theta) = z
	 * </ul>
	 * @return the polar angle theta in radians
	 */
	public double getTheta(){
		if (Double.isNaN(theta)) {
			// calculate theta
			theta = Math.acos(getZ());
		}
		return theta;
	}

}
