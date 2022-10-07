package com.example.fordecosport;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.view.View;
import android.widget.Button;

import com.example.fordecosport.domain.Event;
import com.example.fordecosport.domain.rest.LibApiVolley;
import com.example.fordecosport.sqlite.DataBaseHelper;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class StartActivity extends AppCompatActivity {
    private KeyguardManager keyguardManager;
    private Button button;
    public static final String KEY_NAME = "KEY_NAME";
    private static final int AUTHENTICATION_DURATION_SECONDS = 30;
    private DataBaseHelper dataBaseHelper;
    private LibApiVolley libApiVolley;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        dataBaseHelper = new DataBaseHelper(this);
        button = findViewById(R.id.start_bt);
        libApiVolley = new LibApiVolley(this);
        keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

//        if (!keyguardManager.isKeyguardSecure()) {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
            finish();
//            return;
//        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tryEncrypt()==true){
                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                    startActivity(intent);
                    libApiVolley.addEvent(new Event(0, "4",Time.getTime()));
                    dataBaseHelper.addDataBase("Successful login",Time.getTime());
                    finish();
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        createKey();
        tryEncrypt();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean tryEncrypt() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            SecretKey secretKey = (SecretKey) keyStore.getKey(KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            libApiVolley.addEvent(new Event(0, "4",Time.getTime()));

            return true;
        } catch (UserNotAuthenticatedException e) {
            showAuthenticationScreen();
            return false;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException |
                CertificateException | UnrecoverableKeyException | IOException
                | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationValidityDurationSeconds(AUTHENTICATION_DURATION_SECONDS)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException
                | CertificateException | IOException e) {
            throw new RuntimeException("Failed to create a symmetric key", e);
        }
    }
    private void showAuthenticationScreen() {
        Intent intent = keyguardManager.createConfirmDeviceCredentialIntent(null, null);
        if (intent != null) {
            startActivityForResult(intent, 1);
        }
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (tryEncrypt()) {
                    Intent intent = new Intent(this, MainActivity.class);
                    dataBaseHelper.addDataBase("Successful login",Time.getTime());
                    startActivity(intent);
                    finish();
                }
            } else {
                libApiVolley.addEvent(new Event(0, "5",Time.getTime()));
                dataBaseHelper.addDataBase("Unsuccessful login",Time.getTime());
            }
        }
    }

}