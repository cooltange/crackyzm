package com.stx.crack;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class CrackYZM {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://192.168.200.200/createValidationCode.php");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(5 * 1000);
		conn.connect();
		InputStream in = conn.getInputStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		byte[] dataImage = bos.toByteArray();

		byteToImage(dataImage, "11");
		bos.close();
		in.close();
		
		BufferedImage img = removeBackgroud("d:\\11.jpg");
		
		List<BufferedImage> listImg = splitImage(img);
		Map<BufferedImage, String> map = loadTrainData();
		String result = "";
		for (BufferedImage bi : listImg) {
			result += getSingleCharOcr(bi, map);
		}
		ImageIO.write(img, "JPG", new File("d:\\111.jpg"));
		
		System.out.println(result);

	}

	public static boolean byteToImage(byte[] b, String filename) throws Exception {
		boolean bl = false;
		File binaryFile = new File("d:\\" + filename + ".jpg");
		FileOutputStream fileOutStream = new FileOutputStream(binaryFile);
		for (int i = 0; i < b.length; i++) {
			fileOutStream.write(b[i]);
		}
		fileOutStream.flush();
		fileOutStream.close();
		bl = true;
		return bl;
	}
	
	public static BufferedImage removeBackgroud(String picFile)
			throws Exception {
		BufferedImage img = ImageIO.read(new File(picFile));
		int width = img.getWidth();
		int height = img.getHeight();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				if (isGreen(img.getRGB(x, y))) {
					img.setRGB(x, y, Color.WHITE.getRGB());
				} else {
					img.setRGB(x, y, Color.BLACK.getRGB());
				}
			}
		}
		return img;
	}
	
	
	public static boolean isGreen(int colorInt) {
		Color color = new Color(colorInt);
		return color.getGreen() > 155 && color.getBlue() > 123;
	}
	
	public static String getAllOcr(String file) throws Exception {
		BufferedImage img = removeBackgroud(file);
		List<BufferedImage> listImg = splitImage(img);
		Map<BufferedImage, String> map = loadTrainData();
		String result = "";
		for (BufferedImage bimg : listImg) {
			result += getSingleCharOcr(bimg, map);
		}
		ImageIO.write(img, "JPG", new File("result\\"+result+".jpg"));
		return result;
	}
	
	public static String getSingleCharOcr(BufferedImage img,
			Map<BufferedImage, String> map) {
		String result = "";
		int width = img.getWidth();
		int height = img.getHeight();
		int min = width * height;
		for (BufferedImage bi : map.keySet()) {
			int count = 0;
			Label1: for (int x = 0; x < width; ++x) {
				for (int y = 0; y < height; ++y) {
					if (img.getRGB(x, y) != bi.getRGB(x, y)) {
						count++;
						if (count >= min)
							break Label1;
					}
				}
			}
			if (count < min) {
				min = count;
				result = map.get(bi);
			}
		}
		return result;
	}
	
	public static Map<BufferedImage, String> loadTrainData() throws Exception {
		Map<BufferedImage, String> map = new HashMap<BufferedImage, String>();
		File dir = new File("train");
		File[] files = dir.listFiles();
		for (File file : files) {
			map.put(ImageIO.read(file), file.getName().charAt(0) + "");
		}
		return map;
	}
	
	public static List<BufferedImage> splitImage(BufferedImage img)
			throws Exception {
		List<BufferedImage> subImgs = new ArrayList<BufferedImage>();
		subImgs.add(img.getSubimage(10, 4, 8, 10));
		subImgs.add(img.getSubimage(19, 4, 8, 10));
		subImgs.add(img.getSubimage(28, 4, 8, 10));
		subImgs.add(img.getSubimage(37, 4, 8, 10));
		subImgs.add(img.getSubimage(46, 4, 8, 10));
		
		for (int i = 0; i < subImgs.size(); i++) {
			ImageIO.write(subImgs.get(i), "JPG", new File("d:\\train\\"+i+".jpg"));
		}
		
		return subImgs;
	}

}
