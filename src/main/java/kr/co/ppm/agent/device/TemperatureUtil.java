package kr.co.ppm.agent.device;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;

public class TemperatureUtil {
	private int pinNo;
	private int[] dataSet = { 0, 0, 0, 0, 0 };

	public TemperatureUtil(int pinNo) {
		if (Gpio.wiringPiSetup() == -1) {
			System.out.println(" ==>> GPIO SETUP FAILED");

			return;
		}
		
		this.pinNo = pinNo;

		GpioUtil.export(pinNo, GpioUtil.DIRECTION_OUT);
	}

	public int measure() {
		int status = Gpio.HIGH;

		dataSet[0] = 0;
		dataSet[1] = 0;
		dataSet[2] = 0;
		dataSet[3] = 0;
		dataSet[4] = 0;

		Gpio.pinMode(pinNo, Gpio.OUTPUT);
		Gpio.digitalWrite(pinNo, Gpio.LOW);
		Gpio.delay(18);

		Gpio.digitalWrite(pinNo, Gpio.HIGH);
		Gpio.pinMode(pinNo, Gpio.INPUT);

		int j = 0;

		for (int i = 0; i < 85; i++) {
			int count = 0;

			while (Gpio.digitalRead(pinNo) == status) {
				count++;

				Gpio.delayMicroseconds(2);
				if (count == 255) {
					break;
				}
			}

			status = Gpio.digitalRead(pinNo);

			if (count == 255) {
				break;
			}

			if ((i >= 4)
					&& (i % 2 == 0)) {
				dataSet[j / 8] <<= 1;

				if (count > 16) {
					dataSet[j / 8] |= 1;
				}

				j++;
			}
		}

		float c = 0;

		if ((j >= 40)
				&& checkParity()) {
			c = (float) (((dataSet[2] & 0x7F) << 8) + dataSet[3]) / 10;

			if (c > 125) {
				c = dataSet[2];
			}

			if ((dataSet[2] & 0x80) != 0) {
				c = -c;
			}

		} else {
			return measure();
		}
		
		System.out.println("End Temperature Measure");
		
		return (int)c;
	}

	private boolean checkParity() {
		return (dataSet[4] == ((dataSet[0] + dataSet[1] + dataSet[2] + dataSet[3]) & 0xFF));
	}
}
