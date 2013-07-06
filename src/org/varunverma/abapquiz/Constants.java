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
		// TODO - Varun to set the public key
		return "";
	}

	static String getProductKey() {
		// TODO - Varun to set the product Key
		return "";
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