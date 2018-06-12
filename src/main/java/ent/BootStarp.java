/**
 * 
 */
package ent;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netwk.ZcwDataClient;

/**
 * @author Administrator
 *
 */
public class BootStarp {
	private static final Logger logger = LoggerFactory.getLogger(BootStarp.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ZcwDataClient client = ZcwDataClient.getClient();
		if (client.parse()) {
			Set<String> set = client.getLast_30_issue_no();
			logger.debug("The curent set size:" + set.size());
		}
	}

}
