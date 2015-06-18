package com.okan.qr;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
//jar dosyası burdan
// http://mvnrepository.com/artifact/com.google.zxing/core/2.3.0
// ornek burdan
// http://stackoverflow.com/questions/4854442/embed-zxing-library-without-using-barcode-scanner-app
import com.ipaulpro.afilechooser.utils.FileUtils;
// kullanılan aFileChoose kütüphanesi bunu eklemek için projeye sağ tık properties, android(sol menu), library ekle 
// https://github.com/iPaulPro/aFileChooser


/*
 * core-2.3.0.jar eklenecek
 * aFileChoose kütüphanesi eklenecek
 * Manifeste provider eklenecek
 */

public class MainActivity extends Activity implements OnClickListener {

	public static class Global {
		public static String text=null;
	}
	public static String TAG ="OKANCANCOSAR";
	private static final int REQUEST_CODE = 6384;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * resim seçmeyi sağlıyor(RESMI YUKLE)
		 * */
		Button btn =(Button)findViewById(R.id.butonSec);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showChooser();
			}
		});
	}
	/*
	 *  gelen linki açıyor (LOAD THE URL)
	 * */
	@Override
	public void onClick(View v) {
		Uri uri = Uri.parse(Global.text); 
		Intent intent = new Intent(Intent.ACTION_VIEW, uri); 
		startActivity(intent);
	}
	/*
	 * aldığı resim yolu ile bitmap oluşturup qrcode decode yapıyor
	 * 
	 * */
	public void decoderViaFilePath(String path){

		File folder = new File(path); 
		if(!folder.exists()){		// png var mı diye path ile dosya kontrolü
			Toast.makeText(getApplicationContext(), "Dosya yok", Toast.LENGTH_SHORT).show();
		}else{
			Bitmap bMap = BitmapFactory.decodeFile(path);
			TextView textv = (TextView) findViewById(R.id.mytext);
			View webbutton=findViewById(R.id.webbutton);

			int width = bMap.getWidth();   // bitmap ı elle üretidi
			int height = bMap.getHeight(); // stackoverflow'dakinde import hatası veriyordu.
			int[] pixels = new int[width * height];
			bMap.getPixels(pixels, 0, width, 0, 0, width, height);

			RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Reader reader = new MultiFormatReader();
			try {
				Result result = reader.decode(bitmap);
				Global.text = result.getText(); 
				textv.setText(Global.text);
				webbutton.setOnClickListener(this);

			} catch (NotFoundException e) {
				Log.i("MainAct", e.getMessage().toString());
			} catch (ChecksumException e) {
				Log.i("MainAct", e.getMessage().toString());
			} catch (FormatException e) {
				Log.i("MainAct", e.getMessage().toString());
			}
		}
	}
	/*
	 * seçme ekranını oluşturuyor
	 * */
	private void showChooser() {
		// Use the GET_CONTENT intent from the utility class
		Intent target = FileUtils.createGetContentIntent();
		// Create the chooser Intent
		Intent intent = Intent.createChooser(
				target, getString(R.string.chooser_title));
		try {
			startActivityForResult(intent, REQUEST_CODE);
		} catch (ActivityNotFoundException e) {
			// The reason for the existence of aFileChooser
		}
	}
	/*
	 * Activity sonlanınca yani seçim işlemi yapılınca URI ı gerçek bir path düzenine getiriyor.
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// Get the URI of the selected file
					final Uri uri = data.getData();
					Log.i(TAG, "Uri = " + uri.toString());
					try {
						// Get the file path from the URI
						String path = FileUtils.getPath(this, uri);

						Toast.makeText(MainActivity.this,"File Selected: " + path, Toast.LENGTH_LONG).show();
						// ALINAN RESIM YOLU İLE DECODER ÇALIŞTIRILIR
						decoderViaFilePath(path);

					} catch (Exception e) {
						Log.e("FileSelectorTestActivity", "File select error", e);
					}
				}
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
