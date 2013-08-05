package delegates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;

import akka.event.slf4j.Logger;

import play.Play;
import play.mvc.Http.MultipartFormData.FilePart;
import pojos.CityBean;
import pojos.FileBean;
import utils.FileUtilities;
import utils.PlayDozerMapper;

public class UtilitiesDelegate {
	public static UtilitiesDelegate getInstance() {
		return new UtilitiesDelegate();
	}

	public List<CityBean> getCities () {
		List<models.City> 
			modelCities = 
				models.City.all();
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;
	}
	
	public List<CityBean> getCitiesByCountryId(Long countryId) {
		List<models.City> modelCities = models.City.readByCountry(countryId);
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;
	}

	public List<CityBean> getCitiesByCountryName(String countryName) {
		List<models.City> modelCities = models.City.readByCountryName(countryName);
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;
	}

	public List<CityBean> getCitiesByCountryNameAndRegion(String country,
			String region) {
		List<models.City> modelCities = models.City.readByCountryNameAndRegion(country, region);
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;	
	}

	
	public List<CityBean> getNewCities (Long lastCityId) {
		List<models.City> modelCities = models.City.readNewById(lastCityId);
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;
	}
	
	public List<CityBean> getCityByName (String cityName) {
		List<models.City> modelCities = models.City.findByName(cityName);
		List<CityBean> pojosCities = new ArrayList<CityBean>();
		for (models.City city : modelCities) {
			CityBean cityBean = PlayDozerMapper.getInstance().map(
					city, CityBean.class);
			pojosCities.add(cityBean);
		}
		return pojosCities;
	}

	public CityBean getCitiesById(Long cityId) {
		models.City city = models.City.read(cityId);
		CityBean cityBean = PlayDozerMapper.getInstance().map(city, CityBean.class);
		return cityBean;
	}	

	public FileBean saveFile(FilePart file, FileBean fileBean) {
		/*
		 * 1. Prepare file metadata before saving the file in the final destination
		 */
		String uploadDir = Play.application().configuration().getString("files.home");
		String filesBaseURL = Play.application().configuration().getString("files.baseurl");
		String fileName = file.getFilename();
		String fullPath = uploadDir+ FileUtilities.slash + fileName;
		String contentType = file.getContentType();
		File uploadFile = file.getFile();
		//String filesBaseURL = "http://test.reminiscens.me/files";

		Logger.root().debug("Saving File....");
		Logger.root().debug("--> fileName=" + fileName);
		Logger.root().debug("--> contentType=" + contentType);
		Logger.root().debug("--> uploadFile=" + uploadFile);

		/*
		 * 2. Save the file in the final destination
		 */
		File localFile = new File(fullPath);
		uploadFile.renameTo(localFile);		
		Logger.root().debug("--> localFile=" + localFile);

		/*
		 * 3. Generate Hashcode to use as new name
		 */
		String hashcode = UUID.nameUUIDFromBytes(fullPath.getBytes()).toString();
		/* 
		 * 4. Save File metadata in Database
		 */
		fileBean.setFilename(fileName);
		fileBean.setURI(filesBaseURL + "/" + fileName);
		fileBean.setContentType(contentType);
		fileBean.setCreationDate(DateTime.now());
		fileBean.setHashcode(hashcode);
		Logger.root().debug("--> creationDate=" + fileBean.getCreationDate());
		Logger.root().debug("--> hashcode=" + fileBean.getHashcode());
		
		models.File f = PlayDozerMapper.getInstance().map(fileBean, models.File.class);
		f.save();
		fileBean.setFileId(f.getFileId());
		return fileBean;
	}
	
}
