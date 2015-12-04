package controllers;

import static models.Checker.NotFound;
import static models.Checker.Required;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import models.Faulty;
import models.Faulty.Code;
import models.Secured;
import models.db.Media;
import models.db.Media.Size;
import models.db.Media.Variety;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.data.Upload;
import play.db.jpa.Blob;
import play.libs.Images;

public class MediaHub extends Controller {

	private static void renderImage(Media media, String size)
			throws IOException {

		if (media != null && media.bin != null && media.bin.getFile() != null) {
			File image = media.bin.getFile();
			if (!StringUtils.isEmpty(size)
					&& !size.equalsIgnoreCase(Size.O.toString())) {

				int v;
				if (StringUtils.isNumeric(size))
					v = Integer.parseInt(size);
				else
					v = Size.of(size).value;
				Variety variety = media.findVariety(size);

				if (variety == null && v > 0) {
					File rImage = File.createTempFile("resize", "image");
					Images.resize(image, rImage, v, v, true);
					Blob bin = new Blob();
					bin.set(new FileInputStream(rImage), media.mimeType);
					variety = media.newVariety(size, bin);
				}
				image = variety.bin.getFile();
			}
			renderBinary(new FileInputStream(image), media.filename,
					media.mimeType, true);
		} else {
			File image = Play.getFile("/resources/images/avatar.png");
			renderBinary(new FileInputStream(image), "placeholder.png",
					"image/png", true);
		}
	}

	@Secured
	public static void upload(Upload data) {
		request.args.put(REQ_TYPE, API);
		request.format = "json";
		Media media = Media.create(data, connected());
		media.save();
		renderJSON(media.asJson());
	}

	public static void download(long id) throws FileNotFoundException, Faulty {
		Media media = Media.findById(id);
		NotFound(Code.E1003, media);
		renderBinary(new FileInputStream(media.bin.getFile()), media.filename,
				media.mimeType, true);
	}

	public static void image(Long id, String size) throws IOException, Faulty {
		Required(Code.E1004, id);
		Media media = Media.findById(id);
		NotFound(Code.E1005, media);
		renderImage(media, size);
	}

	public static void thumbnail(Long id) throws IOException, Faulty {
		Required(Code.E1004, id);
		Media media = Media.findById(id);
		NotFound(Code.E1005, media);

		if (media != null && media.bin != null && media.bin.getFile() != null) {
			File image = media.bin.getFile();
			Blob thumbnail = media.thumbnail;
			if (thumbnail == null || thumbnail.getUUID() == null) {
				File rImage = File.createTempFile("thumbnail", "image");
				BufferedImage source = ImageIO.read(image);
				int w = source.getWidth();
				if (source.getHeight() < w)
					w = source.getHeight();
				if (w > 600)
					w = 600;
				int h = (int) (w / 1.5);
				Images.crop(image, rImage, 0, 0, w, h);
				thumbnail = new Blob();
				thumbnail.set(new FileInputStream(rImage), media.mimeType);
				media.thumbnail = thumbnail;
				media.save();
			}
			renderBinary(new FileInputStream(thumbnail.getFile()),
					media.filename, media.mimeType, true);
		} else {
			File image = Play.getFile("/resources/images/avatar.png");
			renderBinary(new FileInputStream(image), "placeholder.png",
					"image/png", true);
		}
	}

}
