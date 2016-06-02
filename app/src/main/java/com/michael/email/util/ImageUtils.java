package com.michael.email.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 图片工具类
 * 
 * @author Michael
 * */
public class ImageUtils
{

	/**
	 * 将图片裁剪为圆形
	 */
	// public static Bitmap getCroppedBitmap(Bitmap bitmap)
	// {
	// Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	// bitmap.getHeight(), Config.ARGB_8888);
	// // 设置一个图片大小的矩形
	// final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	// // bm是一个刚好canvas大小的空Bitmap ，画完后应该会自动保存到bm
	// Canvas canvas = new Canvas(output);
	//
	// final Paint paint = new Paint();
	// paint.setAntiAlias(true);
	//
	// int halfWidth = bitmap.getWidth() / 2;
	// int halfHeight = bitmap.getHeight() / 2;
	// // 画圆
	// canvas.drawCircle(halfWidth, halfHeight,
	// Math.max(halfWidth, halfHeight), paint);
	// // 设置为取两层图像交集部门,只显示上层图像
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	// // 画图像
	// canvas.drawBitmap(bitmap, rect, rect, paint);
	//
	// return output;
	// }

	/**
	 * 将图片裁剪为圆形
	 * 
	 * 没用了
	 */
	public static Bitmap getCroppedBitmap(Bitmap bitmap)
	{
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		// Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
		// return _bmp;
		return output;
	}

	/**
	 * 将bitmap转为File
	 * 
	 * */
	public static File convertBitmapToFile(String filePath, Bitmap bitmap)
	{
		File file = null;
		try
		{
			file = new File(filePath);
			FileOutputStream fOut = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			L.i(null, "保存图片失败!");
		}
		return file;
	}
}
