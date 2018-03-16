package com.silva.lucas.projetoopencv;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ImageView imgImagem, imgFace, imgBrilho, imgPintar, imgContraste, imgRgb, imgTexto;
    Button btnEscolherImagem, btnSalvarImagem;

    private static int REQUISICAO_GALERIA = 1;
    private static int REQUISICAO_LUMINOSIDADE = 2;
    private static int REQUISICAO_RGB = 3;
    private static int REQUISICAO_LETREIRO = 4;

    static {
        if (!OpenCVLoader.initDebug()) {
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnEscolherImagem = (Button) findViewById(R.id.btnEscolherImagem);
        btnSalvarImagem = (Button) findViewById(R.id.btnSalvarImagem);
        imgImagem = (ImageView) findViewById(R.id.imgImagem);
        imgFace = (ImageView) findViewById(R.id.imgFace);
        imgBrilho = (ImageView) findViewById(R.id.imgBrilho);
        imgPintar = (ImageView) findViewById(R.id.imgPintar);
        imgRgb = (ImageView) findViewById(R.id.imgRgb);
        imgTexto = (ImageView) findViewById(R.id.imgTexto);

        btnEscolherImagem.setOnClickListener(new View.OnClickListener() {
            @Override


            //ver o porque da img n carregar

            
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, REQUISICAO_GALERIA);
            }
        });

        btnSalvarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = null;
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
                if(bitmapDrawable==null){
                    imgImagem.buildDrawingCache();
                    bitmap = imgImagem.getDrawingCache();
                    imgImagem.buildDrawingCache(false);
                }else
                {
                    bitmap = bitmapDrawable .getBitmap();
                }

                Random random = new Random();

                int numeroAleatorio = random.nextInt((10000 - 0) + 1) + 0;

                String savedImageURL = MediaStore.Images.Media.insertImage(
                        getContentResolver(),
                        bitmap,
                        "imagem"+Integer.toString(numeroAleatorio),
                        "Imagem tratada no OpenCV!"
                );

                Toast.makeText(MainActivity.this, "Imagem salva em: "+savedImageURL, Toast.LENGTH_SHORT).show();

            }
        });
        imgFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imgImagem.getDrawable());
                Bitmap bitmap;
                if(bitmapDrawable==null){
                    imgImagem.buildDrawingCache();
                    bitmap = imgImagem.getDrawingCache();
                    imgImagem.buildDrawingCache(false);
                }else
                {
                    bitmap = bitmapDrawable .getBitmap();
                }


                Mat tmp = new Mat (bitmap.getWidth(), bitmap.getHeight(), CvType.CV_USRTYPE1);
                Utils.bitmapToMat(bitmap, tmp);

                Mat gray = new Mat();
                Imgproc.cvtColor(tmp, gray, Imgproc.COLOR_RGB2GRAY);
                Imgproc.equalizeHist(gray, gray);

                MatOfRect faceDetections = new MatOfRect();

                CascadeClassifier faceCascade;

                try {
                    // Copy the resource into a temp file so OpenCV can load it
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                    File cascadeDir = getDir("xml", Context.MODE_PRIVATE);
                    File mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
                    FileOutputStream os = new FileOutputStream(mCascadeFile);


                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.close();

                    faceCascade = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                    faceCascade.detectMultiScale(gray,faceDetections,1.1,2,2,new Size(0,0),new Size(gray.width(),gray.height()));
                    for (Rect rect : faceDetections.toArray()) {
                        Imgproc.rectangle(tmp, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
                    }


                    Utils.matToBitmap(tmp, bitmap);

                } catch (Exception e) {
                    Log.e("OpenCVActivity", "Erro ao carregar biblioteca", e);
                }

                imgImagem.setImageBitmap(bitmap);
            }
        });


        imgPintar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_RGB2GRAY);

                Utils.matToBitmap(tmp, bitmap);

                imgImagem.setImageBitmap(bitmap);


            }
        });

        imgBrilho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LuminosidadeActivity.class);
                Bundle pacote = new Bundle();

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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                pacote.putByteArray("foto",byteArray);

                intent.putExtras(pacote);

                startActivityForResult(intent,REQUISICAO_LUMINOSIDADE);
            }
        });

        imgRgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MapaCorActivity.class);
                Bundle pacote = new Bundle();

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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                pacote.putByteArray("foto",byteArray);

                intent.putExtras(pacote);

                startActivityForResult(intent,REQUISICAO_RGB);




            }
        });

        imgTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LetreiroActivity.class);
                Bundle pacote = new Bundle();

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

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                pacote.putByteArray("foto",byteArray);

                intent.putExtras(pacote);

                startActivityForResult(intent,REQUISICAO_LETREIRO);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUISICAO_GALERIA && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();



            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgImagem.setImageBitmap(bmp);

        }

        if (requestCode == REQUISICAO_LUMINOSIDADE && resultCode == RESULT_OK) {
            Bundle pacoteRetorno = data.getExtras();

            byte[] byteArray = pacoteRetorno.getByteArray("foto");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgImagem.setImageBitmap(bmp);

        }

        if (requestCode == REQUISICAO_RGB && resultCode == RESULT_OK) {
            Bundle pacoteRetorno = data.getExtras();

            byte[] byteArray = pacoteRetorno.getByteArray("foto");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgImagem.setImageBitmap(bmp);

        }

        if (requestCode == REQUISICAO_LETREIRO && resultCode == RESULT_OK) {
            Bundle pacoteRetorno = data.getExtras();

            byte[] byteArray = pacoteRetorno.getByteArray("foto");
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgImagem.setImageBitmap(bmp);

        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Biblioteca do OpenCV nao encontrada, procurando biblioteca externa.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "Biblioteca do OpenCV encontrada com exito!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}
