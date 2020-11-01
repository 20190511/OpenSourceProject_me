import java.io.IOException;
//import java.util.HashMap;
//import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
//import java.util.Set;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


 class SmartCampus {
	 	private static Scanner scanner = new Scanner (System.in);
		private static final String Login_URL = 
				"https://myclass.ssu.ac.kr/login/index.php";
		private static final String smart_campus_URL = 
				"http://myclass.ssu.ac.kr/";
		
		private static Map<String,String> cookies;
		private static int count;
		private static String [] subject_title;
		private static String [] subject_link;
		private static String[][][] array_subject_link;  // ���� �߿��� ��ü
		/*(2020.11.01. 14:31 �߰��� �ʵ�*/
		private static String[][][] subject_videoName;
		private static String[][][] subject_videoPeriod;
		private static String[][][] subject_videoLength;
		private static String[][][] subject_videoLate;
		//������ ������ ���� �ʿ䰡 ���ٰ� �Ǵ�. subject_assignmentName[�����][�����̸�] , subject_assignmentPeriond[�����][���� �Ⱓ]
		private static String[][] subject_assignmentName;
		private static String[][] subject_assignmentPeriond;
		
		
		/* �޼ҵ��*/
		public static void login() throws IOException
		{
			//����Ʈ ķ�۽� ID ,PW �޾ƿ� : ��й�ȣ Ʋ���� �� ���� �� ���� -> ���簡 ������ֽñ�
			System.out.println("ID�� �Է��Ͻÿ�");
			String ID = scanner.next();
			System.out.println("PW�� �Է��Ͻÿ�");
			String PW = scanner.next();
			
			//1.login
			Response loginResponse = (Response)Jsoup.connect(Login_URL)
					.data("username", ID)
					.data("password" , PW)
					.method(Method.POST)
					.execute();
			
			//System.out.println("  - PAGE STATUS CODE : " +loginResponse.statusCode() );
//			Document doc = loginResponse.parse();
			//System.out.println("" + doc.toString());
			
			//2.Session ���� ���.
			cookies = loginResponse.cookies();
		}
		
//		System.out.println("=========================================\n");
		//���� �ؽø��� �� �ʿ� ���� ���̶�� �ּ� �޷����� ����ٶ�
//		System.out.println("�����ϴ� ������ �ش� ��ũ\n");
//		for (int i = 0 ; i < startIdx_array.length ; i++)
//			index_subject_link.put(subject_title[i] , subject_link[i]);
		
		
		
		
		//����Ʈķ�۽� ù ������ �ε��� ����
		public static void access_lecture_index() throws IOException
		{
			//3. �� ����	
			Document doc1 = Jsoup.connect(smart_campus_URL)
					.cookies(cookies)
					.get();
//			System.out.println("=========================================\n");
//			System.out.println("SmartCampus Link\n");
//			System.out.println(""+doc1.toString());
//			System.out.println("=========================================\n");
			String docu = doc1.toString();
			
			//int count = 1;
			int startIdxCount = 0;
			int endIdxCount =0;
			//count�� ���ؼ� �� ���ǰ� �� �� �ִ��� �Ǵ�.
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
			subject_title = new String [count];
			subject_link = new String [count];
//			System.out.println(""+count);
			
			int [] startIdx_array = new int [count];
			int [] endIdx_array = new int [count];

			//�ؽð����� ����������� �ǹ̾��� �� ���Ƽ� �ϴ� �ּ�ó��
//			HashMap<String,String> index_subject_link = new HashMap<>() ;
			
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
//				System.out.println(""+docu.substring(startIdx_array[i]+48,endIdx_array[i]));
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
//				System.out.println(""+docu.substring(startIdx_array[i]+48,endIdx_array[i]));
				subject_link[i] = docu.substring(startIdx_array[i]+48,endIdx_array[i]);
			}
		}
		
		/* ���ڿ� �и� �Լ� */
		
		/* ��� �Լ� ���� �޼ҵ� */
		public static String[][][] retrun_array_subject_link()
		{	
			return array_subject_link;
		}
		
		public static String[] return_subject_title()
		{
			return subject_title;
		}
		public static String[] return_subject_link()
		{
			return subject_link;
		}
		public static int return_count()
		{
			return count;
		}
		
		/*Scanner �ݱ� */
		public static void scanner_Close()
		{
			scanner.close();
		}
		
		
		/*����� ���� �Լ� (index_session�� ���ϴ� ������ �迭�� ���� �� ���� */
		public static void execute_debug(int index_session) throws IOException
		{
			if(index_session < count)
			{
				login();
				Document doc_link = Jsoup.connect(subject_link[index_session])
						.cookies(cookies)
						.get();
				String doc_link_string = doc_link.toString(); 
				System.out.println("====================================================");
				System.out.println("subject name = "+subject_title[index_session]);
				System.out.println("" + doc_link_string);
				System.out.println("====================================================");
			}
			else
				System.out.println("not enough array index");
		}
		
		
		/*0�� �ε��� �Լ� (������ , href=�ּ�)�� ���� (�̴� ��ũ�� ���� Ȯ���ϰ� �ϴ°� �� �´ٰ� �Ǵ���)*/
		public static void setup_0_index(String doc_link_string , int section) throws IOException
		{
			//0�� �ε������� �ش� ������ ��ũ�� ����.
//			login();
//			access_lecture_index();
//			array_subject_link = new String [count][16][15];
//			String doc_link_string = doc_link.toString(); 
			String start_index_0 = "<h3 class=\"sectionname accesshide\"><span>���� ����</span></h3>";
			String end_index_0 = "<div class=\"course_box course_box_current\">";
			int index_0_start = doc_link_string.indexOf(start_index_0);
			int index_0_end = doc_link_string.indexOf(end_index_0);
//			System.out.println(""+doc_link_string.substring(index_0_start,index_0_end));
			int index0_count = 0;
			int start_point = 0;
			int end_point = 0;
			//�ش� ���� ��
			int start_assignmentName = 0;
			int end_assignmentName = 0;
			int index0_point = 0;
			while(true)
			{
				String assignment_point = "<a class=\"\" onclick=\"\" href=\"http://myclass.ssu.ac.kr/mod/assign/";
				String assignment_URL = "<a class=\"\" onclick=\"\" ";
				
				String assignment_name = "alt=\"����\" class=\"activityicon\"><span class=\"instancename\">";
				
				if(index0_count == 0)
				{
					start_point = doc_link_string.indexOf(assignment_point, index_0_start);
					end_point = doc_link_string.indexOf("\"><img src=\"http://myclass.ssu.ac.kr/local/ubion/pix/course_format/mod_icon/assign.png\"",index_0_start);
					
					start_assignmentName = doc_link_string.indexOf(assignment_name, index_0_start);
					end_assignmentName = doc_link_string.indexOf("<span class=\"accesshide \"> ����</span></span></a>", index_0_start);
					
					index0_point = start_point;
				}
				else
				{
					start_point = doc_link_string.indexOf(assignment_point, end_point);
					end_point = doc_link_string.indexOf("\"><img src=\"http://myclass.ssu.ac.kr/local/ubion/pix/course_format/mod_icon/assign.png\"",end_point+1);
					
					start_assignmentName = doc_link_string.indexOf(assignment_name, end_assignmentName);
					end_assignmentName = doc_link_string.indexOf("<span class=\"accesshide \"> ����</span></span></a>", end_assignmentName+1);
				}
				
				if(index0_count == 0)
				{
					if(end_point>index_0_end || start_point == -1 || end_point == -1)
						break;
				}
				else
				{
					if(end_point>index_0_end || start_point == -1 || end_point == -1 || start_point == index0_point)
						break;
				}
				
				
				array_subject_link[section][0][index0_count] = doc_link_string.substring(start_assignmentName+assignment_name.length(),end_assignmentName)+","+doc_link_string.substring(start_point+assignment_URL.length(),end_point);
				System.out.println(""+array_subject_link[2][0][index0_count]);
				index0_count++;
			}
		}
		
		public static void setup_other_index(String doc_link_string , int section) throws IOException
		{
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
				System.out.println("==========================================================================");
				sen = Integer.toString(k+1);
				//1������ ���ܷ� �ϳ� ���� (first �������� �����ؾߵǱ� ����)
				if (k == 1)
				{
					System.out.println("k = "+k+"����");
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",first);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix\" role=\"region\" aria-label=",first);
					first_index = doc_link_string.indexOf("<span class=\"instancename\">", startIdxLink);
				}
				else 
				{
					System.out.println("k = "+ k+"����");
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",endIdxLink);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix\" role=\"region\" aria-label=",endIdxLink+len.length());

				}
				//���Ǹ� �߰��ϴ� �ڵ� (�� ���� �ִ� �͵� ��� ������ ��, 1��° �޺��� ������, 0��° �ε������� ��ǻ�ͱ����� ���� �� �տ� ��� ������ ���� ����.
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
					if(k == 1)
					{
						if(lecture_start == -1 || lecture_start < startIdxLink || lecture_end > endIdxLink)
							break;
					}
					else
					{
						if(lecture_start == -1 || lecture_start == first_index ||lecture_end > endIdxLink)
							break;
					}
					array_subject_link[section][k][count2] = doc_link_string.substring(lecture_start+len_start.length(),lecture_end);
//					System.out.println("count = "+count2+"\n"+array_subject_link[section][k][count2]);
					System.out.println(""+array_subject_link[section][k][count2]);
					count2++;
				}
				System.out.println("==========================================================================");
			}
		}
		
		public static void execution_crawling() throws IOException
		{
			//array_subject_link[���� �ε���][��][�ڷ�]
			array_subject_link = new String [count][16][15];
			for (int section = 0 ; section < count ; section++)
			{
				//����� ���
				System.out.println("\n============================================================================");
				System.out.println(section+1+"�� ���Ǹ� " + subject_title[section]);
				System.out.println("============================================================================\n");
				//��ũ�� ���� ���� ����
				Document doc_link = Jsoup.connect(subject_link[section])
						.cookies(cookies)
						.get();
				String doc_link_string = doc_link.toString();
				System.out.println("==========================================================================");
				System.out.println("k = 0����(���� ���信 �ִ� ���� ���ϵ�)");
				setup_0_index(doc_link_string, section);
				System.out.println("==========================================================================");
				setup_other_index(doc_link_string,section);
			}
		}
		
		
		//���� �Լ� (�α��� -> ������ ũ�Ѹ�)
		public static void execute() throws IOException
		{
			login();
			access_lecture_index();
			execution_crawling();
			scanner_Close();
		}
		
		
		/*Ŭ�Ѹ� ������ �м� */
		public static String judgeFunction (String sentence) 
		{
			if(sentence == null)
				return "other";
			String start_len ="<span class=\"accesshide \"> ";
			String end_len = "</span></span>";
			String start_len2 = "</span>";
			String end_len_2 = "</span></a>";
			int video_start = sentence.indexOf(start_len);
			int video_end = sentence.indexOf(end_len);
			int assign_start = sentence.indexOf(start_len2);
			int assign_end = sentence.indexOf(end_len_2);
			
			if (assign_end- assign_start-start_len2.length()!= 0)
				return "assignment";
			else if (sentence.substring(video_start+start_len.length(), video_end).equals("Commons"))
				return "video";
			else
				return "other";
		}
		
		private static String video_name(String sentence)
		{
			int end = sentence.indexOf("<span class=\"accesshide \">");
			return sentence.substring(0, end);
		}
		
		private static String video_period(String sentence)
		{
			String video_period = "<span class=\"displayoptions\"><span class=\"text-ubstrap\">&nbsp;";
			int start = sentence.indexOf(video_period);
			int end = sentence.indexOf("</span></span><span class=\"text-info\">,"); 
			int end2 = sentence.indexOf("</span><span class=\"text-info\">,"); 
			if(end == -1)
			{
				if(end2 == -1)
					return "0000-00-00 00:00:00 ~ 0000-00-00 00:00:00";
				else
					return sentence.substring(start+video_period.length() , end2);
			}
			return sentence.substring(start+video_period.length() , end);
		}
		private static String video_period2 (String sentence)
		{
			String video_period2 = "<span class=\"displayoptions\"><span class=\"text-ubstrap\">&nbsp;";
			int start = sentence.indexOf(video_period2);
			int end = sentence.indexOf(" <span class=\"text-late\">");
			return sentence.substring(start+video_period2.length() , end);
		}
		
		
		private static String video_length(String sentence)
		{
			String video_length = "</span></span><span class=\"text-info\">, ";
			int start = sentence.indexOf(video_length);
			if (start == -1)
			{
				video_length = "</span><span class=\"text-info\">, ";
				start = sentence.indexOf(video_length);
			}
			int end = sentence.indexOf("</span></span>",start+video_length.length());
			return sentence.substring(start+video_length.length() , end);
		}
		//sentence���� period�� ���ߵ�.
		private static String video_late(String sentence)
		{
			String video_late_check = "<span class=\"text-late\">";
			String time_late = "2000-00-00 00:00:00";
			String late_format ="(Late : ";
			String late_format2 ="(���� : ";
			
			int start = sentence.indexOf(video_late_check);
			int start2 = sentence.indexOf(late_format);
			int start3 = sentence.indexOf(late_format2);
			if(start == -1)
				return null;
			else
			{
				if(start2 != -1)
				{
					return sentence.substring(start2+late_format.length() , start2+late_format.length()+time_late.length());
				}
				else if (start3 != -1)
				{
					return sentence.substring(start3+late_format2.length() , start3+late_format2.length()+time_late.length());
				}
			}
			return null;
		}
		
		
		
		
		private static String assign_name (String sentence)
		{
			int end = sentence.indexOf("</span></a> ");
			if(end == -1)
				return "this is not assignment";
			return sentence.substring(0, end);
		}
		private static String assign_period (String sentence)
		{
			String period_assign = "<span class=\"displayoptions\">";
			int start = sentence.indexOf(period_assign);
			int end = sentence.indexOf("</span>",start);
			return sentence.substring(start+period_assign.length() , end);
		}
		
		public static void video_assignment_divide()
		{
			subject_videoName = new String [count][15][15];
			subject_videoPeriod = new String [count][15][15];
			subject_videoLength = new String [count][15][15];
			subject_videoLate = new String[count][15][15];
			subject_assignmentName = new String [count][15];
			subject_assignmentPeriond = new String [count][15];
			
			//1~15���� ������ (���ǿ� ���� ����)
			for(int count_sub = 0 ; count_sub < count ; count_sub++)
			{
				System.out.println("\n============================================================================");
				System.out.println(count_sub+1+"�� ���Ǹ� " + subject_title[count_sub]);
				System.out.println("============================================================================\n");
				for(int week = 1 ; week <= 15 ; week++)
				{
					int week_video_count = 0;
					int week_assign_count = 0;
					for (int data = 0 ; data < 15 ; data++)
					{
						if(judgeFunction(array_subject_link[count_sub][week][data]).equals("video"))
						{
							subject_videoName[count_sub][week][week_video_count] = video_name(array_subject_link[count_sub][week][data]);
							subject_videoPeriod[count_sub][week][week_video_count] = video_period(array_subject_link[count_sub][week][data]);
							subject_videoLength[count_sub][week][week_video_count] = video_length(array_subject_link[count_sub][week][data]);
							subject_videoLate[count_sub][week][week_video_count] = video_late(subject_videoPeriod[count_sub][week][week_video_count]);
							if(subject_videoLate[count_sub][week][week_video_count] != null)
							{
								subject_videoPeriod[count_sub][week][week_video_count] = video_period2(array_subject_link[count_sub][week][data]);
							}
							System.out.println("video name = "+video_name(array_subject_link[count_sub][week][data])+"\nvideo period = "+subject_videoPeriod[count_sub][week][week_video_count]+"\nvideo length = "+video_length(array_subject_link[count_sub][week][data])+
									"\nvideo late = "+subject_videoLate[count_sub][week][week_video_count]+"\n");
							week_video_count++;
							
						}
						else if(judgeFunction(array_subject_link[count_sub][week][data]).equals("assignment"))
						{
							if(assign_name(array_subject_link[count_sub][week][data]).equals("this is not assignment"))
								continue;
							else
							{
								subject_assignmentName[count_sub][week_assign_count] = assign_name(array_subject_link[count_sub][week][data]);
								subject_assignmentPeriond[count_sub][week_assign_count] = assign_period(array_subject_link[count_sub][week][data]);
								System.out.println("assign name = "+assign_name(array_subject_link[count_sub][week][data])+"\nassign period = "+assign_period(array_subject_link[count_sub][week][data])+"\n");
								week_assign_count++;
							}
						}
					}
				}
			}
		}
}



public class test {
	public static void main(String[] args) throws IOException {
		SmartCampus.execute();
		SmartCampus.video_assignment_divide();
	}

}
