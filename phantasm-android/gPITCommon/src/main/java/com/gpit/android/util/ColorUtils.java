package com.gpit.android.util;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;

public class ColorUtils {
	public Bitmap setBrightness(Bitmap src, int value) {
		// original image size
		int width = src.getWidth();
		int height = src.getHeight();
		// create output bitmap
		Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
		// color information
		int A, R, G, B;
		int pixel;

		// scan through all pixels
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// get pixel color
				pixel = src.getPixel(x, y);
				A = Color.alpha(pixel);
				R = Color.red(pixel);
				G = Color.green(pixel);
				B = Color.blue(pixel);

				// increase/decrease each channel
				R += value;
				if (R > 255) {
					R = 255;
				} else if (R < 0) {
					R = 0;
				}

				G += value;
				if (G > 255) {
					G = 255;
				} else if (G < 0) {
					G = 0;
				}

				B += value;
				if (B > 255) {
					B = 255;
				} else if (B < 0) {
					B = 0;
				}

				// apply new pixel color to output bitmap
				bmOut.setPixel(x, y, Color.argb(A, R, G, B));
			}
		}

		// return final image
		return bmOut;
	}

	public static ColorMatrixColorFilter brightIt(int fb) {
		ColorMatrix cmB = new ColorMatrix();
		cmB.set(new float[] {
				1, 0, 0, 0, fb,
				0, 1, 0, 0, fb,
				0, 0, 1, 0, fb,
				0, 0, 0, 1, 0   });

		ColorMatrix colorMatrix = new ColorMatrix();
		colorMatrix.set(cmB);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(colorMatrix);

		return f;
	}
}
