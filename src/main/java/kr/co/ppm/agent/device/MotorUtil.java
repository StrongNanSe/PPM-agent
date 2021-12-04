package kr.co.ppm.agent.device;

import com.pi4j.io.gpio.*;

public class MotorUtil implements Runnable{
    public static boolean isMove = false;
    public static boolean isWarn = false;
    public static int angle = 0;
    private  GpioController gpio;
    private static final PinState LOW = PinState.LOW;
    private static final PinState HIGH = PinState.HIGH;


    private static final PinState MOTOR_SEQUENCE[][] = new PinState[][] { { LOW, LOW, LOW, HIGH },
            { LOW, LOW, HIGH, LOW }, { LOW, HIGH, LOW, LOW }, { HIGH, LOW, LOW, LOW }, { LOW, LOW, LOW, HIGH },
            { LOW, LOW, HIGH, LOW }, { LOW, HIGH, LOW, LOW }, { HIGH, LOW, LOW, LOW } };

    private int stepDuration;


    private GpioPinDigitalOutput[] motorPins;

    public MotorUtil(Pin pinA, Pin pinB, Pin pinC, Pin pinD, int stepDuration)
    {
		gpio = GpioFactory.getInstance();
		
        motorPins = new GpioPinDigitalOutput[4];

        motorPins[0] = gpio.provisionDigitalOutputPin(pinA, "Pin A", LOW);
        motorPins[1] = gpio.provisionDigitalOutputPin(pinB, "Pin B", LOW);
        motorPins[2] = gpio.provisionDigitalOutputPin(pinC, "Pin C", LOW);
        motorPins[3] = gpio.provisionDigitalOutputPin(pinD, "Pin D", LOW);
		
        this.stepDuration = stepDuration;
    }

    public void action() {
        int steps;
        steps = (int) (512 * 4 * angle) / 360;

        step(steps);
    }

    public void step(int noOfSteps) {
        if (noOfSteps > 0) {
            for (int currentStep = noOfSteps; currentStep > 0; currentStep--) {
                int currentSequenceNo = currentStep % 8;

                if (isWarn) {
                    try {
                        Thread.sleep(2000);

                        isWarn = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                writeSequence(currentSequenceNo);
            }
        } else {
            for (int currentStep = 0; currentStep < Math.abs(noOfSteps); currentStep++) {
                int currentSequenceNo = currentStep % 8;

                if (isWarn) {
                    try {
                        Thread.sleep(2000);
                        isWarn = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                writeSequence(currentSequenceNo);
            }
        }
    }

    private void writeSequence(int sequenceNo) {
        for (int i = 0; i < 4; i++) {
            motorPins[i].setState(MOTOR_SEQUENCE[sequenceNo][i]);
        }
        try {
            Thread.sleep(stepDuration);
        } catch (InterruptedException e) {
        }
    }

    @Override
    public void run() {
        action();

        this.isMove = false;

		gpio.shutdown();
        gpio.unprovisionPin(motorPins);
		
		System.out.println("MotorActionComplete");
    }
}
