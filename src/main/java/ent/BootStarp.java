/**
 * 
 */
package ent;

import java.util.Set;

import netwk.ZcwDataClient;

/**
 * @author Administrator
 *
 */
public class BootStarp {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ZcwDataClient client = ZcwDataClient.getClient().parse();
		//System.out.println(client.getLast_30_issue_no());
		//System.out.println(client.getKJDetailByIssue("2017146"));
		Set<String> set = client.getLast_30_issue_no();
		set.forEach((String e) ->{
			try {
				System.out.println(client.getKjNumByIssue(e));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}

}
