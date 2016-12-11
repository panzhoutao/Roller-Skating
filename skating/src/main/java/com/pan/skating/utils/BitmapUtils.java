package com.pan.skating.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitmapUtils {
	public static Bitmap getBitmap100k(Bitmap bit, int i) {
		//内存流 用作压缩bitmap的容器
		ByteArrayOutputStream ARRAY = new ByteArrayOutputStream();
		// 压缩所需要的参数 ，格式，静态属性，品质
		int quality = 100;

		bit.compress(CompressFormat.JPEG, quality, ARRAY);
		while (ARRAY.toByteArray().length > i * 1024) {
			ARRAY.reset();
			quality = quality * 90 / 100;
			bit.compress(CompressFormat.JPEG, quality, ARRAY);
			if(quality<10){
				break;
			}
		}
		bit = BitmapFactory.decodeByteArray(ARRAY.toByteArray(), 0,
				ARRAY.toByteArray().length);
		return bit;

	}
}
