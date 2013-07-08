/**
 * 
 */
package org.varunverma.abapquiz;

/**
 * @author varun
 *
 */
public class Constants {

	private static boolean premiumVersion;
	private static String productTitle, productDescription, productPrice;
	
	static String getPublicKey() {
		return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCA" +
				"QEAqr69gdA86Ni+C1pV/8fp0JsYpVxEV4U/CLuD2" +
				"vs1R1CvYbhgxjMOIJErRi4wlEjum2DMzLP+y804D" +
				"70m7zLt3oli1CudhDULPM8r+gCpMbbGqNlsdCWVe" +
				"eroBYhCjJqzWxw6FEMe4r7FhBgPYsuFodoQsBV/C" +
				"YFDU7wACdT9Yov/9Rz7bibdcWQg/HVJcEz8FF6mr" +
				"IQ8l5zWLFGvAHi3EA/fT7tucNqSkZjH1QdLh5G1f" +
				"QPN0d8dyzXstFsuvRwtlc7WweJ26uXWwKfIvPAjC" +
				"6v1ddiI+sYRhLnJWo4PSExIav2awP0K6flEB79hm" +
				"3xPJYSVBob6NRoB/N1px//UawIDAQAB";
	}

	static String getProductKey() {
		return "premium_content";
	}

	static void setPremiumVersion(boolean premiumVersion) {
		Constants.premiumVersion = premiumVersion;
	}
	
	static boolean isPremiumVersion(){
		return premiumVersion;
	}

	/**
	 * @return the productTitle
	 */
	static String getProductTitle() {
		return productTitle;
	}

	/**
	 * @param productTitle the productTitle to set
	 */
	static void setProductTitle(String productTitle) {
		Constants.productTitle = productTitle;
	}

	/**
	 * @return the productDescription
	 */
	static String getProductDescription() {
		return productDescription;
	}

	/**
	 * @param productDescription the productDescription to set
	 */
	static void setProductDescription(String productDescription) {
		Constants.productDescription = productDescription;
	}

	/**
	 * @return the productPrice
	 */
	static String getProductPrice() {
		return productPrice;
	}

	/**
	 * @param productPrice the productPrice to set
	 */
	static void setProductPrice(String productPrice) {
		Constants.productPrice = productPrice;
	}

}