package com.haishanda.android.videoapp.Fragement;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.haishanda.android.videoapp.R;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class QRCodeFragment extends Fragment {
    private String globalId;
    @BindView(R.id.qrcode_img)
    ImageView qrCodeImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode, container, false);
        ButterKnife.bind(this, view);
        Bundle qrCodeData = this.getArguments();
        globalId = qrCodeData.getString("globalId");
        Glide.with(this)
                .load(createQRCode(globalId))
                .asBitmap()
                .into(qrCodeImage);
//        qrCodeImage.setImageBitmap(createQRCode(globalId));
        return view;
    }

    @OnClick(R.id.back_to_about_boat_btn)
    public void backToAboutBoatFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    public Bitmap createQRCode(String qrText) {
        int sidelength = 480;
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(qrText,
                    BarcodeFormat.QR_CODE, sidelength, sidelength, hints);
            int[] pixels = new int[sidelength * sidelength];
            for (int y = 0; y < sidelength; y++) {
                for (int x = 0; x < sidelength; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * sidelength + x] = 0xff000000;
                    } else {
                        pixels[y * sidelength + x] = 0xffffffff;
                    }

                }
            }
            Bitmap bitmap = Bitmap.createBitmap(sidelength, sidelength,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, sidelength, 0, 0, sidelength, sidelength);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            return null;
        }

    }

}
