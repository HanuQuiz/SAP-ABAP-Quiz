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
		return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj" +
				"bCiPdAIohSBBUgW4L5MbLDdQJammxP1oIP0mVwG9XtQ9" +
				"rC+zcgWXGqlN2dJ/qphf2yH6hPXlvdUCOKujnR3CEjZ6" +
				"PUpdexWJX2G6vlB4ziMIFeo0fKsxNMob+Tyz7zqa/bOj" +
				"EeoJRDK7PTm1jFwhX7NvYsdCj52zOb2iCxdMMUrhHdiB" +
				"wtmhgv88icQ1vL2oyEvv6zow6PxZuFK1uw9eXQKVU5Mq" +
				"VwkyFArENDoM0ReACWCb3IIc9Flb9PFW4gHDiHZiGc18" +
				"H2cDDeyXBtUUUc23TmxC6CFGv8rhbRoQHuVOE8YKE4+2" +
				"sg8m9vA6iA+pMUmb/iQ1Fbizo37S7HIqwIDAQAB";
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