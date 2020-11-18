
import java.io.IOException;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class SSU_Date extends SmartCampusTokenizer{
	protected static final String u_saint_Date_link 
	= "https://ssu.ac.kr/%ED%95%99%EC%82%AC/%ED%95%99%EC%82%AC%EC%9D%BC%EC%A0%95/?years=2020";
	protected static final String u_saint_Date_link2
	="https://ssu.ac.kr/%ED%95%99%EC%82%AC/%ED%95%99%EC%82%AC%EC%9D%BC%EC%A0%95/?years=2019";
	
	
	/*�Ʒ� �� �迭�� ��¥�� ���� ������ ������ ������.*/
	protected static String[] usaint_date; // ũ�Ѹ� ������ ��� �迭 (�߿�迭) usaint_date[��¥]
	protected static String[] usaint_schedule; //usaint_schedule[������ ����]
	private static int count_usaint_date;
	
	protected static String [][] usaint_date_token; //���� �׸��� �� [0]:���� ,[1]:��
	
	
	
	
	//usaint �л������� �������� �ܾ��
	public static void u_saint_date_block_crawl() throws IOException
	{
		Document usaint_date_link = Jsoup.connect(u_saint_Date_link)
				.get();
//		System.out.println(usaint_date_link);
		String usaint_date_toString = usaint_date_link.toString();
//		String start_block = "<a href=\"?years="+Integer.toString(current_year)+"\" class=\"year-next\">";
		String start_block = "<h5 class=\"font-weight-light mb-0\"><strong>";
		String end_block = "<footer id=\"footer\" class=\"d-print-none\">";
		
		int start_block_index = usaint_date_toString.indexOf(start_block);
		int end_block_index = usaint_date_toString.indexOf(end_block);
//		System.out.println(usaint_date_toString.substring(start_block_index,end_block_index));
		String one_block = usaint_date_toString.substring(start_block_index,end_block_index);
		
		
		String one_block_start_str = "<div class=\"col-12 col-lg-4 col-xl-3 font-weight-normal text-primary\">";
		String one_block_end_str ="</div> </li>"; 
		
		int next = 0;
		count_usaint_date = 1;
		while(true)
		{
//			System.out.println("=========================================================================");
			int one_block_start_idx = one_block.indexOf(one_block_start_str,next);
			int one_block_end_idx = one_block.indexOf(one_block_end_str,one_block_start_idx);
			if(one_block_start_idx == -1)
				break;
//			System.out.println("=========================================================================");
			next = one_block_end_idx;
			count_usaint_date++;
		}
		
		
		next = 0;
		usaint_date = new String [count_usaint_date];
		usaint_schedule = new String [count_usaint_date]; 
		int count_array = 0;
		while(true)
		{
//			System.out.println("=========================================================================");
			int one_block_start_idx = one_block.indexOf(one_block_start_str,next);
			int one_block_end_idx = one_block.indexOf(one_block_end_str,one_block_start_idx);
			if(one_block_start_idx == -1)
				break;
			String one_block_date = one_block.substring(one_block_start_idx,one_block_end_idx);
//			System.out.println("=========================================================================");
			next = one_block_end_idx;
			
//			System.out.println(one_block_date);
			String date_start_str = "<div class=\"col-12 col-lg-4 col-xl-3 font-weight-normal text-primary\">";
			String date_end_str = "</div>";
			int date_start_idx = one_block_date.indexOf(date_start_str);
			int date_end_idx = one_block_date.indexOf(date_end_str);
//			System.out.println(one_block_date.substring(date_start_idx+date_start_str.length(),date_end_idx).trim());
			usaint_date[count_array] = one_block_date.substring(date_start_idx+date_start_str.length(),date_end_idx).trim();
			
			String date_start_str2 = "<div class=\"col-12 col-lg-8 col-xl-9\">";
			String date_end_str2 = "</div>";
			int date_start_idx2 = one_block_date.indexOf(date_start_str2);
			int date_end_idx2 = one_block_date.indexOf(date_end_str2,date_start_idx2);
//			System.out.println(one_block_date.substring(date_start_idx2+date_start_str2.length(),date_end_idx2).trim());
			usaint_schedule[count_array] = one_block_date.substring(date_start_idx2+date_start_str2.length(),date_end_idx2).trim();
			count_array++;
		}
	}
	
	
	//�л� ���� ��� �Լ�
	public static void print_schedule_list()
	{
		
		for(int i = 0 ; i < count_usaint_date ; i++)
		{
			if(usaint_schedule[i] != null)
			{
				System.out.println("count = "+ (i+1));
				System.out.println("�� �� = "+usaint_schedule[i]);
//				System.out.println("�л� ��¥  = "+usaint_date[i]);
				System.out.println("�л� ��¥ (����) = "+usaint_date_token[i][0]);
				System.out.println("�л� ��¥ (��) = "+usaint_date_token[i][1]);
				System.out.println();
			}
		}
	}
	
	
	// tokenzier : 01.13 (��) ~ 01.15 (��) ������ -> 2020-01-13 00:00:00 ~ 2020-01-15 00:00:00 �������� ��ȯ
	public static void usaint_date_tokenzier()
	{
		usaint_date_token = new String [count_usaint_date][2];
		//SmartCampusTokenizer�� current_time (����ð�)
		String current_time_temp = current_time;
		int current_year = year(current_time_temp);
		String current_year1 = Integer.toString(current_year)+"-";
		
		
		for(int  i = 0 ; i < count_usaint_date ; i++)
		{
			if(usaint_date[i] != null)
			{
				//~�� �ؼ� ������ �� ��¥�� �ִ� ���̰�, ������ �Ϸ縸 ������ �����ִ� ����.
				int check_point = usaint_date[i].indexOf("~");
//				System.out.println("check = "+check_point);
				if(check_point != -1)
				{
					String[] split_format1 = usaint_date[i].trim().split(Pattern.quote("~"));
					int end2 = split_format1[0].indexOf("(");
					split_format1[0] = split_format1[0].substring(0,end2);
					split_format1[0] = split_format1[0].trim().replace(".","-");
//					System.out.println(test[0].trim());
					
					int end = split_format1[1].indexOf("(");
					split_format1[1] = split_format1[1].substring(0,end);
					split_format1[1] = split_format1[1].trim().replace(".","-");
//					System.out.println(test[1].trim());
					
					String start_date = current_year1+split_format1[0] + " 00:00:00";
					String end_date = current_year1+split_format1[1] + " 23:59:59";
//					System.out.println(start_date);
//					System.out.println(end_date);
//					System.out.println(" ");
					usaint_date_token[i][0] = start_date;
					usaint_date_token[i][1] = end_date;
					
					
				}
				else
				{
//					System.out.println(usaint_date[1]);
					String split_format2[] = usaint_date[i].split(" ");
//					System.out.println(split_format2[0]);
					split_format2[0] = split_format2[0].trim().replace(".","-");
					
					
					String start_date = current_year1+split_format2[0] + " 00:00:00";
					String end_date = current_year1+split_format2[0] + " 23:59:59";
//					System.out.println(start_date);
//					System.out.println(end_date);
//					System.out.println(" ");
					usaint_date_token[i][0] = start_date;
					usaint_date_token[i][1] = end_date;
				}
			}
			
		}
	}
	
	
	
	
	

	//���� �Լ� (��üȭ)
	public static void execute_usaint() throws IOException
	{
		//���� �ð��� �������� �Լ� (���ذ� ���� �������� �����;ߵǱ� ����)
		current_time();
		
		//������Ʈ �л����� ũ�Ѹ� �Լ�
		u_saint_date_block_crawl();
		
		//// tokenzier : 01.13 (��) ~ 01.15 (��) ������ -> 2020-01-13 00:00:00 ~ 2020-01-15 00:00:00 �������� ��ȯ
		usaint_date_tokenzier();
		
		System.out.println("=========================================================================");
		System.out.println("�л����� ��� ��...");
		print_schedule_list();
		System.out.println("=========================================================================");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		execute_usaint();
	}

}
