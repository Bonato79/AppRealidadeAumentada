package com.example.apprealidadeaumentada;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.TextureView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;


public class CameraActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1;
    private TextureView textureView;
    private Session arSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        textureView = findViewById(R.id.texture_view);

        // Verificar permissões da câmera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida, inicializar o ARCore
            initializeAR();
        } else {
            // Solicitar permissão da câmera
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void initializeAR() {
        // Verificar suporte do ARCore
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (availability.isTransient()) {
            // Aguardar o ARCore se tornar disponível
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initializeAR();
        } else if (availability.isSupported()) {
            // Criar sessão AR
            try {
                arSession = new Session(this);
            } catch (UnavailableArcoreNotInstalledException |
                     UnavailableDeviceNotCompatibleException | UnavailableSdkTooOldException |
                     UnavailableApkTooOldException e) {
                throw new RuntimeException(e);
            }

            // Configurar a visualização da câmera no TextureView
            textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                    // Configurar a sessão AR para renderizar no TextureView
                    textureView.getSurfaceTexture().setOnFrameAvailableListener(null);

                    int[] textures = new int[1];
                    GLES20.glGenTextures(1, textures, 0);
                    int textureId = textures[0];

                    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
                    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                    arSession.setCameraTextureName(textureId);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
                    // Atualizar configurações da sessão AR ao alterar o tamanho do TextureView
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    // Liberar recursos ao destruir o TextureView
                    arSession.setCameraTextureName(0);
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
                    // Atualizar frame da câmera
                }
            });
        } else {
            // O dispositivo não suporta o ARCore
            Toast.makeText(this, "ARCore não suportado neste dispositivo.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, inicializar o ARCore
                initializeAR();
            } else {
                // Permissão negada, encerrar a atividade
                Toast.makeText(this, "Permissão da câmera negada.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
