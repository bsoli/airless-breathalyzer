package com.aabb.airlessbreathalyzer;

import java.text.DecimalFormat;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.BitmapFactory.Options;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * This is an example of using the accelerometer to integrate the device's
 * acceleration to a position using the Verlet method. This is illustrated with
 * a very simple particle system comprised of a few iron balls freely moving on
 * an inclined wooden table. The inclination of the virtual table is controlled
 * by the device's accelerometer.
 *
 * @see SensorManager
 * @see SensorEvent
 * @see Sensor
 */

public class LevelActivity extends Activity {

    private SimulationView mSimulationView;
    private SensorManager mSensorManager;
    protected PowerManager.WakeLock mWakeLock;
    private UpdownCasting udc = new UpdownCasting();
    private float centerX, centerY;
    private float xx = 0f, yy = 0f;
    private float[] rotationMatrix = new float[9];
    private float[] accel = new float[3];
    private float[] magnet = new float[3];
    private float[] accMagOrientation = new float[3];
    private Handler /* mHandler, */mHandlerN;
    private DecimalFormat d = new DecimalFormat("#.##");
    private float timestamp;
    private boolean initState = true;
    private float[] gyroMatrix = new float[9];
    private float[] gyro = new float[3];
    private static final float NS2S = 1.0f / 1000000000.0f;
    private static final float EPSILON = 0.000000001f;
    private float[] fusedOrientation = new float[3];
    private static final float FILTER_COEFFICIENT = 0.98f;
    private float[] mValuesOrientation = new float[3];
    private float[] mValuesOrientationOther = new float[3];

    // orientation angles from gyro matrix
    private float[] gyroOrientation = new float[3];
    private int width, height;
    private Display display;
    float angleX, angleY;
    private Typeface tfAngle;
    private int orientation;
    private boolean magnetoStatus = true;
    private boolean tabletStatus;

    private Profile profile;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        tabletStatus=isTablet(this);
        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        tfAngle = Typeface.createFromAsset(getAssets(), "helvetica.ttf");
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        Log.d("debug","Screen inches : " + screenInches);
        System.out.println("debugScreen inches : " + screenInches);
        centerX = width / 2;// convertDpToPixel(pixelCenterX, this);

        centerY = height / 2;// convertDpToPixel(pixelCenterY, this);
        mHandlerN = new Handler();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        d.setMaximumFractionDigits(3);
        d.setMinimumFractionDigits(3);
        gyroOrientation[0] = 0.0f;
        gyroOrientation[1] = 0.0f;
        gyroOrientation[2] = 0.0f;

        // initialise gyroMatrix with identity matrix
        gyroMatrix[0] = 1.0f;
        gyroMatrix[1] = 0.0f;
        gyroMatrix[2] = 0.0f;
        gyroMatrix[3] = 0.0f;
        gyroMatrix[4] = 1.0f;
        gyroMatrix[5] = 0.0f;
        gyroMatrix[6] = 0.0f;
        gyroMatrix[7] = 0.0f;
        gyroMatrix[8] = 1.0f;
        // Get an instance of the PowerManager
		/*
		 * fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
		 * 1000, TIME_CONSTANT);
		 */

        mSimulationView = new SimulationView(this);
        setContentView(mSimulationView);
        makeScreenAwake();

        Bundle bundle = this.getIntent().getExtras();
        profile = bundle.getParcelable(getString(R.string.profile));

        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                //timer.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                Intent myIntent = new Intent(getBaseContext(), mathTest.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(getString(R.string.profile), profile);
                myIntent.putExtras(bundle);
                startActivity(myIntent);
            }
        }.start();

    }

    @Override
    public void onDestroy() {
        if (this.mWakeLock != null) {
            this.mWakeLock.release();
        }
        super.onDestroy();
    }

    private void makeScreenAwake() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "My Tag");
        this.mWakeLock.acquire();
    }

    private float[] getRotationMatrixFromOrientation(float[] o) {
        float[] xM = new float[9];
        float[] yM = new float[9];
        float[] zM = new float[9];

        float sinX = (float) Math.sin(o[1]);
        float cosX = (float) Math.cos(o[1]);
        float sinY = (float) Math.sin(o[2]);
        float cosY = (float) Math.cos(o[2]);
        float sinZ = (float) Math.sin(o[0]);
        float cosZ = (float) Math.cos(o[0]);

        // rotation about x-axis (pitch)
        xM[0] = 1.0f;
        xM[1] = 0.0f;
        xM[2] = 0.0f;
        xM[3] = 0.0f;
        xM[4] = cosX;
        xM[5] = sinX;
        xM[6] = 0.0f;
        xM[7] = -sinX;
        xM[8] = cosX;

        // rotation about y-axis (roll)
        yM[0] = cosY;
        yM[1] = 0.0f;
        yM[2] = sinY;
        yM[3] = 0.0f;
        yM[4] = 1.0f;
        yM[5] = 0.0f;
        yM[6] = -sinY;
        yM[7] = 0.0f;
        yM[8] = cosY;

        // rotation about z-axis (azimuth)
        zM[0] = cosZ;
        zM[1] = sinZ;
        zM[2] = 0.0f;
        zM[3] = -sinZ;
        zM[4] = cosZ;
        zM[5] = 0.0f;
        zM[6] = 0.0f;
        zM[7] = 0.0f;
        zM[8] = 1.0f;

        // rotation order is y, x, z (roll, pitch, azimuth)
        float[] resultMatrix = matrixMultiplication(xM, yM);
        resultMatrix = matrixMultiplication(zM, resultMatrix);
        return resultMatrix;
    }

    private float[] matrixMultiplication(float[] A, float[] B) {
        float[] result = new float[9];

        result[0] = A[0] * B[0] + A[1] * B[3] + A[2] * B[6];
        result[1] = A[0] * B[1] + A[1] * B[4] + A[2] * B[7];
        result[2] = A[0] * B[2] + A[1] * B[5] + A[2] * B[8];

        result[3] = A[3] * B[0] + A[4] * B[3] + A[5] * B[6];
        result[4] = A[3] * B[1] + A[4] * B[4] + A[5] * B[7];
        result[5] = A[3] * B[2] + A[4] * B[5] + A[5] * B[8];

        result[6] = A[6] * B[0] + A[7] * B[3] + A[8] * B[6];
        result[7] = A[6] * B[1] + A[7] * B[4] + A[8] * B[7];
        result[8] = A[6] * B[2] + A[7] * B[5] + A[8] * B[8];

        return result;
    }

    class calculateFusedOrientationTask extends TimerTask {
        public void run() {
            float oneMinusCoeff = 1.0f - FILTER_COEFFICIENT;
            // System.out.println("running fused task...");
			/*
			 * Fix for 179? <--> -179? transition problem: Check whether one of
			 * the two orientation angles (gyro or accMag) is negative while the
			 * other one is positive. If so, add 360? (2 * math.PI) to the
			 * negative value, perform the sensor fusion, and remove the 360?
			 * from the result if it is greater than 180?. This stabilizes the
			 * output in positive-to-negative-transition cases.
			 */

            // azimuth
            if (gyroOrientation[0] < -0.5 * Math.PI
                    && accMagOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (FILTER_COEFFICIENT
                        * (gyroOrientation[0] + 2.0 * Math.PI) + oneMinusCoeff
                        * accMagOrientation[0]);
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else if (accMagOrientation[0] < -0.5 * Math.PI
                    && gyroOrientation[0] > 0.0) {
                fusedOrientation[0] = (float) (FILTER_COEFFICIENT
                        * gyroOrientation[0] + oneMinusCoeff
                        * (accMagOrientation[0] + 2.0 * Math.PI));
                fusedOrientation[0] -= (fusedOrientation[0] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else {
                fusedOrientation[0] = FILTER_COEFFICIENT * gyroOrientation[0]
                        + oneMinusCoeff * accMagOrientation[0];
            }

            // pitch
            if (gyroOrientation[1] < -0.5 * Math.PI
                    && accMagOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (FILTER_COEFFICIENT
                        * (gyroOrientation[1] + 2.0 * Math.PI) + oneMinusCoeff
                        * accMagOrientation[1]);
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else if (accMagOrientation[1] < -0.5 * Math.PI
                    && gyroOrientation[1] > 0.0) {
                fusedOrientation[1] = (float) (FILTER_COEFFICIENT
                        * gyroOrientation[1] + oneMinusCoeff
                        * (accMagOrientation[1] + 2.0 * Math.PI));
                fusedOrientation[1] -= (fusedOrientation[1] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else {
                fusedOrientation[1] = FILTER_COEFFICIENT * gyroOrientation[1]
                        + oneMinusCoeff * accMagOrientation[1];
            }

            // roll
            if (gyroOrientation[2] < -0.5 * Math.PI
                    && accMagOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (FILTER_COEFFICIENT
                        * (gyroOrientation[2] + 2.0 * Math.PI) + oneMinusCoeff
                        * accMagOrientation[2]);
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else if (accMagOrientation[2] < -0.5 * Math.PI
                    && gyroOrientation[2] > 0.0) {
                fusedOrientation[2] = (float) (FILTER_COEFFICIENT
                        * gyroOrientation[2] + oneMinusCoeff
                        * (accMagOrientation[2] + 2.0 * Math.PI));
                fusedOrientation[2] -= (fusedOrientation[2] > Math.PI) ? 2.0 * Math.PI
                        : 0;
            } else {
                fusedOrientation[2] = FILTER_COEFFICIENT * gyroOrientation[2]
                        + oneMinusCoeff * accMagOrientation[2];
            }

            // overwrite gyro matrix and orientation with fused orientation
            // to comensate gyro drift
            gyroMatrix = getRotationMatrixFromOrientation(fusedOrientation);
            System.arraycopy(fusedOrientation, 0, gyroOrientation, 0, 3);

            // update sensor output in GUI
            // umcomment if needed...
            // mHandler.post(updateOreintationDisplayTask);
        }
    }

    private Runnable updateOreintationDisplayTask = new Runnable() {
        public void run() {
            updateOreintationDisplay();
        }
    };

    public void updateOreintationDisplay() {
        // String s = d.format(accMagOrientation[1] * 180 / Math.PI);
        // String t = d.format(accMagOrientation[2] * 180 / Math.PI);

        String s, t;
        if (magnetoStatus) {
            s = d.format(mValuesOrientation[1] * 180 / Math.PI);
            t = d.format(mValuesOrientation[2] * 180 / Math.PI);
        } else {
            s = d.format(mValuesOrientationOther[0]);
            t = d.format(mValuesOrientationOther[1]*10);
            //System.out.println("here s..." + s);
            //System.out.println("here s..." + t);

        }
        //System.out.println("angle X....b4............." + angleX);
        System.out.println("here X..." + s);
        System.out.println("here Y..." + t);
        angleX = (float) (angleX * 180 / Math.PI);

        //System.out.println("angle X................." + angleX);

        ++orientation;
        // rotationMatrix
        xx = Float.parseFloat(s);
        yy = Float.parseFloat(t);
        if(tabletStatus){
            xx = Float.parseFloat(s);
            yy = Float.parseFloat(t);
        }else{
            yy = Float.parseFloat(s);
            xx = Float.parseFloat(t);
        }
        // System.out.println("sssssss" + s);
        System.out.println("xx" + xx);
        System.out.println("xxyy" + yy);

    }

    @Override
    protected void onResume() {
        super.onResume();

		/*
		 * when the activity is resumed, we acquire a wake-lock so that the
		 * screen stays on, since the user will likely not be fiddling with the
		 * screen or buttons.
		 */
        mWakeLock.acquire();

        // Start the simulation
        //fuseTimer = new Timer();
        //fuseTimer.scheduleAtFixedRate(new calculateFusedOrientationTask(),
        //1000, TIME_CONSTANT);
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();

		/*
		 * When the activity is paused, we make sure to stop the simulation,
		 * release our sensor resources and wake locks
		 */

        // Stop the simulation
        //fuseTimer.cancel();

        mSimulationView.stopSimulation();

        // and release our wake-lock
        mWakeLock.release();
    }
    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    class SimulationView extends View implements SensorEventListener {
        // diameter of the balls in meters

        private float sBallDiameter /* = 0.010f */;
        private float sBallDiameter2 /*
									 * = sBallDiameter sBallDiameter
									 */;

        // friction of the virtual table and air
        private static final float sFriction = 0.5f;

        private Sensor mAccelerometer, /* mGyro, */mMagneto;
        private long mLastT;
        private float mLastDeltaT;

        private float mXDpi;
        private float mYDpi;
        private float mMetersToPixelsX;
        private float mMetersToPixelsY;
        private Bitmap mBitmap;
        private Bitmap mWood;
        private float mXOrigin;
        private float mYOrigin;
        private Bitmap mWoodDial, mWoodDial1, mWoodDialZero;
        private Bitmap mBitmapOvel, mBitmapOvelAlter;
        private float mSensorX;
        private float mSensorY;
        private long mSensorTimeStamp;
        private long mCpuTimeStamp;
        private float mHorizontalBound;
        private float mVerticalBound;
        private final ParticleSystem mParticleSystem = new ParticleSystem();

        /*
         * Each of our particle holds its previous and current position, its
         * acceleration. for added realism each particle has its own friction
         * coefficient.
         */
        class Particle {
            private float mPosX;
            private float mPosY;
            private float mAccelX;
            private float mAccelY;
            private float mLastPosX;
            private float mLastPosY;
            private float mOneMinusFriction;

            Particle() {
                // make each particle a bit different by randomizing its
                // coefficient of friction
                final float r = ((float) Math.random() - 0.5f) * 0.2f;
                mOneMinusFriction = 1.0f - sFriction + r;
            }

            public void computePhysics(float sx, float sy, float dT, float dTC) {
                // Force of gravity applied to our virtual object
                final float m = 1000.0f; // mass of our virtual object
                final float gx = -sx * m;
                final float gy = -sy * m;

				/*
				 * ?F = mA <=> A = ?F / m We could simplify the code by
				 * completely eliminating "m" (the mass) from all the equations,
				 * but it would hide the concepts from this sample code.
				 */
                final float invm = 1.0f / m;
                final float ax = gx * invm;
                final float ay = gy * invm;

				/*
				 * Time-corrected Verlet integration The position Verlet
				 * integrator is defined as x(t+?t) = x(t) + x(t) - x(t-?t) +
				 * a(t)?t?2 However, the above equation doesn't handle variable
				 * ?t very well, a time-corrected version is needed: x(t+?t) =
				 * x(t) + (x(t) - x(t-?t)) * (?t/?t_prev) + a(t)?t?2 We also add
				 * a simple friction term (f) to the equation: x(t+?t) = x(t) +
				 * (1-f) * (x(t) - x(t-?t)) * (?t/?t_prev) + a(t)?t?2
				 */
                final float dTdT = dT * dT;
                //System.out.println("mposX" + mPosX);

                final float x = mPosX + mOneMinusFriction * dTC
                        * (mPosX - mLastPosX) + mAccelX * dTdT;
                final float y = mPosY + mOneMinusFriction * dTC
                        * (mPosY - mLastPosY) + mAccelY * dTdT;
                mLastPosX = mPosX;
                // System.out.println("mposX....x" + x);

                mLastPosY = mPosY;
                mPosX = x;
                mPosY = y;
                mAccelX = ax;
                mAccelY = ay;
            }

            /*
             * Resolving constraints and collisions with the Verlet integrator
             * can be very simple, we simply need to move a colliding or
             * constrained particle in such way that the constraint is
             * satisfied.
             */
            public void resolveCollisionWithBounds() {
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;
                } else if (x < -xmax) {
                    mPosX = -xmax;
                }
                if (y > ymax) {
                    mPosY = ymax;
                } else if (y < -ymax) {
                    mPosY = -ymax;
                }
            }
        }

        /*
         * A particle system is just a collection of particles
         */
        class ParticleSystem {
            static final int NUM_PARTICLES = 1;
            private Particle mBalls[] = new Particle[NUM_PARTICLES];

            ParticleSystem() {

				/*
				 * Initially our particles have no speed or acceleration
				 */
                for (int i = 0; i < mBalls.length; i++) {
                    mBalls[i] = new Particle();
                }
            }

            /*
             * Update the position of each particle in the system using the
             * Verlet integrator.
             */
            private void updatePositions(float sx, float sy, long timestamp) {
                final long t = timestamp;
                if (mLastT != 0) {
                    final float dT = (float) (t - mLastT)
                            * (1.0f / 1000000000.0f);
                    if (mLastDeltaT != 0) {
                        final float dTC = dT / mLastDeltaT;
                        final int count = mBalls.length;
                        for (int i = 0; i < count; i++) {
                            Particle ball = mBalls[i];
                            ball.computePhysics(sx, sy, dT, dTC);
                        }
                    }
                    mLastDeltaT = dT;
                }
                mLastT = t;
            }

            /*
             * Performs one iteration of the simulation. First updating the
             * position of all the particles and resolving the constraints and
             * collisions.
             */
            public void update(float sx, float sy, long now) {
                // update the system's positions
                updatePositions(sx, sy, now);

                // We do no more than a limited number of iterations
                final int NUM_MAX_ITERATIONS = 10;

				/*
				 * Resolve collisions, each particle is tested against every
				 * other particle for collision. If a collision is detected the
				 * particle is moved away using a virtual spring of infinite
				 * stiffness.
				 */
                boolean more = true;
                final int count = mBalls.length;
                for (int k = 0; k < NUM_MAX_ITERATIONS && more; k++) {
                    more = false;
                    for (int i = 0; i < count; i++) {
                        Particle curr = mBalls[i];
                        for (int j = i + 1; j < count; j++) {
                            Particle ball = mBalls[j];
                            float dx = ball.mPosX - curr.mPosX;
                            float dy = ball.mPosY - curr.mPosY;
                            float dd = dx * dx + dy * dy;
                            // Check for collisions
                            if (dd <= sBallDiameter2) {

								/*
								 * add a little bit of entropy, after nothing is
								 * perfect in the universe.
								 */
                                dx += ((float) Math.random() - 0.5f) * 0.0001f;
                                dy += ((float) Math.random() - 0.5f) * 0.0001f;
                                dd = dx * dx + dy * dy;
                                // simulate the spring
                                final float d = (float) Math.sqrt(dd);
                                final float c = (0.5f * (sBallDiameter - d))
                                        / d;
                                curr.mPosX -= dx * c;
                                curr.mPosY -= dy * c;
                                ball.mPosX += dx * c;
                                ball.mPosY += dy * c;
                                more = true;
                            }
                        }

						/*
						 * Finally make sure the particle doesn't intersects
						 * with the walls.
						 */
                        curr.resolveCollisionWithBounds();
                    }
                }
            }

            public int getParticleCount() {
                return mBalls.length;
            }

            public float getPosX(int i) {
                return mBalls[i].mPosX;
            }

            public float getPosY(int i) {
                return mBalls[i].mPosY;
            }
        }

        public void startSimulation() {

			/*
			 * It is not necessary to get accelerometer events at a very high
			 * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
			 * automatic low-pass filter, which "extracts" the gravity component
			 * of the acceleration. As an added benefit, we use less power and
			 * CPU resources.
			 */

			/*
			 * mSensorManager.registerListener(this, mGyro,
			 * SensorManager.SENSOR_DELAY_UI);
			 */

            mSensorManager.registerListener(this, mAccelerometer,
                    SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(this, mMagneto,
                    SensorManager.SENSOR_DELAY_UI);
        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

        public SimulationView(Context context) {
            super(context);
            if (height <= 320) {
                sBallDiameter = 0.010f / 2.2f;
            } else if (height <= 480) {
                sBallDiameter = 0.010f / 1.85f;
                // sBallDiameter = 0.004f;
            }else if (height <= 800) {
                sBallDiameter = 0.010f/1.05f;
                // sBallDiameter = 0.004f;
            }
            else if (height <= 1280 && width <= 720) {
                if(tabletStatus)
                {
                    sBallDiameter = 0.014f;
                }else{
                    sBallDiameter = 0.010f/1.45f;
                }
            } else {
                sBallDiameter = 0.015f;
            }
            sBallDiameter2 = sBallDiameter * sBallDiameter;
            mAccelerometer = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            // mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            mMagneto = mSensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mXDpi = metrics.xdpi;
            mYDpi = metrics.ydpi;
            // System.out.println("mXDi" + mXDpi);
            // System.out.println("mXDi" + mYDpi);
            // String all = getDisplayMetrics(HandyLevelFreeActivity.this);
            // System.out.println("alll" + all);
            mMetersToPixelsX = mXDpi / 0.0254f;
            mMetersToPixelsY = mYDpi / 0.0254f;

            // rescale the ball so it's about 0.5 cm on screen
            Bitmap ball = BitmapFactory.decodeResource(getResources(),
                    R.drawable.bubble);
            Bitmap ballOvel = BitmapFactory.decodeResource(getResources(),
                    R.drawable.owel_bubble);
            final int dstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            final int dstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
            mBitmap = Bitmap
                    .createScaledBitmap(ball, dstWidth, dstHeight, true);
            if (height <= 320) {
                mBitmapOvel = Bitmap.createScaledBitmap(ballOvel, dstWidth,
                        dstHeight - 6, true);
                mBitmapOvelAlter = Bitmap.createScaledBitmap(ballOvel,
                        dstWidth - 6, dstHeight, true);
            } else if (height <= 480) {
                mBitmapOvel = Bitmap.createScaledBitmap(ballOvel, dstWidth,
                        dstHeight - 12, true);
                mBitmapOvelAlter = Bitmap.createScaledBitmap(ballOvel,
                        dstWidth - 12, dstHeight, true);
            } else {
                mBitmapOvel = Bitmap.createScaledBitmap(ballOvel, dstWidth,
                        dstHeight - 42, true);
                mBitmapOvelAlter = Bitmap.createScaledBitmap(ballOvel,
                        dstWidth - 42, dstHeight, true);
            }
            Options opts = new Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bg = BitmapFactory.decodeResource(getResources(),
                    R.drawable.bg);
            mWood = Bitmap.createScaledBitmap(bg, width, height, true);
            Bitmap bgDial = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dail);
            Bitmap bgDial1 = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dail3);
            Bitmap bgDialZero = BitmapFactory.decodeResource(getResources(),
                    R.drawable.dail4);
            mWoodDial = Bitmap.createScaledBitmap(bgDial, width - 20,
                    width - 20, true);
            mWoodDial1 = Bitmap.createScaledBitmap(bgDial1, width - 20,
                    width - 20, true);
            mWoodDialZero = Bitmap.createScaledBitmap(bgDialZero, width - 20,
                    width - 20, true);

        }

        public String getDisplayMetrics(Context cx) {
            String str = "";
            DisplayMetrics dm = new DisplayMetrics();
            dm = cx.getApplicationContext().getResources().getDisplayMetrics();
            int screenWidth = dm.widthPixels;
            int screenHeight = dm.heightPixels;
            float density = dm.density;
            float xdpi = dm.xdpi;
            float ydpi = dm.ydpi;
            str += "The absolute width:" + String.valueOf(screenWidth)
                    + "pixels,";
            str += "The absolute heightin:" + String.valueOf(screenHeight)
                    + "pixels,";
            str += "The logical density of the display.:"
                    + String.valueOf(density) + ",";
            str += "X dimension :" + String.valueOf(xdpi) + "pixels per inch,";
            str += "Y dimension :" + String.valueOf(ydpi) + "pixels per inch,";
            return str;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
            mXOrigin = (w - mBitmap.getWidth()) * 0.5f;
            mYOrigin = (h - mBitmap.getHeight()) * 0.5f + 30f;
            mHorizontalBound = (((w - 180) / mMetersToPixelsX - sBallDiameter) * 0.5f);
            mVerticalBound = (((100) / mMetersToPixelsY - sBallDiameter) * 0.5f);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    // copy new accelerometer data into accel array and calculate
                    // orientation
                    System.arraycopy(event.values, 0, accel, 0, 3);
				/*
				 * if (event.values[1] < 6.5 && event.values[1] > -6.5) { if
				 * (orientation != 1) { Log.d("Sensor", "Landscape"); }
				 * orientation = 1; } else { if (orientation != 0) {
				 * Log.d("Sensor", "Portrait"); } orientation = 0; }
				 */
                    // can be deleted..start....
                    // float x,y,z;
                    // /float op[]=new float[3];
                    // x=event.values[0];
                    // y=event.values[1];
                    // z=event.values[2];
                    lowPass(accel, mValuesOrientationOther);

                    // mValuesOrientationOther=op;
                    // float accelationSquareRoot = (op[0] * op[0] + op[1] * op[1] +
                    // op[2] * op[2] )
                    // / (SensorManager.GRAVITY_EARTH *
                    // SensorManager.GRAVITY_EARTH);
                    // System.out.println("accelationSquareRoot"+accelationSquareRoot);
                    // can be deleted...end.
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    System.out.println("mSensonrX....."+mSensorX);
                    System.out.println("mSensonrY....."+mSensorY);

                    calculateAccMagOrientation();
                    break;

                case Sensor.TYPE_GYROSCOPE:
                    // process gyro data
                    gyroFunction(event);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    // copy new magnetometer data into magnet array
                    System.arraycopy(event.values, 0, magnet, 0, 3);
                    break;

            }
            mHandlerN.post(updateOreintationDisplayTask);
            // updateOreintationDisplaySV();

            mSensorTimeStamp = event.timestamp;
            mCpuTimeStamp = System.nanoTime();
        }

        public void gyroFunction(SensorEvent event) {
            // don't start until first accelerometer/magnetometer orientation
            // has been acquired
            if (accMagOrientation == null)
                return;

            // initialisation of the gyroscope based rotation matrix
            if (initState) {
                float[] initMatrix = new float[9];
                initMatrix = getRotationMatrixFromOrientation(accMagOrientation);
                float[] test = new float[3];
                SensorManager.getOrientation(initMatrix, test);
                gyroMatrix = matrixMultiplication(gyroMatrix, initMatrix);
                initState = false;
            }

            // copy the new gyro values into the gyro array
            // convert the raw gyro data into a rotation vector
            float[] deltaVector = new float[4];
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                System.arraycopy(event.values, 0, gyro, 0, 3);
                getRotationVectorFromGyro(gyro, deltaVector, dT / 2.0f);
            }

            // measurement done, save current time for next interval
            timestamp = event.timestamp;

            // convert rotation vector into rotation matrix
            float[] deltaMatrix = new float[9];
            // SensorManager.getRotationMatrixFromVector(deltaMatrix,
            // deltaVector);

            // apply the new rotation interval on the gyroscope based rotation
            // matrix
            gyroMatrix = matrixMultiplication(gyroMatrix, deltaMatrix);

            // get the gyroscope based orientation from the rotation matrix
            SensorManager.getOrientation(gyroMatrix, gyroOrientation);
        }

        private void getRotationVectorFromGyro(float[] gyroValues,
                                               float[] deltaRotationVector, float timeFactor) {
            float[] normValues = new float[3];

            // Calculate the angular speed of the sample
            float omegaMagnitude = (float) Math.sqrt(gyroValues[0]
                    * gyroValues[0] + gyroValues[1] * gyroValues[1]
                    + gyroValues[2] * gyroValues[2]);

            // Normalize the rotation vector if it's big enough to get the axis
            if (omegaMagnitude > EPSILON) {
                normValues[0] = gyroValues[0] / omegaMagnitude;
                normValues[1] = gyroValues[1] / omegaMagnitude;
                normValues[2] = gyroValues[2] / omegaMagnitude;
            }

            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the
            // timestep
            // We will convert this axis-angle representation of the delta
            // rotation
            // into a quaternion before turning it into the rotation matrix.
            float thetaOverTwo = omegaMagnitude * timeFactor;
            float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
            float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);
            deltaRotationVector[0] = sinThetaOverTwo * normValues[0];
            deltaRotationVector[1] = sinThetaOverTwo * normValues[1];
            deltaRotationVector[2] = sinThetaOverTwo * normValues[2];
            deltaRotationVector[3] = cosThetaOverTwo;
        }

        // public void updateOreintationDisplaySV() {
        // //mSensorX = xx;
        // //mSensorY = yy;
        // }

        public void calculateAccMagOrientation() {

            // mValuesOrientation = lowPass(accel, mValuesOrientation);

            if (SensorManager.getRotationMatrix(rotationMatrix, null, accel,
                    magnet)) {
                // SensorManager.getOrientation(rotationMatrix,
                // accMagOrientation);
                SensorManager.remapCoordinateSystem(rotationMatrix,
                        SensorManager.AXIS_X, SensorManager.AXIS_Y,
                        mValuesOrientation);
                SensorManager
                        .getOrientation(rotationMatrix, mValuesOrientation);
                // mValuesOrientation[1]=(float) Math.atan2(accel[2],accel[1]);
                // mValuesOrientation[2]=(float) Math.atan2(accel[2],accel[1]);
                angleX = (float) Math.atan2(mSensorY, mSensorX);
                // angleX=angleX
                // System.out.println("angleX:::::::::::"+angleX);
                magnetoStatus = true;
            } else {
                // if (magnet[0] <= 0) System.out.println("herr.....");
                final float alpha = 0.5f;
                float gravity[] = new float[3];

                gravity[0] = alpha * gravity[0] + (1 - alpha) * accel[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * accel[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * accel[2];

                // mValuesOrientation[0] = accel[0] - gravity[0];
                // mValuesOrientation[1] = accel[1] - gravity[1];
                // mValuesOrientation[2] = accel[2] - gravity[2];
                magnetoStatus = false;
                // angleX = (float) Math.atan2(mSensorY, mSensorX);
                // mValuesOrientation[2] = (float) Math.atan2(accel[0],
                // accel[1]);
                // mValuesOrientation = lowPass(accel, mValuesOrientation);
                // mValuesOrientation[2]=angleX;
                // mValuesOrientation[1]=accel[0];

            }
        }

        float ALPHA = 0.3f;

        protected float[] lowPass(float[] input, float[] output) {
            if (output == null)
                return input;
            if (xx < 1 || yy < 1) {

            } else if (xx < 5 || yy < 5) {
                ALPHA = .35f;
            } else if (xx < 12 || yy < 12) {
                ALPHA = .4f;
            }
            for (int i = 0; i < input.length; i++) {
                ALPHA=1;
                output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }

        @Override
        protected void onDraw(Canvas canvas) {

			/*
			 * draw the background
			 */

            canvas.drawBitmap(mWood, 0, 0, null);

			/*
			 * compute the new position of our object, based on accelerometer
			 * data and present time.
			 */

            final ParticleSystem particleSystem = mParticleSystem;
            final long now = mSensorTimeStamp
                    + (System.nanoTime() - mCpuTimeStamp);
            final float sx = mSensorX;
            final float sy = mSensorY;
            //System.out.println("SX" + sx);
            //System.out.println("SY" + sy);

            particleSystem.update(sx, sy, now);

            final float xc = mXOrigin;
            final float yc = mYOrigin;
            final float xs = mMetersToPixelsX;
            final float ys = mMetersToPixelsY;
            final Bitmap bitmap = mBitmap;
            final Bitmap bitmapOvel = mBitmapOvel;
            // final int count = particleSystem.getParticleCount();

			/*
			 * for (int i = 0; i < 1; i++) { canvas.drawCircle(centerX, centerY,
			 * centerX - 10, p); }
			 */

            canvas.drawBitmap(mWoodDial, 10, centerY - centerX + 20, null);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setStyle(Style.FILL_AND_STROKE);
            // p.setTextScaleX(2f);
            p.setTypeface(tfAngle);
            if (height <= 320) {
                p.setTextSize(18f);
            } else if (height <= 480) {
                p.setTextSize(32f);
            } else {
                p.setTextSize(44f);
            }

            if (yy > 0) {
                if (yy > 90) {
                    yy = 180 - yy;
                }
            } else {
                if (yy < -90) {
                    yy = -180 - yy;
                }
            }
            String ssAngle;


            if (xx > 0) {

                ssAngle = Html.fromHtml("+" + (int) xx + "&#176").toString();
            } else {
                ssAngle = Html.fromHtml((int) xx + "&#176").toString();

            }
            String ssx = Html.fromHtml("X:" + (int) xx + "&#176").toString();

            String ssy = Html.fromHtml("Y:" + (int) yy + "&#176").toString();

            if (height <= 320) {
                if ((Math.abs(xx) + Math.abs(yy)) < -50
                        || (Math.abs(xx) + Math.abs(yy)) > 50) {
                    canvas.drawText(ssAngle, centerX - 25, centerY - 15, p);
                } else {
                    canvas.drawText(ssx, centerX - 25, centerY - 15, p);
                    canvas.drawText(ssy, centerX - 25, centerY + 45, p);

                }
            } else if (height <= 480) {
                if (xx < -50 || xx > 50) {
                    canvas.drawText(ssx + ssy, centerX - 35, centerY - 35, p);
                } else {
                    canvas.drawText(ssx, centerX - 35, centerY - 35, p);
                    canvas.drawText(ssy, centerX - 35, centerY + 75, p);
                }
            }else if (height <= 800) {
                if (xx < -50 || xx > 50) {
                    canvas.drawText(ssx + ssy, centerX - 40, centerY - 40, p);
                } else {
                    canvas.drawText(ssx, centerX - 40, centerY - 40, p);
                    canvas.drawText(ssy, centerX - 40, centerY + 85, p);
                }
            }
            else {
                if (yy < -50 || yy > 50) {
                    canvas.drawText(ssAngle, centerX - 45, centerY - 85, p);
                } else {
                    if(tabletStatus)
                    {
                        canvas.drawText(ssx, centerX - 45, centerY - 85, p);
                        canvas.drawText(ssy, centerX - 45, centerY + 125, p);
                    }
                    else{
                        canvas.drawText(ssy, centerX - 45, centerY - 85, p);
                        canvas.drawText(ssx, centerX - 45, centerY + 125, p);

                    }
                }
            }

            // for (int i = 0; i < count; i++) {

			/*
			 * We transform the canvas so that the coordinate system matches the
			 * sensors coordinate system with the origin in the center of the
			 * screen and the unit is the meter.
			 */

            final float x = xc + particleSystem.getPosX(0) * xs;
            final float y = yc - particleSystem.getPosY(0) * ys * .5f;
            System.out.println("X" + x);
            System.out.println("Y" + y);

            boolean status = true;
            if (height <= 480) {
                if (yy >= 57 || yy <= -70) {
                    status = false;

                }
            }else if (height <= 800) {
                if (xx >= 41 || xx <= -45) {
                    status = false;
                }
            }
            else if (height <= 1232) {
                if (xx >= 75 || xx <= -80) {
                    status = false;
                }
            } else {
                if (xx >= 65 || xx <= -70) {
                    status = false;
                }
            }
            if (height <= 320) {

                if (status) {
                    if (xx > 33f) {
                        canvas.drawBitmap(bitmapOvel, centerX + yy * .6f - 8f,
                                centerY + 10 * .6f, null);
                    } else if (xx < -30f) {
                        canvas.drawBitmap(bitmapOvel, centerX + yy * .6f - 8f,
                                centerY - 33 * .25f, null);
                    } else {
                        canvas.drawBitmap(bitmap, centerX + yy * .6f - 8f,
                                centerY - 5 * .4f, null);
                    }
                } else {
                    if (xx > 33f) {
                        if (yy < -70) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 74 * .6f - 8f, centerY + 5 * .4f,
                                    null);
                        } else if (yy >= 57) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 70 * .6f - 8f, centerY - 5 * .4f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter, centerX + yy
                                    * .6f - 8f, centerY + 35 * .25f + 6f, null);

                        }
                    } else if (xx < -30f) {
                        if (yy < -70) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * .6f, centerY - 33 * .25f,
                                    null);
                            //System.out.println(" this time 22 xx:"					+ (centerX - 78 * .4f) + "::..  yy:"
                            //	+ (centerY + 30 * .6f + 6f));

                        } else if (yy > 57) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 57 * .6f - 4f,
                                    centerY - 37 * .25f, null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter, centerX + yy
                                    * .6f - 8f, centerY - 33 * .25f + 6, null);

                        }
                    } else {
                        if (yy > -70 && yy < 57) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 5 * .6f - 8f, centerY - 5 * .4f,
                                    null);
                        } else {
                            if (yy < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * .6f, centerY - 5 * .4f,
                                        null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 55 * .6f, centerY - 5 * .4f,
                                        null);
                            }

                        }
                    }
                }
            } else if (height <= 480) {
                if (status) {
                    if (xx > 20f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 10f, centerY - 15, null);
                    } else if (xx < -12f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 10f, centerY, null);
                    } else {
                        canvas.drawBitmap(bitmap, centerX + xx * 1.7f - 15f,
                                centerY - yy - 5f, null);
                    }
                } else {
                    if (xx > 20f) {
                        if (yy < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.2f - 10f, centerY - 15f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.2f - 5f, centerY - 10f,
                                    null);
                        }
                    } else if (xx < -12f) {
                        if (yy < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.2f - 10f, centerY - 15f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.2f - 10f, centerY - 15f,
                                    null);
                        }
                    } else {
                        if (yy < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.2f - 10f, centerY - yy,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.2f - 10f, centerY - yy,
                                    null);
                        }
                    }
                }
                // canvas.drawBitmap(bitmapOvel, centerX + xx * 1.2f - 10f,
                // centerY-yy, null);
            }
            else if((height <= 800)){
                if (status) {
                    if (yy > 9f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 30f, centerY - 20, null);
                    } else if (yy < -11f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 30f, centerY + 25f, null);
                    } else {
                        canvas.drawBitmap(bitmap, centerX + xx * 1.7f - 25f,
                                centerY - yy * .85f - 20f, null);
                    }
                } else {
                    if (yy > 9f) {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 50 * 1.7f - 20f, centerY - 25f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 50 * 1.7f , centerY - 20f,
                                    null);
                        }
                    } else if (yy < -11f) {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 50 * 1.7f - 20f, centerY - 25f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 50 * 1.7f, centerY - 25f,
                                    null);
                        }
                    } else {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 50 * 1.7f - 18f, centerY - 20f
                                            - yy, null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 50 * 1.7f, centerY - 20f
                                            - yy, null);
                        }
                    }
                }
            }
            // large size....
            else if (height <= 1232) {
                if (status) {
                    if (yy > 12f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 40f, centerY - 45, null);
                    } else if (yy < -12f) {
                        canvas.drawBitmap(bitmapOvel,
                                centerX + xx * 1.7f - 40f, centerY + 15f, null);
                    } else {
                        canvas.drawBitmap(bitmap, centerX + xx * 1.7f - 45f,
                                centerY - yy - 35f, null);
                    }
                } else {
                    if (yy > 12f) {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.7f - 40f, centerY - 45f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.7f - 15f, centerY - 45f,
                                    null);
                        }
                    } else if (yy < -12f) {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.7f - 40f, centerY - 25f,
                                    null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.7f - 10f, centerY - 25f,
                                    null);
                        }
                    } else {
                        if (xx < 0) {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX - 80 * 1.7f - 40f, centerY - 30f
                                            - yy, null);
                        } else {
                            canvas.drawBitmap(mBitmapOvelAlter,
                                    centerX + 80 * 1.7f - 15f, centerY - 30f
                                            - yy, null);
                        }
                    }
                }
            }
            // for s3
            else {
                if(tabletStatus)
                {
                    if (status) {
                        if (yy > 10f) {
                            canvas.drawBitmap(bitmapOvel,
                                    centerX + xx * 1.7f - 40f, centerY - 40, null);
                        } else if (yy < -12f) {
                            canvas.drawBitmap(bitmapOvel,
                                    centerX + xx * 1.7f - 40f, centerY + 15f, null);
                        } else {
                            canvas.drawBitmap(bitmap, centerX + xx * 1.7f - 45f,
                                    centerY - yy * .85f - 30f, null);
                        }
                    } else {
                        if (yy > 10f) {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 20f, centerY - 35f,
                                        null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 20f, centerY - 40f,
                                        null);
                            }
                        } else if (yy < -12f) {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 25f, centerY - 25f,
                                        null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 25f, centerY - 25f,
                                        null);
                            }
                        } else {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 25f, centerY - 30f
                                                - yy, null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 20f, centerY - 30f
                                                - yy, null);
                            }
                        }
                    }}
                else{

                    System.out.println("coming here for phone ...s3");
                    if (status) {
                        if (yy > 10f) {
                            canvas.drawBitmap(bitmapOvel,
                                    centerX + xx * 1.7f - 40f, centerY - 40, null);
                        } else if (yy < -12f) {
                            canvas.drawBitmap(bitmapOvel,
                                    centerX + xx * 1.7f - 40f, centerY + 15f, null);
                        } else {
                            canvas.drawBitmap(bitmap, centerX + xx * 1.7f - 45f,
                                    centerY - yy * .85f - 30f, null);
                        }
                    } else {
                        if (yy > 10f) {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 20f, centerY - 35f,
                                        null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 20f, centerY - 40f,
                                        null);
                            }
                        } else if (yy < -12f) {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 25f, centerY - 25f,
                                        null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 25f, centerY - 25f,
                                        null);
                            }
                        } else {
                            if (xx < 0) {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX - 80 * 1.7f - 25f, centerY - 30f
                                                - yy, null);
                            } else {
                                canvas.drawBitmap(mBitmapOvelAlter,
                                        centerX + 80 * 1.7f - 20f, centerY - 30f
                                                - yy, null);
                            }
                        }
                    }
                }
            }
            // y = 0.52(10-30)+25
			/*
			 * float angleInRad=(float) ((xx)*Math.PI/180);
			 * System.out.println("angle i rad.."+angleInRad); float
			 * reqY=angleInRad*(2*centerX-10-xx)+yy;
			 * System.out.println("req y.."+reqY);
			 *
			 * canvas.drawLine(centerX, centerY+10, 2*centerX-10, reqY+centerY,
			 * p);
			 */
			/*
			 * if (xx <= 0) { canvas.rotate(xx, centerX, centerY); } else {
			 */
            canvas.save();

            if (height <= 320) {
                int other = (int) yy;
                yy = other;
                if (xx < 0) {
                    canvas.rotate(udc.angleCasting(xx)/* xx */, centerX, 170.5f);
                } else {
                    canvas.rotate(180 + udc.angleCasting(xx)/* xx */, centerX,
                            170.5f);
                }
            } else if (height <= 480) {
                canvas.rotate(udc.angleCasting(yy), centerX, centerY + 10f);
            } else if (height == 1280) {
                // canvas.rotate(yy, centerX, centerY);
                if (yy < 0) {
                    canvas.rotate(180 + udc.angleCasting(xx)/*
															 * udc.angleCasting(xx
															 * )
															 */,
                            centerX + .72f, centerY + 10.54f);
                } else {
                    canvas.rotate(udc.angleCasting(xx)/* udc.angleCasting(xx) */,
                            centerX + .72f, centerY + 10.54f);
                }

            } else {
                // canvas.rotate(yy, centerX, centerY);
                if (yy < 0) {
                    canvas.rotate(180 + udc.angleCasting(xx)/*
															 * udc.angleCasting(xx
															 * )
															 */, centerX,
                            centerY + 9.5f);
                } else {
                    canvas.rotate(udc.angleCasting(xx)/* udc.angleCasting(xx) */,
                            centerX, centerY + 9.5f);

                }
            }

            p.setAntiAlias(true);
            p.setFilterBitmap(true);
            p.setDither(true);
            boolean imageStatus = ((int) udc.angleCasting(xx) == 0 && (int) udc
                    .angleCasting(yy) == 0)
                    || (int) udc.angleCasting(xx) == 90
                    && (int) udc.angleCasting(yy) == 90;
            if (height <= 480) {
                if (imageStatus
                        || ((int) udc.angleCasting(xx) == 0 && (int) udc
                        .angleCasting(yy) == 90)
                        || ((int) udc.angleCasting(xx) >= 80 && (int) udc
                        .angleCasting(yy) == 0)) {
                    canvas.drawBitmap(mWoodDialZero, 10,
                            centerY - centerX + 20, p);
                } else if ((Math.abs(xx) + Math.abs(yy)) < -50
                        || (Math.abs(xx) + Math.abs(yy)) > 50) {
                    canvas
                            .drawBitmap(mWoodDial1, 10, centerY - centerX + 20,
                                    p);
                }
            } else {
                if (imageStatus
                        || ((int) udc.angleCasting(xx) == 0 && (int) udc
                        .angleCasting(yy) == 90)
                        || ((int) udc.angleCasting(xx) == 90 && (int) udc
                        .angleCasting(yy) == 0)) {
                    canvas.drawBitmap(mWoodDialZero, 10,
                            centerY - centerX + 20, p);
                } else if (yy < -50 || yy > 50) {
                    if (((int) udc.angleCasting(xx) == 0 || (int) udc
                            .angleCasting(xx) == 90)) {
                        canvas.drawBitmap(mWoodDialZero, 10, centerY - centerX
                                + 20, p);
                    } else {
                        canvas.drawBitmap(mWoodDial1, 10, centerY - centerX
                                + 20, p);
                    }
                }
            }
            canvas.restore();
            //System.out.println("xx:" + xx + "::..  yy:" + yy);

            // }
            // and make sure to redraw asap
            invalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

}

/*
 * public static float convertDpToPixel(float dp, Context context) { Resources
 * resources = context.getResources(); DisplayMetrics metrics =
 * resources.getDisplayMetrics(); float px = dp * (metrics.densityDpi / 160f);
 * return px; }
 */

/**
 * This method converts device specific pixels to device independent pixels.
 *
 * @param px
 *            A value in px (pixels) unit. Which we need to convert into db
 * @param context
 *            Context to get resources and device specific display metrics
 * @return A float value to represent db equivalent to px value
 */
/*
 * public static float convertPixelsToDp(float px, Context context) { Resources
 * resources = context.getResources(); DisplayMetrics metrics =
 * resources.getDisplayMetrics(); float dp = px / (metrics.densityDpi / 160f);
 * return dp;
 *
 * } }
 */