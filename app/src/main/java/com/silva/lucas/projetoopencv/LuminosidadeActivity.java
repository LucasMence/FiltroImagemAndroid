package com.silva.lucas.projetoopencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.ByteArrayOutputStream;

public class LuminosidadeActivity extends AppCompatActivity  implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "LuminosidadeActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV nao carregado");
        } else {
            Log.d(TAG, "OpenCV carregado");
        }
    }

    private ImageView imgImagem;
    private AppCompatSeekBar volLuminosidade;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luminosidade);

        imgImagem = (ImageView)findViewById(R.id.imgImagem);
        volLuminosidade = (AppCompatSeekBar)findViewById(R.id.volLuminosidade);
        volLuminosidade.setOnSeekBarChangeListener(this);

        byte[] byteArray = getIntent().getByteArrayExtra("foto");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgImagem.setImageBitmap(bmp);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
        if(bitmapDrawable==null){
            imgImagem.buildDrawingCache();
            bitmap = imgImagem.getDrawingCache();
            imgImagem.buildDrawingCache(false);
        }else
        {
            bitmap = bitmapDrawable.getBitmap();
        }

        Button btnAplicar = (Button) findViewById(R.id.btnAplicar);
        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResultado = new Intent();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
                if(bitmapDrawable==null){
                    imgImagem.buildDrawingCache();
                    bitmap = imgImagem.getDrawingCache();
                    imgImagem.buildDrawingCache(false);
                }else
                {
                    bitmap = bitmapDrawable.getBitmap();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Bundle pacoteResultado = new Bundle();

                pacoteResultado.putByteArray("foto",byteArray);

                intentResultado.putExtras(pacoteResultado);
                setResult(Activity.RESULT_OK, intentResultado);
                finish();
            }
        });

    }

    private Bitmap increaseBrightness(Bitmap bitmap, int value){

        Mat src = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bitmap,src);
        src.convertTo(src,-1,1,value);
        Bitmap result = Bitmap.createBitmap(src.cols(),src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src,result);
        return result;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        Bitmap edited = increaseBrightness(bitmap,progress);
        imgImagem.setImageBitmap(edited);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
