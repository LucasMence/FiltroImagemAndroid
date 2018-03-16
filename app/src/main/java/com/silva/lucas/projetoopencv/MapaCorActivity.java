package com.silva.lucas.projetoopencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

public class MapaCorActivity extends AppCompatActivity {

    Spinner spnFiltro;
    ImageView imgImagem;
    Button btnAplicar, btnCancelar;

    private static final String TAG = "MapaCorActivity";

    static {
        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV nao carregado");
        } else {
            Log.d(TAG, "OpenCV carregado");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_cor);

        spnFiltro = (Spinner) findViewById(R.id.spnFiltro);
        imgImagem = (ImageView) findViewById(R.id.imgImagem);
        btnAplicar = (Button) findViewById(R.id.btnAplicar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);

        String[] items = new String[]{"","Outono", "Osso", "Frio", "Quente", "Cromatico", "Deteccao de Calor", "Oceano", "Parula", "Rosa", "Arco-Iris", "Primavera", "Verão", "Inverno"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spnFiltro.setAdapter(adapter);

        byte[] byteArray = getIntent().getByteArrayExtra("foto");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgImagem.setImageBitmap(bmp);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResultado = new Intent();
                setResult(Activity.RESULT_CANCELED, intentResultado);
                finish();
            }
        });

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentResultado = new Intent();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
                Bitmap bitmapResultado;
                if(bitmapDrawable==null){
                    imgImagem.buildDrawingCache();
                    bitmapResultado = imgImagem.getDrawingCache();
                    imgImagem.buildDrawingCache(false);
                }else
                {
                    bitmapResultado = bitmapDrawable.getBitmap();
                }
                bitmapResultado.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Bundle pacoteResultado = new Bundle();

                pacoteResultado.putByteArray("foto",byteArray);

                intentResultado.putExtras(pacoteResultado);
                setResult(Activity.RESULT_OK, intentResultado);
                finish();
            }
        });

        spnFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorFontButton));
                AplicarFiltroNaImagem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    void AplicarFiltroNaImagem(int ATipo){
        if (ATipo == 0){
            return;
        }
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
        Bitmap bitmap;
        if(bitmapDrawable==null){
            imgImagem.buildDrawingCache();
            bitmap = imgImagem.getDrawingCache();
            imgImagem.buildDrawingCache(false);
        }else
        {
            bitmap = bitmapDrawable.getBitmap();
        }

        Mat tmp = new Mat (bitmap.getWidth(), bitmap.getHeight(), CvType.CV_USRTYPE1);
        Utils.bitmapToMat(bitmap, tmp);
        Imgproc.cvtColor(tmp, tmp, CvType.CV_8SC1);
        tmp.convertTo(tmp,CvType.CV_8UC1);

        switch (ATipo){
            case 1: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_AUTUMN);
                break;
            }
            case 2: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_BONE);
                break;
            }
            case 3: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_COOL);
                break;
            }
            case 4: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_HOT);
                break;
            }
            case 5: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_HSV);
                break;
            }
            case 6: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_JET);
                break;
            }
            case 7: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_OCEAN);
                break;
            }
            case 8: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_PARULA);
                break;
            }
            case 9: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_PINK);
                break;
            }
            case 10: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_RAINBOW);
                break;
            }
            case 11: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_SPRING);
                break;
            }
            case 12: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_SUMMER);
                break;
            }
            case 13: {
                Imgproc.applyColorMap(tmp, tmp, Imgproc.COLORMAP_WINTER);
                break;
            }
        }

        Utils.matToBitmap(tmp, bitmap);

        imgImagem.setImageBitmap(bitmap);
    }
}
