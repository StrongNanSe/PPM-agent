package kr.co.ppm.agent.device;

import com.pi4j.io.gpio.*;

public class MotorUtil implements Runnable{
    public static boolean isWarn = false;
    private boolean isFold;
    private int stepDuration;
    private GpioController gpio;
    private GpioPinDigitalOutput[] motorPins;
    private static final PinState LOW = PinState.LOW;
    private static final PinState HIGH = PinState.HIGH;
		
	private final PinState motorSequence[][] = new PinState[][] { { LOW, LOW, LOW, HIGH },
            { LOW, LOW, HIGH, LOW }, { LOW, HIGH, LOW, LOW }, { HIGH, LOW, LOW, LOW }, { LOW, LOW, LOW, HIGH },
            { LOW, LOW, HIGH, LOW }, { LOW, HIGH, LOW, LOW }, { HIGH, LOW, LOW, LOW } };

    public MotorUtil(Pin pinA, Pin pinB, Pin pinC, Pin pinD, int stepDuration, boolean isFold) {
		this.gpio = GpioFactory.getInstance();
		
        motorPins = new GpioPinDigitalOutput[4];

        motorPins[0] = gpio.provisionDigitalOutputPin(pinA, "Pin A", LOW);
        motorPins[1] = gpio.provisionDigitalOutputPin(pinB, "Pin B", LOW);
        motorPins[2] = gpio.provisionDigitalOutputPin(pinC, "Pin C", LOW);
        motorPins[3] = gpio.provisionDigitalOutputPin(pinD, "Pin D", LOW);
		
        this.stepDuration = stepDuration;
        this.isFold = isFold;
    }

    public void foldAction() throws InterruptedException {
        int steps;
        steps = (512 * 4 * 200) / 360;

        step(steps);
    }
    
    public void unfoldAction() throws InterruptedException {
        int steps;
        steps = (512 * 4 * -200) / 360;

        step(steps);
    }

    public void step(int noOfSteps) throws InterruptedException {
        if (noOfSteps > 0) {
            for (int currentStep = noOfSteps; currentStep > 0; currentStep--) {
                int currentSequenceNo = currentStep % 8;

                while (isWarn) {
					Thread.sleep(100);
				}

                writeSequence(currentSequenceNo);
            }
        } else {
            for (int currentStep = 0; currentStep < Math.abs(noOfSteps); currentStep++) {
                int currentSequenceNo = currentStep % 8;               
				
				while (isWarn) {
					Thread.sleep(100);
				}

                writeSequence(currentSequenceNo);
            }
        }
    }

    private void writeSequence(int sequenceNo) {
        for (int i = 0; i < 4; i++) {
            motorPins[i].setState(motorSequence[sequenceNo][i]);
        }
        try {
            Thread.sleep(stepDuration);
        } catch (InterruptedException e) {
			e.printStackTrace();
        }
    }

    @Override
    public void run() {
		try {
			if (isFold) {
				foldAction();
			} else {
				unfoldAction();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		DeviceUtilImpl.isMotorAction = false;

		gpio.shutdown();
        gpio.unprovisionPin(motorPins);
		
		System.out.println("MotorActionComplete");
    }
}
