package com.silva.lucas.projetoopencv;
import android.app.Activity;

import org.opencv.android.BaseLoaderCallback;
        import org.opencv.android.LoaderCallbackInterface;
        import org.opencv.android.OpenCVLoader;

        import android.app.Activity;
        import android.os.Bundle;
        import android.util.Log;

public class GerenciadorOpenCv extends Activity
{

    final String TAG = "Hello World";

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    // Create and set View
                    setContentView(R.layout.activity_main);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_2, this, mOpenCVCallBack))
        {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
    }
}