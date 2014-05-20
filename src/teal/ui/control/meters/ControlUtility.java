package teal.ui.control.meters;

import java.text.*;

import teal.util.Format;

/**
 * This class contains the utilities used by the teal.ui.control package.
 * No methods in this class are public.
 */

public class ControlUtility {

	static final double BASE = 10;

	public ControlUtility() {
	}

	static String makeValidName(String name) {
		if (name != null && name.length() == 0)
			name = null; // treat empty string as null
		return name;
	}

	static String format(double x) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(3);
		return numberFormat.format(x);
	}

	static String format(double x, String fmt) {
		Format format = new Format(fmt);
		char code = format.getFormatCode();
		if (code == 'd' || code == 'i' || code == 'o' || code == 'x' || code == 'X') {
			long y = new Double(x).longValue();
			return format.form(y);
		} else {
			return format.form(x);
		}
	}

	/**
	 * Generate pretty axis and tick number.
	 * the tick number will be 6 - 10 depend on the range
	 */
	static double[] makePrettyAxis(double low, double high) {
		double[] lo_hi_tick = new double[3];
		if (low == high) {
			high += 1;
			//low  -= 1;
		}
		double k = Math.floor(Math.log(high - low) / Math.log(BASE));
		double tenKpow = Math.pow(BASE, k);
		lo_hi_tick[0] = tenKpow * Math.floor(low / tenKpow);
		lo_hi_tick[1] = tenKpow * Math.ceil(high / tenKpow);
		int tick = (int) ((lo_hi_tick[1] - lo_hi_tick[0]) / tenKpow);
		if (tick <= 2 || tick == 5)
			tick = 10;
		if (tick == 3 || tick == 4)
			tick *= 2;
		lo_hi_tick[2] = tick;
		return lo_hi_tick;
	}

}
