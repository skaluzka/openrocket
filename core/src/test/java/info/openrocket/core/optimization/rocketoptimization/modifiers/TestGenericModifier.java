package info.openrocket.core.optimization.rocketoptimization.modifiers;

import static info.openrocket.core.util.MathUtil.EPSILON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import info.openrocket.core.document.Simulation;
import info.openrocket.core.optimization.general.OptimizationException;
import info.openrocket.core.rocketcomponent.Rocket;
import info.openrocket.core.unit.UnitGroup;

import info.openrocket.core.util.BaseTestCase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestGenericModifier extends BaseTestCase {

	private TestValue value;
	private GenericModifier<TestValue> gm;
	private Simulation sim;

	@BeforeEach
	public void setup() {
		value = new TestValue();
		sim = new Simulation(new Rocket());

		Object related = new Object();

		gm = new GenericModifier<>("Test modifier", "Description", related,
				UnitGroup.UNITS_NONE, 2.0, TestValue.class, "value") {
			@Override
			protected TestValue getModifiedObject(Simulation simulation) {
				Assertions.assertTrue(simulation == sim);
				return value;
			}
		};

		gm.setMinValue(0.5);
		gm.setMaxValue(5.5);
	}

	@Test
	public void testGetCurrentValue() throws OptimizationException {
		value.d = 1.0;
		assertEquals(2.0, gm.getCurrentSIValue(sim), EPSILON);
		value.d = 2.0;
		assertEquals(4.0, gm.getCurrentSIValue(sim), EPSILON);
	}

	@Test
	public void testGetCurrentScaledValue() throws OptimizationException {
		value.d = 0.0;
		assertEquals(-0.1, gm.getCurrentScaledValue(sim), EPSILON);
		value.d = 1.0;
		assertEquals(0.3, gm.getCurrentScaledValue(sim), EPSILON);
		value.d = 2.0;
		assertEquals(0.7, gm.getCurrentScaledValue(sim), EPSILON);
		value.d = 3.0;
		assertEquals(1.1, gm.getCurrentScaledValue(sim), EPSILON);
	}

	@Test
	public void testModify() throws OptimizationException {
		value.d = 0.0;
		gm.modify(sim, -0.5);
		assertEquals(-1.0, value.d, EPSILON);

		gm.modify(sim, 0.0);
		assertEquals(0.25, value.d, EPSILON);

		gm.modify(sim, 0.5);
		assertEquals(1.5, value.d, EPSILON);

		gm.modify(sim, 1.0);
		assertEquals(2.75, value.d, EPSILON);

		gm.modify(sim, 1.5);
		assertEquals(4.0, value.d, EPSILON);
	}

	@Test
	public void testSingularRange() throws OptimizationException {
		gm.setMinValue(1.0);
		gm.setMaxValue(1.0);
		value.d = 0.25;
		assertEquals(0.0, gm.getCurrentScaledValue(sim), EPSILON);
		value.d = 0.5;
		assertEquals(0.5, gm.getCurrentScaledValue(sim), EPSILON);
		value.d = 0.50001;
		assertEquals(1.0, gm.getCurrentScaledValue(sim), EPSILON);
	}

	public class TestValue {
		private double d;

		public double getValue() {
			return d;
		}

		public void setValue(double value) {
			this.d = value;
		}
	}

}
