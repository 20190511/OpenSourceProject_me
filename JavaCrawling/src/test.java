import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class test {

	private static final String Login_URL = 
			"https://myclass.ssu.ac.kr/login/index.php";
	private static final String smart_campus_URL = 
			"http://myclass.ssu.ac.kr/";
	
	private static Map<String,String> cookies;
	
	
	public static void main(String[] args) throws IOException {
		/*
		Scanner scanner = new Scanner (System.in);
		System.out.println("ID�� �Է��Ͻÿ�");
		String ID = scanner.next();
		System.out.println("PW�� �Է��Ͻÿ�");
		String PW = scanner.next();
		*/
		String ID_m = "20190511";
		String PW_m = "qwert101806!";
		
		
		
		//1.login
		Response loginResponse = (Response)Jsoup.connect(Login_URL)
				.data("username", ID_m)
				.data("password" , PW_m)
				.method(Method.POST)
				.execute();
		
		//System.out.println("  - PAGE STATUS CODE : " +loginResponse.statusCode() );
		Document doc = loginResponse.parse();
		//System.out.println("" + doc.toString());
		
		//2.Session ���� ���.
		cookies = loginResponse.cookies();

		//3. �� ����	
		Document doc1 = Jsoup.connect(smart_campus_URL)
				.cookies(cookies)
				.get();
//		System.out.println("=========================================\n");
//		System.out.println("SmartCampus Link\n");
//		System.out.println(""+doc1.toString());
//		System.out.println("=========================================\n");
		String docu = doc1.toString();
		
		int count = 1;
		int startIdxCount = 0;
		int endIdxCount =0;
		while (true)
		{
			if(count == 1)
			{
				endIdxCount = docu.indexOf("<span style=\"color:#999;\">");
				startIdxCount = docu.indexOf("<div class=\"course-title\">");
				++count;
			}
			else
			{
				endIdxCount = docu.indexOf("<span style=\"color:#999;\">",endIdxCount+50);
				startIdxCount = docu.indexOf("<div class=\"course-title\">",endIdxCount);
				if(startIdxCount == -1)
					break;
				++count;
			}
		}
//		System.out.println(""+count);
		
		int [] startIdx_array = new int [count];
		int [] endIdx_array = new int [count];
		String [] subject_title = new String [count];
		String [] subject_link = new String [count];
		HashMap<String,String> index_subject_link = new HashMap<>() ;
		
		for (int i = 0; i < startIdx_array.length ; i++)
		{
			if(i == 0)
			{
				endIdx_array[i] = docu.indexOf("<span style=\"color:#999;\">");
				startIdx_array[i] = docu.indexOf("<div class=\"course-title\">");
			}
			else
			{
				endIdx_array[i] = docu.indexOf("<span style=\"color:#999;\">",endIdx_array[i-1]+50);
				startIdx_array[i] = docu.indexOf("<div class=\"course-title\">",endIdx_array[i-1]);
			}
//			System.out.println(""+docu.substring(startIdx_array[i]+48,endIdx_array[i]));
			subject_title[i] = docu.substring(startIdx_array[i]+48,endIdx_array[i]);
		}
		
		for (int i = 0; i < startIdx_array.length ; i++)
		{
			if(i == 0)
			{
				endIdx_array[i] = docu.indexOf("\" class=\"course_link\">");
				startIdx_array[i] = docu.indexOf("<div class=\"course_box\">");
			}
			else
			{
				endIdx_array[i] = docu.indexOf("\" class=\"course_link\">",endIdx_array[i-1]+50);
				startIdx_array[i] = docu.indexOf("<div class=\"course_box\">",endIdx_array[i-1]);
			}
//			System.out.println(""+docu.substring(startIdx_array[i]+48,endIdx_array[i]));
			subject_link[i] = docu.substring(startIdx_array[i]+48,endIdx_array[i]);
		}
		
		
//		System.out.println("=========================================\n");
//		System.out.println("�����ϴ� ������ �ش� ��ũ\n");
		for (int i = 0 ; i < startIdx_array.length ; i++)
			index_subject_link.put(subject_title[i] , subject_link[i]);
		
		
		
		
		//4. �ش� ��ũ�� ���� �ֿ� ���� (�� �� �ؾ��� ������ ���� ���ǵ��� ũ�Ѹ��ؿ�)
		//array_subject_link[���� �ε���][��][�ڷ�]
		String[][][] array_subject_link = new String [count][16][15];
		
		for (int section = 0 ; section < count ; section++)
		{
			System.out.println("\n============================================================================");
			System.out.println(section+"�� ���Ǹ� " + subject_title[section]);
			System.out.println("============================================================================\n");
			//��ũ�� ���� ���� ����
			Document doc_link = Jsoup.connect(subject_link[section])
					.cookies(cookies)
					.get();
			String doc_link_string = doc_link.toString(); 

			// 1~15�ޱ����̸� , 0��° ���� ��ǻ�ͱ��� ���� �� �տ� �x�� assignment�� ��� �뵵
//			String[][] week_things = new String[16][15];
			int first = doc_link_string.indexOf("<h2 class=\"main\">���� �� �н� Ȱ��<span class=\"icons\"></span></h2>");
			int startIdxLink = 0;
			int endIdxLink = 0;
			String len = "<li id=\\\"section-\"+sen+\"\\\" class=\\\"section main clearfix\\\" role=\\\"region\\\" aria-label=";
			String sen = "0";
			int first_index = 0;
			//1~16���� ���Ǳ��� �۾�
			for (int k = 1 ; k < 16 ; k++)
			{
				sen = Integer.toString(k+1);
				//1������ ���ܷ� �ϳ� ���� (first���� ���� �����ؾߵǱ� ����)
				if (k == 1)
				{
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",first);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix\" role=\"region\" aria-label=",first);
					System.out.println("==========================================================================");
					int count2 = 0;
					int lecture_start = 0;
					int lecture_end = 0;
					while(true)
					{
						String len_start = "<span class=\"instancename\">";
						if (count2 == 0)
						{
							lecture_start = doc_link_string.indexOf("<span class=\"instancename\">", startIdxLink);
							lecture_end = doc_link_string.indexOf("</div>", lecture_start + len_start.length());
							first_index = lecture_start;
						}
						else
						{
							lecture_start = doc_link_string.indexOf("<span class=\"instancename\">", lecture_end);
							lecture_end = doc_link_string.indexOf("</div>", lecture_start + len_start.length());
						}
						if(lecture_start == -1 || lecture_start < startIdxLink || lecture_end > endIdxLink)
							break;
//						week_things[k][count2] = doc_link_string.substring(lecture_start+len_start.length(),lecture_end);
//						System.out.println("count = "+count2+"\n"+week_things[k][count2]);
						
						array_subject_link[section][k][count2] = doc_link_string.substring(lecture_start+len_start.length(),lecture_end);
						System.out.println("count = "+count2+"\n"+array_subject_link[section][k][count2]);
						count2++;
					}
					System.out.println("==========================================================================");
				}
				else
				{
					System.out.println("k = "+ k+"����");
					
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",endIdxLink);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix\" role=\"region\" aria-label=",endIdxLink+len.length());
					
					System.out.println("==========================================================================");
					int count2 = 0;
					int lecture_start = 0;
					int lecture_end = 0;
					while(true)
					{
						String len_start = "<span class=\"instancename\">";
						if (count2 == 0)
						{
							lecture_start = doc_link_string.indexOf("<span class=\"instancename\">", startIdxLink);
							lecture_end = doc_link_string.indexOf("</div>", lecture_start + len_start.length());
						}
						else
						{
							lecture_start = doc_link_string.indexOf("<span class=\"instancename\">", lecture_end);
							lecture_end = doc_link_string.indexOf("</div>", lecture_start + len_start.length());
						}
						
						//���� ������ �� ������ ���� �ǵ��ư��� ���� ������ lecture_start == first_index  �����Ͽ���
						if(lecture_start == -1 || lecture_start == first_index ||lecture_end > endIdxLink)
							break;
//						week_things[k][count2] = doc_link_string.substring(lecture_start+len_start.length(),lecture_end);
//						System.out.println("count = "+count2+"\n"+week_things[k][count2]);
						
						array_subject_link[section][k][count2] = doc_link_string.substring(lecture_start+len_start.length(),lecture_end);
						System.out.println("count = "+count2+"\n"+array_subject_link[section][k][count2]);
						count2++;
					}
					System.out.println("==========================================================================");
				}

			}
		}

	}

}
