package com.silva.lucas.projetoopencv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;

public class LetreiroActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private ImageView imgImagem, imgAtualizar;
    private AppCompatSeekBar volPosicaoX, volPosicaoY;
    private Bitmap bitmap;
    private Button btnAplicar, btnCancelar;
    private Spinner spnTipoFonte, spnTamanho, spnCor;
    private EditText edtTexto;

    private String texto;
    private int tipoFonte, tamanhoFonte, corFonte,  x, y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letreiro);

        spnTipoFonte = (Spinner) findViewById(R.id.spnTipoFonte);
        spnTamanho = (Spinner) findViewById(R.id.spnTamanho);
        spnCor = (Spinner) findViewById(R.id.spnCor);
        imgImagem = (ImageView)findViewById(R.id.imgImagem);
        volPosicaoX = (AppCompatSeekBar)findViewById(R.id.volPosicaoX);
        volPosicaoY = (AppCompatSeekBar)findViewById(R.id.volPosicaoY);
        btnAplicar = (Button) findViewById(R.id.btnAplicar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        imgAtualizar = (ImageView) findViewById(R.id.imgAtualizar);
        edtTexto = (EditText) findViewById(R.id.edtTexto);

        volPosicaoX.setOnSeekBarChangeListener(this);
        volPosicaoY.setOnSeekBarChangeListener(this);

        String[] itemsTamanhoFonte = new String[]{"Hershey Simplex","Hershey Plain", "Hershey Duplex", "Hershey Complex", "Hershey Triplex", "Hershey Complex Small", "Hershey Script Simples"};
        ArrayAdapter<String> adapterTamanhoFonte = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsTamanhoFonte);
        spnTipoFonte.setAdapter(adapterTamanhoFonte);

        String[] itemsTamanho = new String[]{"1","2","3","4","5"};
        ArrayAdapter<String> adapterTamanho = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsTamanho);
        spnTamanho.setAdapter(adapterTamanho);

        String[] itemsCor = new String[]{"Preto","Branco","Vermelho","Azul","Verde","Amarelo"};
        ArrayAdapter<String> adapterCor = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsCor);
        spnCor.setAdapter(adapterCor);

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

        volPosicaoX.setMax(bitmap.getWidth());
        volPosicaoY.setMax(bitmap.getHeight());

        texto = edtTexto.getText().toString();
        x = 0;
        y = 0;
        tipoFonte = 1;
        corFonte = 0;
        tamanhoFonte = 1;

        spnTamanho.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorFontButton));
                tamanhoFonte = position+1;
                AtualizarImagem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnTipoFonte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorFontButton));
                tipoFonte = position+1;
                AtualizarImagem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnCor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorFontButton));
                corFonte = position;
                AtualizarImagem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtualizarImagem();
            }
        });

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
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (seekBar.getId() == R.id.volPosicaoX){
           x = progress;
        } else if (seekBar.getId() == R.id.volPosicaoY){
            y = progress;
        }

        AtualizarImagem();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void AtualizarImagem(){
        texto = edtTexto.getText().toString();

        Mat tmp = new Mat (bitmap.getWidth(), bitmap.getHeight(), CvType.CV_USRTYPE1);
        Utils.bitmapToMat(bitmap, tmp);
        Scalar cor = null;

        switch (corFonte) {
            case 1 :{
                cor = new Scalar(255,255,255,0);
                break;
            }
            case 2 :{
                cor = new Scalar(255,0,0,0);
                break;
            }
            case 3 :{
                cor = new Scalar(0,0,255,0);
                break;
            }
            case 4 :{
                cor = new Scalar(0,255,0,0);
                break;
            }
            case 5 :{
                cor = new Scalar(255,255,0,0);
                break;
            }
            default :{
                cor = new Scalar(0,0,0,0);
                break;
            }
        }

        Imgproc.putText(tmp, texto, new Point(x, y), tipoFonte, tamanhoFonte, cor, 2);

        Bitmap bitmapResultado = null;

        byte[] byteArray = getIntent().getByteArrayExtra("foto");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        imgImagem.setImageBitmap(bmp);

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
        if(bitmapDrawable==null){
            imgImagem.buildDrawingCache();
            bitmapResultado = imgImagem.getDrawingCache();
            imgImagem.buildDrawingCache(false);
        }else
        {
            bitmapResultado = bitmapDrawable.getBitmap();
        }

        Utils.matToBitmap(tmp, bitmapResultado);

        imgImagem.setImageBitmap(bitmapResultado);

    }
}
