/**   
 * <p>本文件仅为内部使用，请勿外传</p>
 */
package com.ebrain.api.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * <p>
 * Description: 图片处理工具类
 * </p>
 * 时间: 2017年6月14日 下午7:29:18
 *
 * @author peisong
 * @since v1.4.2
 */
public class ImageUtils {
	/**
	 * 
	 * <p>
	 * Description: 合并图片，把targetImg
	 * </p>
	 * 时间: 2017年6月14日 下午8:21:59
	 *
	 * @author peisong
	 * @since v1.3.2
	 * @param image
	 * @param x
	 * @param y
	 * @param targetImg
	 * @return BufferedImage
	 */
	public static BufferedImage getBufferedImage(BufferedImage image, int x, int y, BufferedImage targetImg) {
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(targetImg, x, y, null);
		g2.dispose();
		return image;
	}

	public static BufferedImage getBufferedImage(BufferedImage image, int x, int y, String text, Font font,
      Color fontColor) {
    return getBufferedImage(image, x, y, text, font, fontColor, null);
  }
	private static BufferedImage getBufferedImage(BufferedImage image, int x, int y, String text, Font font,
			Color fontColor,Color backgroundColor) {
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setFont(font);
		g2.setColor(fontColor);
		if(null!=backgroundColor){
	    g2.setBackground(backgroundColor);
		}
		g2.drawString(text, x, y);
		g2.dispose();
		return image;
	}

	public static BufferedImage getBufferedImageByURL(String imgUrl) throws IOException {
		return ImageIO.read(new URL(imgUrl));
	}

	public static BufferedImage getBufferedImageFile(File srcFile) throws IOException {
		return ImageIO.read(srcFile);
	}

	/*
	 * 图片缩放
	 */
	public static BufferedImage zoomImage(BufferedImage srcImage, int toWidth, int toHeight) {

		BufferedImage result = null;

		try {
			/* 新生成结果图片 */
			result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
			result.getGraphics().drawImage(srcImage.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH),
					0, 0, null);
		} catch (Exception e) {
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
		return result;
	}

	/*
	 * 图片裁剪
	 */
	public static BufferedImage cutImage(BufferedImage srcImage, int x, int y, int w, int h) throws IOException {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
		ImageIO.write(srcImage, "png", imOut);
		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(bs.toByteArray()));
		Iterator<ImageReader> it=ImageIO.getImageReaders(iis);
		ImageReader reader = it.next();
		reader.setInput(iis, true);
		ImageReadParam param = reader.getDefaultReadParam();
		
		Rectangle rect = new Rectangle(x, y, w, h);
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);
		iis.close();
		return bi;
	}
	
	/*
	 * 图片居中裁剪成正方形
	 */
	public static BufferedImage cutImage(BufferedImage srcImage) throws IOException {
		int w=srcImage.getWidth();
		int newW=w;
		int h=srcImage.getHeight();
		if(newW>h){
			newW=h;
		}
		return ImageUtils.cutImage(srcImage, (w-newW)/2, (h-newW)/2, newW, newW);
	}

	public static BufferedImage makeRoundedImg(BufferedImage srcImage, int toWidth, int toHeight) {

		BufferedImage result = null;

		try {
			BufferedImage im = zoomImage(srcImage, toWidth, toHeight);
			/* 新生成结果图片 */
			result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = result.createGraphics();
			g2.setComposite(AlphaComposite.Src);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.WHITE);
			g2.fill(new RoundRectangle2D.Float(0, 0, toWidth, toHeight, toWidth, toHeight));
			g2.setComposite(AlphaComposite.SrcAtop);
			g2.drawImage(im, 0, 0, null);
		} catch (Exception e) {
			System.out.println("创建缩略图发生异常" + e.getMessage());
		}
		return result;
	}
  public static String toBase64Png(BufferedImage image) throws IOException{
    ByteArrayOutputStream bao=new ByteArrayOutputStream();        
    ImageIO.write(image, "png",bao);
    byte[] bytes= bao.toByteArray();
    bao.close();
    return "data:image/png;base64,"+Base64.encodeBase64String(bytes);
  }
	public static void writeFile(BufferedImage image, File file){
		try{
			FileOutputStream fos = new FileOutputStream(file);
			ImageIO.write(image, "png", fos);
			fos.flush();
			fos.close();
		}catch(Exception e){
			
		}
	}
}
