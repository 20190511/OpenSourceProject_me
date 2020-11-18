import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	 	protected static final String Login_URL = 
				"https://myclass.ssu.ac.kr/login/index.php";
	 	protected static final String smart_campus_URL = 
				"http://myclass.ssu.ac.kr/";
		
	 	//���� ������ private -> protected ���� (SmartCampusTokenizer����) _ (2020.11.14_ 20190511������)
	 	protected static Map<String,String> cookies;
	 	protected static int count;
	 	protected static String [] subject_title;
	 	protected static String [] subject_link;
		protected static String[][][] array_subject_link;  // ���� �߿��� ��ü
		/*(2020.11.01. 14:31 �߰��� �ʵ�*/
		protected static String[][][] subject_videoName;
		protected static String[][][] subject_videoPeriod;
		protected static String[][][] subject_videoLength;
		protected static String[][][] subject_videoLate;
		
		/*���� ������Ʈ ��� ��ȯ -> (���� ����Ȯ�� ��Ͽ��� �̸��� ���� ��� ũ�Ѹ� �ؿ�) == �ӵ� ������*/
		//������ ������ ���� �ʿ䰡 ���ٰ� �Ǵ�. subject_assignmentName[�����][�����̸�] , subject_assignmentPeriond[�����][���� �Ⱓ]
//		private static String[][] subject_assignmentName;
//		private static String[][] subject_assignmentPeriond;
		
		/*(2020.11.04 �߰��� �ʵ� */
		//������ ���� ���� ��Ȳ �������� �Ѿ�� ��ũ�� ��Ƶ�
		protected static String[] check_video_link; 
		protected static String[] check_assignment_link;
//		private static String[][] subject_video_lateCheck;
		//���� ��ũ�� o/xüũ�� ���������� ������.
		protected static String[][][] check_video;
		protected static String[][] check_assignment;
		protected static String[][] temp_subject_assignmentName;
		protected static String[][] temp_subject_assignmentPeriond;
		//���� üũ ��Ͽ��� �̹��ֿ� �������� ���Ǹ�� ��� �� ������ ������.
		
		/* �޼ҵ��*/
		public static void login() throws IOException
		{
			//����Ʈ ķ�۽� ID ,PW �޾ƿ� :
			
			System.out.println("ID�� �Է��Ͻÿ�");
			String ID_scan = scanner.next();
			System.out.println("PW�� �Է��Ͻÿ�");
			String PW_scan = scanner.next();
			
			//�� �� ���� ����
			String ID = ID_scan.trim();
			String PW = PW_scan.trim();
			
			
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
		public static String execute_debug(int index_session) throws IOException
		{
			login();
			access_lecture_index();
			if(index_session < count)
			{
				Document doc_link = Jsoup.connect(subject_link[index_session])
						.cookies(cookies)
						.get();
				String doc_link_string = doc_link.toString(); 
				System.out.println("====================================================");
				System.out.println("subject name = "+subject_title[index_session]);
				System.out.println("" + doc_link_string);
				System.out.println("====================================================");
				return doc_link_string;
			}
			else
				System.out.println("not enough array index");
				return "-1";
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
//				System.out.println(""+array_subject_link[2][0][index0_count]);
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
			
			/*
			System.out.println("==============================");
			System.out.println(doc_link_string);
			System.out.println("==============================");
			*/
			//1~16���� ���Ǳ��� �۾�
			for (int k = 1 ; k < 16 ; k++)
			{
				
//				System.out.println("==========================================================================");
				sen = Integer.toString(k+1);
				//1������ ���ܷ� �ϳ� ���� (first �������� �����ؾߵǱ� ����)
				if (k == 1)
				{
//					System.out.println("k = "+k+"����");
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",first);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix",first);
					first_index = doc_link_string.indexOf("<span class=\"instancename\">", startIdxLink);
				}
				else 
				{
//					System.out.println("k = "+ k+"����");
					startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>",endIdxLink);
					endIdxLink = doc_link_string.indexOf("<li id=\"section-"+sen+"\" class=\"section main clearfix",endIdxLink+len.length());
			
					if(Integer.parseInt(sen) == 16)
					{
						startIdxLink = doc_link_string.indexOf("<h3 class=\"sectionname\"><span>15����",endIdxLink);
						endIdxLink = doc_link_string.indexOf("<div class=\"coursemos-course-menu-expand\">",endIdxLink+len.length());
					}
//					System.out.println(doc_link_string.substring(startIdxLink,endIdxLink));
				}
				//���Ǹ� �߰��ϴ� �ڵ� (�� ���� �ִ� �͵� ��� ������ ��, 1��° �޺��� ������, 0��° �ε������� ��ǻ�ͱ����� ���� �� �տ� ��� ������ ���� ����.
				int count2 = 0;
				int lecture_start = 0;
				int lecture_end = 0;
				
				
				/*------------------2020 . 11 . 05 �߰��� ���� ������� Ȯ���ϴ� �Լ� ����-------------------- */
//				System.out.println("=====/===============================");
//				System.out.println("link = "+check_video_link[section]);
//				count_video_check = video_check_function(section , count_video_check,k);
//				System.out.println("count_video_check = "+count_video_check);
//				System.out.println("====================================");
				/*----------------------------------------------------*/
				
				
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
//					System.out.println("count = "+count2+"\n"+array_subject_link[section][k][count2]); //�� ����(����)�� ���
//					System.out.println(""+array_subject_link[section][k][count2]); //�� ������ ���
					count2++;
				}
//				System.out.println("==========================================================================");
			}
		}
		
		public static void execution_crawling() throws IOException
		{
			//array_subject_link[���� �ε���][��][�ڷ�]
			array_subject_link = new String [count][16][15];
			check_video_link = new String [count];
			check_assignment_link = new String [count];
			
			for (int section = 0 ; section < count ; section++)
			{
				//����� ���
				/*
				System.out.println("\n============================================================================");
				System.out.println(section+1+"�� ���Ǹ� " + subject_title[section]);
				System.out.println("============================================================================\n");
				*/
				//��ũ�� ���� ���� ����
				Document doc_link = Jsoup.connect(subject_link[section])
						.cookies(cookies)
						.get();
				String doc_link_string = doc_link.toString();
				/*
				System.out.println("==========================================================================");
				System.out.println("k = 0����(���� ���信 �ִ� ���� ���ϵ�)");
				*/
//				System.out.println(subject_title[section]+"������ ������� ���� ��ũ �迭");
//				System.out.println("video link = "+check_video_link[section]+"\nassign link = "+check_assignment_link[section]);
				
				
				setup_0_index(doc_link_string, section);
//				System.out.println("==========================================================================");
				setup_other_index(doc_link_string,section);
				
//				System.out.println("==========================================================================");


			}
		}
		
		
		//���� �Լ� (�α��� -> ���� �ε��� ����/��ũ ũ�Ѹ� -> ���� �̸� ���� -> ����/������ ������Ȳ ũ�Ѹ�)
		public static void execute() throws IOException
		{
			//Login �߸� �Է����� �� ����.
			System.out.println("======================================================================");
			is_login();
			System.out.println("======================================================================");
			System.out.println("A. �α��� ��....");
			access_lecture_index();
			System.out.println("======================================================================");
			System.out.println("B. ���� ��� Ȯ�� ��(���� ! ����Ʈ ķ�۽����� ������ �س��� ��� ��Ÿ���� ����)....");
			System.out.println("======================================================================");
			execution_crawling();
			System.out.println("======================================================================");
			System.out.println("C. ���� , ���� ������Ʈ �� ....");
			System.out.println("======================================================================");
			video_assignment_divide();
			//������ ���� �迭 ���
			System.out.println("======================================================================");
			System.out.println("D. ������ �̸�/����/����/�����ð�/������Ȳ ��� �غ� �� ....");
			System.out.println("======================================================================");
			return_videoName_attendance();
			
			System.out.println("======================================================================");
			System.out.println("E. ������ �̸�/����/����/�����ð�/������Ȳ ��� �غ� �� ....");
			System.out.println("======================================================================");
			//���� ���� �迭 ���
			return_assign_attendance();
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
		
		
		
		//���� ũ�Ѹ� ��� �������� ���� �ı� �Լ���
		/*
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
		*/
		public static void video_assignment_divide() throws IOException
		{
			subject_videoName = new String [count][15][15];
			subject_videoPeriod = new String [count][15][15];
			subject_videoLength = new String [count][15][15];
			subject_videoLate = new String[count][15][15];
//			subject_assignmentName = new String [count][15];
//			subject_assignmentPeriond = new String [count][15];
			
			/*2020.11.05 �߰��� ����,���� ��û ��Ȳ üũ �ʵ� ������ */
			check_video = new String [count][16][15];
			//���� ��� ���� ���̵� ������ 
			check_assignment = new String [count][40];
			temp_subject_assignmentName = new String [count][40];
			temp_subject_assignmentPeriond = new String [count][40];
			
			//1~15���� ������ (���ǿ� ���� ����)
			for(int count_sub = 0 ; count_sub < count ; count_sub++)
			{
				/*
				System.out.println("\n============================================================================");
				System.out.println(count_sub+1+"�� ���Ǹ� " + subject_title[count_sub]);
				System.out.println("============================================================================\n");
				*/
				//������ ,���� ��Ȳ�� �� �� �ִ� ��ũ ũ�Ѹ� +-> ������ ���� ��� "����"���� �����Ƿ�, null�� �����  --> check_video_assignment_link[������ ��ũ][���� ��ũ]
				
				
				System.out.println(count_sub+1+"�� ���Ǹ� " + subject_title[count_sub]+" �м��� .... ("+(count_sub+1)+"/"+count+")");
				
				//üũ �ε���SS
				check_assignment(count_sub);
				Document doc_video_check = Jsoup.connect(check_video_link[count_sub])
						.cookies(cookies)
						.get();
//				System.out.println("link = "+check_video_link[count_sub]);
//				System.out.println("assign = "+check_assignment_link[count_sub]);
				Document doc_assignment_check = Jsoup.connect(check_assignment_link[count_sub])
						.cookies(cookies)
						.get();
				
				
				assignment_check_function(doc_assignment_check ,count_sub);
				
				int count_video_check = 0;
				
				for(int week = 1 ; week <= 15 ; week++)
				{
					//������ ������Ȳ Ȯ��
					count_video_check = video_check_function(doc_video_check ,count_sub, count_video_check,week);
					
					int week_video_count = 0;
//					int week_assign_count = 0;
					for (int data = 0 ; data < 15 ; data++)
					{
						if(judgeFunction(array_subject_link[count_sub][week][data]).equals("video"))
						{ 	
//							System.out.println("data = "+array_subject_link[count_sub][week][data]);
							subject_videoName[count_sub][week][week_video_count] = video_name(array_subject_link[count_sub][week][data]);
							subject_videoPeriod[count_sub][week][week_video_count] = video_period(array_subject_link[count_sub][week][data]);
							subject_videoLength[count_sub][week][week_video_count] = video_length(array_subject_link[count_sub][week][data]);
							subject_videoLate[count_sub][week][week_video_count] = video_late(subject_videoPeriod[count_sub][week][week_video_count]);
							if(subject_videoLate[count_sub][week][week_video_count] != null)
							{
								subject_videoPeriod[count_sub][week][week_video_count] = video_period2(array_subject_link[count_sub][week][data]);
							}
							/*
							System.out.println("video name = "+video_name(array_subject_link[count_sub][week][data])+"\nvideo period = "+subject_videoPeriod[count_sub][week][week_video_count]+"\nvideo length = "+video_length(array_subject_link[count_sub][week][data])+
									"\nvideo late = "+subject_videoLate[count_sub][week][week_video_count]+"\nvideo Attendance(% or O/X) = "+check_video[count_sub][week][data]+"\n");
							*/
							week_video_count++;
							
						}
						//���� ���̵� ���ö� �ʿ������
						/*
						else if(judgeFunction(array_subject_link[count_sub][week][data]).equals("assignment"))
						{
							if(assign_name(array_subject_link[count_sub][week][data]).equals("this is not assignment"))
								continue;
							else
							{
								subject_assignmentName[count_sub][week_assign_count] = assign_name(array_subject_link[count_sub][week][data]);
								subject_assignmentPeriond[count_sub][week_assign_count] = assign_period(array_subject_link[count_sub][week][data]);
//								System.out.println("assign name = "+assign_name(array_subject_link[count_sub][week][data])+"\nassign period = "+assign_period(array_subject_link[count_sub][week][data])+"\n");
								week_assign_count++;
							}
						}
						*/
					}
				}
			}
		}
		
		
		
		
		/*2020.11.04 �߰��� �޼ҵ� , */
		 
		//Crawling �Լ� (ũ�Ѹ� ���� �Լ�) -> �� ���� �ٿ��� ũ�Ѹ��� �ϴ°��
		public static String crawl_start(String crawl_HTML , String end_string)
		{
			int end = crawl_HTML.indexOf(end_string);
			if(end == -1)//ũ�Ѹ� �����Ͱ� ���� ���.
				return "-1";
			return crawl_HTML.substring(0, end);
		}
		// �߰� �κ� �ؽ�Ʈ�� ũ�Ѹ� �ϴ� ��� , -1�� ������, ���� �ε��� ��ȣ�� ������ �ǹ���.
		public static String crawl_middle(String crawl_HTML ,String start_string, String end_string)
		{
			int start_len = start_string.length();
			int start = crawl_HTML.indexOf(start_string);
			int end = crawl_HTML.indexOf(end_string,start);


			if(start == -1)
				return "Start can't be found";
			else if(end == -1)
				return "End can't be found";
			else if(start > end)
				return "start > end";
			return crawl_HTML.substring(start+start_len, end);
		}
		//������ �κ��� ũ�Ѹ� �ϴ� ���
		public static String crawl_end(String crawl_HTML , String start_string)
		{
			int start_len = start_string.length();
			int start = crawl_HTML.indexOf(start_string);
			if (start == -1)
				return "-1";
			return crawl_HTML.substring(start+start_len);
		}
		
		
		
		 //���� ������ ���� ��Ȳ�� �����ִ� ��ũ�� ��� �迭�� ���� ũ�Ѹ� �Լ�
		public static void check_assignment(int section) throws IOException 
		{
			/*���� ��� ��� ���� �ϰ��� */
//			System.out.println(subject_link[0]);
			String link_id = "http://myclass.ssu.ac.kr/course/view.php";
			String id = crawl_end(subject_link[section],link_id);
			//ID ������� ��ũ ������ (������ ���)
			String video_link ="http://myclass.ssu.ac.kr/report/ubcompletion/progress.php"+id;
			check_video_link[section] = video_link;
			
			String assign_link = "http://myclass.ssu.ac.kr/mod/assign/index.php"+id;
//			System.out.println(assign_link);
			check_assignment_link[section] = assign_link;
			
			
		}
		
		
		//���� , X , �� ���� ���� �ε��� ����
		public static int small_index_return(int value1 , int value2 , int value3)
		{
			if (value1 == -1 && value2 == -1 && value3 == -1)
				return -1;
			int va1 = value1;
			int va2 = value2;
			int va3 = value3;
			
			//�Ű������� �ٲٸ� �ν� �Ұ�
			if(va1 == -1)
				va1 = 999999999;
			if(va2 == -1)
				va2 = 999999999;
			if(va3 == -1)
				va3 = 999999999;
			
			int small_est = va1;
			if(va2 < va1)
				small_est = va2;
			if(va3 < va2)
				small_est = va3;
			return small_est;
		}
		
		//������ ������Ȳ üũ �Լ�
		public static int video_check_function(Document doc_video_check ,int section, int length , int k) throws IOException
		{
			/*������ ��ũ���� ������ ������Ȳ üũ*/
			
			String doc_video_check_string = doc_video_check.toString();
//			System.out.println(doc_video_check_string);
			
			//����� ���
			String check_video_offline_block_open = "<div class=\"sectiontitle\" title=\"";
			String check_video_offline_block_close = "class=\"vmiddle text-center\">";
			int offline_check_previous_count = doc_video_check_string.indexOf(check_video_offline_block_close);
			
			
			//������ ���
			String check_video_online_block_open = "<td class=\"text-center\">"+Integer.toString(k)+"</td>";
			int online_check_previous_count00 = doc_video_check_string.indexOf(check_video_online_block_open,length);
			String check_video_online_block_middle = "</tr>";
			int online_check_previous_count0 = doc_video_check_string.indexOf(check_video_online_block_middle,online_check_previous_count00);
			String check_video_online_block_close = "<td class=\"text-center\" rowspan=";
			int online_check_previous_count = doc_video_check_string.indexOf(check_video_online_block_close,online_check_previous_count0);
			
			if (online_check_previous_count00 != -1)
			{
//				System.out.println(doc_video_check_string.substring(online_check_previous_count00,online_check_previous_count0));
				online_check_previous_count = online_check_previous_count0;
			}
			else
			{
				check_video_online_block_open = "<td class=\"text-center\" rowspan=";
				online_check_previous_count00 = doc_video_check_string.indexOf(check_video_online_block_open,length);
				if(online_check_previous_count00 == -1 && offline_check_previous_count == -1)
				{
					System.out.println("���α׷����� ã�� �� ���� ���ǰ� ������ ���� �ֽ��ϴ�. (�����ð��� �������� �ʴ� ���)");
					return doc_video_check_string.length();
				}
				check_video_online_block_middle = "</tr>";
				online_check_previous_count0 = doc_video_check_string.indexOf(check_video_online_block_middle,online_check_previous_count00);
				check_video_online_block_close = "<td class=\"text-center\" rowspan=";
				online_check_previous_count = doc_video_check_string.indexOf(check_video_online_block_close,online_check_previous_count0);
				
				//���� ������ ��� ���� ������ rowsapn�� ���� ������ üŷ�� ���� ���
				String if_check_video_online_overWeek = "<td class=\"text-center\">"+Integer.toString(k+1)+"</td>";
				int online_check_previous_count1 = doc_video_check_string.indexOf(if_check_video_online_overWeek,online_check_previous_count00);
				
				if(online_check_previous_count1 != -1 && offline_check_previous_count == -1)
				{
					online_check_previous_count = online_check_previous_count1;
				}
				else if(online_check_previous_count == -1 && offline_check_previous_count == -1)
				{
//					System.out.println(doc_video_check_string.substring(online_check_previous_count00,online_check_previous_count0));
					online_check_previous_count = online_check_previous_count0;
				}
			}			
			if(offline_check_previous_count != -1)
			{
				
//				System.out.println("OffLine");
				//�������� ���� ���� ����� �޶� üŷ���ֱ� �ٍ�.
				//return ���̰� ���� crawl_middle()�� �־����� ����.
//				int length = check_video_offline_block_close.length();

				
				int offline_check_start = doc_video_check_string.indexOf(check_video_offline_block_open,length);
				int offline_check_end = doc_video_check_string.indexOf(check_video_offline_block_close,offline_check_start);
				if(offline_check_end == -1)
					return doc_video_check_string.length();
				
				//������ �����ؾ��ϴ� ��.
				int length_offline = offline_check_end;
				
				String check_video_offline_block = doc_video_check_string.substring(offline_check_start,offline_check_end);
//				System.out.println("========================================");
//				System.out.println(check_video_offline_block);
				
				String len_offline = "</button></td>";
				int next_value = check_video_offline_block.indexOf(len_offline);
				int count_offline = 1;
//				System.out.println("=========================================");
				int index_start = 0;
				int check_recursive = 0;
				int index_end = 0;
				while(true)
				{
					String check_video_offline_start = "<td class=\"text-center\">";
					String check_video_offline_end = "%";
					int length2 = check_video_offline_start.length();
					int length3 = check_video_offline_end.length();
					index_start = check_video_offline_block.indexOf(check_video_offline_start,next_value);
					index_end = check_video_offline_block.indexOf(check_video_offline_end,next_value);


					if(index_start == -1 || index_end==-1 || index_start>index_end || (check_recursive > index_start && count_offline != 1))
						break;
					next_value = check_video_offline_block.indexOf("</button></td>",index_end+length3);
//					System.out.println("start = " + index_start+", end = "+index_end);
//					System.out.println(check_video_offline_block.substring(index_start+length2,index_end+1));
					check_video[section][k][count_offline-1] = check_video_offline_block.substring(index_start+length2,index_end+1);
					count_offline++;
					check_recursive = index_start;
				}
				return length_offline /*+ check_video_offline_block_close.length()*/;
			}
			else
			{
				
//				System.out.println(doc_video_check_string);
//				System.out.println("OnLine");
				
				int online_check_start = online_check_previous_count00;
				int online_check_end = online_check_previous_count;
				
				//������ �����ؾ��ϴ� ��
				int length_online = online_check_end;
				String check_video_online_block = doc_video_check_string.substring(online_check_start,online_check_end);
				
				/*
				System.out.println("=========================================");
				System.out.println(check_video_online_block);
				System.out.println("=========================================");
				*/
				
				String len_online = "</button></td>";
				int next_value = check_video_online_block.indexOf(len_online);
				String len_online2 = "</tr>";
				int next_value_total = 0;
				
				int count_online = 1;
				int index_start_O = 0;
				int index_start_X = 0;
				int index_start_another = 0;
//				int index_end = 0;
			
				while(true)
				{
					String length_cal = "<td class=\"text-center\">";
					//O
					String check_video_online_check_point_O =  "<td class=\"text-center\">O</td>"; 
					//X
					String check_video_online_check_point_X =  "<td class=\"text-center\">X</td>"; 
					//�� (2020.11.14 �߰��� �ʵ�)
					String check_video_online_check_point_another =  "<td class=\"text-center\">��</td>"; 
					
//					int length_O = check_video_online_check_point_O.length(); //���̴� O/X ����
//					int length_X = check_video_online_check_point_X.length();
					index_start_O = check_video_online_block.indexOf(check_video_online_check_point_O,next_value_total);
					index_start_X = check_video_online_block.indexOf(check_video_online_check_point_X,next_value_total);
					index_start_another = check_video_online_block.indexOf(check_video_online_check_point_another,next_value_total);
					
//					System.out.println(" ( index_start_O , index_start_X , index_start_another) = ( "+index_start_O+" , "+index_start_X+" , "+index_start_another+" )");
					int small_index = small_index_return (index_start_O , index_start_X , index_start_another);
//					System.out.println("small index = "+small_index);
					if(small_index == -1 || next_value_total == -1)
					{
//						System.out.println("break + count = "+count_online);
						break;
					}
					else
					{
						next_value = check_video_online_block.indexOf(len_online2,small_index) + len_online2.length();
//						System.out.println("start = " + index_start_X+", end = "+(index_start_X+1));
//						System.out.println(check_video_online_block.substring(index_start_X+length_cal.length(),index_start_X+length_cal.length()+1));
						check_video[section][k][count_online-1] = check_video_online_block.substring(small_index+length_cal.length(),small_index+length_cal.length()+1);
//						System.out.println(check_video_online_block.substring(small_index+length_cal.length(),small_index+length_cal.length()+1));
						count_online++;
						next_value_total = next_value;
					}
					
					//�ٸ� ��� ��Ŀ������ ã�Ƴ��� �ӵ� ���� (2020 . 11. 14 _ 20190511)
					/*
					else if(index_start_O == -1 && index_start_X != -1) //X
					{
						next_value = check_video_online_block.indexOf(len_online2,index_start_X) + len_online2.length();
//						System.out.println("start = " + index_start_X+", end = "+(index_start_X+1));
//						System.out.println(check_video_online_block.substring(index_start_X+length_cal.length(),index_start_X+length_cal.length()+1));
						check_video[section][k][count_online-1] = check_video_online_block.substring(index_start_X+length_cal.length(),index_start_X+length_cal.length()+1);
//						System.out.println("X");
						count_online++;
						next_value_total = next_value;
					}
					else if (index_start_O != -1 && index_start_X == -1) //O
					{
						next_value = check_video_online_block.indexOf(len_online2,index_start_O) + len_online2.length();
//						System.out.println("start = " + index_start_O+", end = "+(index_start_O+1));
//						System.out.println(check_video_online_block.substring(index_start_O+length_cal.length(),index_start_O+length_cal.length()+1));
						check_video[section][k][count_online-1] = check_video_online_block.substring(index_start_O+length_cal.length(),index_start_O+length_cal.length()+1);
//						System.out.println("O");
						count_online++;
						next_value_total = next_value;
					}
					else if (index_start_O != -1 && index_start_X != -1) //O X ���� �ִ� ��� ó��
					{
//						System.out.println("O/X = "+index_start_O+"/"+index_start_X);
						//���� �ִ��� �� ���� ó���ϵ��� �Ѵ�. (������ ���� �� �ֱ� �����̴�.)
						if(index_start_O < index_start_X)
						{
//							next_value = check_video_online_block.indexOf(len_online2,index_start_O) + len_online2.length();
							next_value = index_start_X;
//							System.out.println("start = " + index_start_O+", end = "+(index_start_O+1));
//							System.out.println(check_video_online_block.substring(index_start_O+length_cal.length(),index_start_O+length_cal.length()+1));
							check_video[section][k][count_online-1] = check_video_online_block.substring(index_start_O+length_cal.length(),index_start_O+length_cal.length()+1);
//							System.out.println("O");
							count_online++;
							next_value_total = next_value;
						}
						else
						{
//							next_value = check_video_online_block.indexOf(len_online2,index_start_X) + len_online2.length();
							next_value = index_start_O;
//							System.out.println("start = " + index_start_X+", end = "+(index_start_X+1));
//							System.out.println(check_video_online_block.substring(index_start_X+length_cal.length(),index_start_X+length_cal.length()+1));
							check_video[section][k][count_online-1] = check_video_online_block.substring(index_start_X+length_cal.length(),index_start_X+length_cal.length()+1);
//							System.out.println("X");
							count_online++;
							next_value_total = next_value;
						}
//						System.out.println("next_value = "+next_value_total);
					 	*/
					
				}
//				System.out.println("finish");
				return length_online;
				
			}

		}
		
		public static void assignment_check_function (Document doc , int section) throws IOException
		{			
//			System.out.println(doc);
			String doc_assignment = doc.toString();
			String doc2 = "�� ���¿��� ������ �����ϴ�.";
			int check_assign = doc_assignment.indexOf(doc2);
			if(check_assign == -1)
			{
//				System.out.println(doc_assignment);
//				String assign_style = "<td class=\"cell c3\" style=\"text-align:right;\">";
				
				//�ش� ���� ��
				String assign_check_block_open = "<tbody>";
				String assign_check_block_close = "</tbody>";
				String assign_check_block = crawl_middle(doc_assignment,assign_check_block_open,assign_check_block_close);
//				System.out.println(assign_check_block);
				
				//���� �̸� , ���� ���� , ���� ���� ��Ȳ �ܾ����
				int start_point = 0;
				int count_assign = 0;
				while(true)
				{
//					System.out.println("=====================================================================================================");
//					System.out.println("count = "+count_assign);
					String assign_name_start = "<td class=\"cell c1\" style=\"text-align:left;\"><a href=\"http://myclass.ssu.ac.kr/mod/assign/view.php?id=";
					String assign_name_end = "</a></td>";
					int assign_name_start_index = assign_check_block.indexOf(assign_name_start,start_point) ;
					int assign_name_end_index = assign_check_block.indexOf(assign_name_end,assign_name_start_index) ;
					
					String assign_period_start = "<td class=\"cell c2\" style=\"text-align:center;\">";
					String assign_period_end = "</td>";
					int assign_period_start_index = assign_check_block.indexOf(assign_period_start,assign_name_end_index) ;
					int assign_period_end_index = assign_check_block.indexOf(assign_period_end,assign_period_start_index) ;
					
					String assign_check_start = "<td class=\"cell c3\" style=\"text-align:right;\">";
					String assign_check_end = "</td>";
					int assign_check_start_index = assign_check_block.indexOf(assign_check_start,assign_period_end_index) ;
					int assign_check_end_index = assign_check_block.indexOf(assign_check_end,assign_check_start_index) ;
					
					if(assign_name_start_index == -1 || ((start_point > assign_name_start_index) &&  assign_name_start_index != -1))
					{
						break;
					}
					
					String assign_name_unmodified = assign_check_block.substring(assign_name_start_index+assign_name_start.length(), assign_name_end_index);
					String assign_period = assign_check_block.substring(assign_period_start_index+assign_period_start.length() , assign_period_end_index);
					String assign_check = assign_check_block.substring(assign_check_start_index+assign_check_start.length() , assign_check_end_index);
					
					String modify_assign_name = ">";
					int modify_assign_name_index = assign_name_unmodified.indexOf(modify_assign_name);
					String assign_name = assign_name_unmodified.substring(modify_assign_name_index+modify_assign_name.length());
					
//					System.out.println("���� ��  = "+assign_name+"\n���� ���� = "+assign_period+"\n���� üũ = "+assign_check);
					
					temp_subject_assignmentName [section][count_assign] = assign_name;
					temp_subject_assignmentPeriond[section][count_assign] = assign_period;
					check_assignment [section][count_assign] = assign_check;
					
					start_point = assign_check_end_index+assign_check_end.length();
					
					
//					System.out.println("=====================================================================================================");
					count_assign++;
				}
			}

			
			/*���� ������Ȳ üũ*/
			/*
			Document doc_video_check = Jsoup.connect(check_assignment_link[0])
					.cookies(cookies)
					.get();
			String doc_assignment_check_string = doc_video_check.toString();
			System.out.println(doc_assignment_check_string);
			*/
		}
		
		
	
		
		
		//video ���� �迭 ��¹�
		 public static void return_videoName_attendance()
		 {
			 for(int i = 0 ; i < count ; i++)
			 {
				 System.out.println("====================================================================================");
				 System.out.println("subject = "+subject_title[i]);
				 System.out.println();
				 for(int j = 0 ; j < 15 ; j++)
				 {
					 System.out.println("------------------------------------------------------");
					 System.out.println("week = "+(j+1));

					 if(j != 0 && check_video[i][j][0] == null)
					 {
						 System.out.println((j+1)+"�ֿ��� ���ǰ� �ö������ �ʰų� �⼮������ �̹� �������ϴ�.");
					 }
					 for(int k = 0 ; k < 15 ; k++)
					 {
						 if(subject_videoName[i][j][k] != null && check_video[i][j][k] != null)
						 {
//							 System.out.println("subject_videoName["+i+"]["+j+"]["+k+"] = "+subject_videoName[i][j][k]);
//							 System.out.println("check_video["+i+"]["+j+"]["+k+"] = "+check_video[i][j][k]);
							 
							 System.out.println("���� �� = "+subject_videoName[i][j][k]);
							 System.out.println("���� �Ⱓ  = "+subject_videoPeriod[i][j][k]);
							 System.out.println("���� ����  = "+subject_videoLength[i][j][k]);
							 if(subject_videoLate[i][j][k] != null)
							 {
								 System.out.println("���� �Ⱓ(�ִ°��)  = "+subject_videoLate[i][j][k]);
							 }
							 System.out.println("���� Ȯ��(O/X or %) = "+check_video[i][j][k]);
							 System.out.println();
							 
						 }
						
					 }
					 System.out.println();
					 System.out.println("------------------------------------------------------");
				 }
				 System.out.println("====================================================================================");
			 }
					 
		 }
		 
		 
		//����(assignment)���� �迭 ȣ�⹮
		 public static void return_assign_attendance ()
		 {
			 for (int section_s = 0 ; section_s < count ; section_s++)
			 {
				 
				 System.out.println("======================================================================");
				 System.out.println(section_s+1 + "�� ���� ["+subject_title[section_s]+"]�� ���� ��ϰ� �ڷ��");
				 if(temp_subject_assignmentName[section_s][0] == null)
					 System.out.println("�ش� ������ ������ �������� �ʽ��ϴ�.");
				 for(int count_assign = 0 ; count_assign < 40 ; count_assign++)
				 {
					 if(temp_subject_assignmentName[section_s][count_assign] != null)
					 {
						 System.out.println("���� �� = "+temp_subject_assignmentName[section_s][count_assign]);
						 System.out.println("���� ���� = "+temp_subject_assignmentPeriond[section_s][count_assign]);
						 System.out.println("���� ���� ���� = "+check_assignment[section_s][count_assign]);
						 System.out.println();
					 }
				 }
				 System.out.println("======================================================================");
			 }
		 }
		 
		 
		 
		 public static void is_login() throws IOException
		 {
			 int count_login = 1;
			 while(true)
			 {
				 login();
				 Document login_check = Jsoup.connect(smart_campus_URL)
							.cookies(cookies)
							.get();
				 String log = login_check.toString();
//				 System.out.println(log);
				 String check_str =  "���̵� / ��й�ȣ ã��";
				 int check_str_index = log.indexOf(check_str);
				 if(check_str_index == -1)
				 {
					 break;
				 }
				 System.out.println("���̵�/��й�ȣ�� �߸� �Է��ϼ̽��ϴ�. �ٽ� �Է����ֽʽÿ�.");
				 System.out.println("login �õ� = "+count_login);
				 System.out.println();
				 count_login++;
			 }
		 }
}

 
 
 /*����Ʈķ�۽� �ð� ������ ���� SimpleDateFormat��ü�� ����� ���� (2020-11-14 09:56:57) ���·� ���� ���ֱ� ������.*/
 /*Java API ���� ���� java.text.SimpleDateFormat */
 
 //����Ʈķ�۽� ���� ��ü (SmartCampus������ü) _ 2020.11.14 _20190511
class SmartCampusTokenizer extends SmartCampus
{
	//����ð� ����
	public static String current_time;
	//if_notattendent_week_video[video_name] or [video_date] or [video_Late]
	/*2020-11-18 SmartCampusOption �� ��ӹޱ� ���� private->protected ���� ������ Ȯ��*/
	protected static String if_notattendent_week_videoSubject[];
	protected static String if_notattendent_week_videoName[];
	protected static String if_notattendent_week_videoDate[];
	protected static String if_notattendent_week_videoLate[];
	protected static String if_notattendent_week_videoLength[];
	
	/*���� �����⸸ �ٷ�*/
	protected static String if_notPassed_AssignedSubject[];
	protected static String if_notPassed_AssignedName[];
	protected static String if_notPassed_AssignedDate[];
	
	/*2020 11.18 �߰��� ����*/ 
	//���� count ���� 1 ����  (count + 1)��  [���� print�Լ��� null���� ������ break�ϴ� ����̹Ƿ� �迭 �� ĭ�� null�� ������]
	protected static int count_notWatching_video;
	protected static int count_notPassed_assign;
	
	
	
	//Override�� �ƴϰ� �θ� ��ü ����
	public static void execute () throws IOException
	{
		SmartCampus.execute();
	}
	//ctrl + o ## �޼ҵ� �ʵ� ����Ű
	
	//���� �ð� ��ü
	public static void current_time()
	{
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		Date time = new Date();
		current_time = format1.format(time);
	}
	
	//�ð� ��ü��
	public static int year(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[0].split("-")[0]);
		return year;
	}
	
	public static int month(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[0].split("-")[1]);
		return year;
	}
	public static int date(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[0].split("-")[2]);
		return year;
	}
	public static int hour(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[1].split(":")[0]);
		return year;
	}
	public static int minute(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[1].split(":")[1]);
		return year;
	}
	public static int second(String time_obj)
	{
		String[] Date_time = new String[2];
		Date_time = time_obj.split(" ");
		int year = Integer.parseInt(Date_time[1].split(":")[2]);
		return year;
	}
	
	//2020-09-29 00:00:00 ~ 2020-10-05 23:59:59 �̷� ������ ��� ���� ��¥�� �м��ϱ�
	public static String video_date_tokenizer(String video_date)
	{
		String video_close_date = video_date.split("~")[1];
		video_close_date = video_close_date.trim();
		return video_close_date;
	}
	
	
	//���� �ð��� ������ ��¥ ���Լ� (�Ѱ踦 ���������� �Ǵ�.)
	public static boolean video_verse_current(String video_date , String video_late)
	{
		
		if(video_late == null)
		{
			if((month(current_time)<=month(video_date))&&(date(current_time)<=date(video_date)))
				return true;
		}
		else
		{
			if((month(current_time)<=month(video_late))&&(date(current_time)<=date(video_late)))
				return true;
		}
		return false;
	}
	
	//���� ��¥ Ȥ�� ������¥�� ���� �ð����� ���� �ִ� ��� -->
	public static void check_x_video_time()
	{
		//2020.11.18 => 100->count_notWatching_video (���� count_notPassed_assign�� ���� ������ �迭�� ����� ������ �迭ĭ�� ���� ���� ������)
		if_notattendent_week_videoName = new String [count_notWatching_video];
		if_notattendent_week_videoDate = new String [count_notWatching_video];
		if_notattendent_week_videoLate = new String [count_notWatching_video];
		if_notattendent_week_videoLength = new String [count_notWatching_video];
		if_notattendent_week_videoSubject = new String [count_notWatching_video];
		
		int count_video_check = 0;
		for(int i = 0 ; i < count ; i++)
		 {
			 for(int j = 0 ; j < 15 ; j++)
			 {
				 for(int k = 0 ; k < 15 ; k++)
				 {
					 if(subject_videoName[i][j][k] != null && check_video[i][j][k] != null)
					 {
						 
						 
						 /*
						System.out.println("����� ���� �̸� = "+subject_videoName[i][j][k]);
						System.out.println("����� ���� ���� ���� = "+subject_videoPeriod[i][j][k]);
						System.out.println("üŷ ��ũ  = "+check_video[i][j][k].trim());
						
							*/
						 
						 boolean offline = check_video[i][j][k].equals(" 100%");
						 boolean online =  check_video[i][j][k].equals("O");
//						 System.out.println("subject_videoName = "+subject_videoName[i][j][k]+", check="+check_video[i][j][k]);
						 if(offline == false && online == false)
						 {
//							System.out.println("count = "+count_video_check);
							String video_date = video_date_tokenizer(subject_videoPeriod[i][j][k]);
							boolean time_check = video_verse_current(video_date,subject_videoLate[i][j][k]);
							if(time_check == true)
							{
								if_notattendent_week_videoName[count_video_check] = subject_videoName[i][j][k];
								if_notattendent_week_videoDate[count_video_check] = video_date;
								if_notattendent_week_videoLate[count_video_check] = subject_videoLate[i][j][k];
								if_notattendent_week_videoLength[count_video_check] = subject_videoLength[i][j][k];
								if_notattendent_week_videoSubject[count_video_check] = subject_title[i];
								count_video_check++;
							}
						}
					 }
					
				 }
			 }
		 }
	}
	
	
	public static void print_notAttendent_video()
	{
		System.out.println("==========================================================================");
//		System.out.println("���� �ð��� ���Ǿ� ���� �⼮�� �� �ִ� ������ ��� ");
//		System.out.println("���� �ð� =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(if_notattendent_week_videoName[count_print] == null)
			{
				if(count_print == 0)
					System.out.println("\n���� ���� �� �ִ� ���Ǵ� ��� û���Ͽ����ϴ�.\n");
				break;
			}
			else
			{
				System.out.println("count = "+(count_print+1));
				System.out.println("�ش� ���� ������  = "+if_notattendent_week_videoSubject[count_print]);
				System.out.println("���� �� = "+if_notattendent_week_videoName[count_print]);
				System.out.println("���� �Ⱓ  = "+if_notattendent_week_videoDate[count_print]);
				System.out.println("���� ����  = "+if_notattendent_week_videoLength[count_print]);
				if(if_notattendent_week_videoLate[count_print] != null)
					System.out.println("���� �Ⱓ  = "+if_notattendent_week_videoLate[count_print]);
				System.out.println();
			}
			count_print++;
		}
		System.out.println("==========================================================================");
	}
	
	
	/*���� üũ �Լ� */
	public static boolean assignment_verse_current(String assign_date)
	{

		if((month(current_time)<=month(assign_date))&&(date(current_time)<=date(assign_date)))
			return true;
		return false;
	}
	
	
	public static void check_x_assignment_time ()
	{
		//2020.11.18 => 100->count_notPassed_assign (���� count_notPassed_assign�� ���� ������ �迭�� ����� ������ �迭ĭ�� ���� ���� ������)
		if_notPassed_AssignedSubject = new String [count_notPassed_assign];
		if_notPassed_AssignedName = new String [count_notPassed_assign];
		if_notPassed_AssignedDate = new String[count_notPassed_assign];
		
		int count_assign_check = 0;
		for (int section_s = 0 ; section_s < count ; section_s++)
		 {
			 for(int count_assign = 0 ; count_assign < 40 ; count_assign++)
			 {
				 if(temp_subject_assignmentName[section_s][count_assign] != null)
				 {
					 /*
					 System.out.println("���� �� = "+temp_subject_assignmentName[section_s][count_assign]);
					 System.out.println("���� ���� = "+temp_subject_assignmentPeriond[section_s][count_assign]);
					 System.out.println("���� ���� ���� = "+check_assignment[section_s][count_assign]);
					 System.out.println();
					 */
					 boolean assign_check = check_assignment[section_s][count_assign].equals("������");
					 boolean time_check = assignment_verse_current(temp_subject_assignmentPeriond[section_s][count_assign]);
					 if(assign_check == true && time_check == true)
					 {
						if_notPassed_AssignedSubject[count_assign_check] = subject_title[section_s];
						if_notPassed_AssignedName[count_assign_check] = temp_subject_assignmentName[section_s][count_assign];
						if_notPassed_AssignedDate[count_assign_check] = temp_subject_assignmentPeriond[section_s][count_assign];
						count_assign_check++;
					 }
				 }
			 }
		 }
	}
	
	public static void print_notPassed_assignment()
	{
//		System.out.println("���� �ð��� ���Ǿ� ���� ������ �� �ִ� ������ ��� ");
//		System.out.println("���� �ð� =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(if_notPassed_AssignedName[count_print] == null)
			{
				if(count_print == 0)
					System.out.println("\n���� ������ �� �ִ� ������ ��� �����ϼ̽��ϴ�.\n");
				break;
			}
			else
			{
				System.out.println("count = "+(count_print+1));
				System.out.println("�ش� ���� ������  = "+if_notPassed_AssignedSubject[count_print]);
				System.out.println("���� �� = "+if_notPassed_AssignedName[count_print]);
				System.out.println("���� ����  = "+if_notPassed_AssignedDate[count_print]);
				System.out.println();
			}
			count_print++;
		}
	}
	
	/*2020.11.18 �߰��� �Լ� */
	//�迭 ũ�� ������ �����ֱ� ���� ������ �󸶳� �ʿ���ϴ��� üũ�ϴ� �Լ�
	public static void check_video_count()
	{

		count_notWatching_video = 1;
		for(int i = 0 ; i < count ; i++)
		 {
			 for(int j = 0 ; j < 15 ; j++)
			 {
				 for(int k = 0 ; k < 15 ; k++)
				 {
					 if(subject_videoName[i][j][k] != null && check_video[i][j][k] != null)
					 {				 
						 boolean offline = check_video[i][j][k].equals(" 100%");
						 boolean online =  check_video[i][j][k].equals("O");
//						 System.out.println("subject_videoName = "+subject_videoName[i][j][k]+", check="+check_video[i][j][k]);
						 if(offline == false && online == false)
						 {
//							System.out.println("count = "+count_video_check);
							String video_date = video_date_tokenizer(subject_videoPeriod[i][j][k]);
							boolean time_check = video_verse_current(video_date,subject_videoLate[i][j][k]);
							if(time_check == true)
							{
								count_notWatching_video++;
							}
						}
					 }
					
				 }
			 }
		 }
//		System.out.println("count assign = "+count_notWatching_video);
	}
	
	//���� ������� ���� count ����
	public static void check_assign_count ()
	{
		count_notPassed_assign = 1;		
		for (int section_s = 0 ; section_s < count ; section_s++)
		 {
			 for(int count_assign = 0 ; count_assign < 40 ; count_assign++)
			 {
				 if(temp_subject_assignmentName[section_s][count_assign] != null)
				 {
					 boolean assign_check = check_assignment[section_s][count_assign].equals("������");
					 boolean time_check = assignment_verse_current(temp_subject_assignmentPeriond[section_s][count_assign]);
					 if(assign_check == true && time_check == true)
					 {
						 count_notPassed_assign++;
					 }
				 }
			 }
		 }
	}
	
	
	
	public static void execute_tokenizer() throws IOException
	{
		//Login �߸� �Է����� �� ����.
		System.out.println("======================================================================");
		is_login();
		System.out.println("======================================================================");
		System.out.println("A. �α��� ��....");
		access_lecture_index();
		System.out.println("======================================================================");
		System.out.println("B. ���� ��� Ȯ�� ��(���� ! ����Ʈ ķ�۽����� ������ �س��� ��� ��Ÿ���� ����)....");
		System.out.println("======================================================================");
		execution_crawling();
		System.out.println("======================================================================");
		System.out.println("C. ���� , ���� ������Ʈ �� ....");
		System.out.println("======================================================================");
		video_assignment_divide();
//		return_assign_attendance();
		
		//���� -> ���߿� �𺧷ӿ��� ������ ���� �� ������ �ϴ� ������ �ٷ��.
		scanner_Close();
		
		//SmartCampus_tokenzier
		System.out.println("\n");
		System.out.println("======================================================================");
		current_time();
		System.out.println("=                     ���� �ð� : "+current_time+"                    =");
		System.out.println("======================================================================");
		System.out.println("\n");
		
		//���� ��û���� ���� ���� ���� count�Լ�.
		check_video_count();
		//���� �������� ���� ���� ���� count�Լ�.
		check_assign_count();
		
		
		System.out.println("======================================================================");
		System.out.println("D. ���� �ð��� ���Ͽ� ���� �⼮���� ���� ���� ���");
		System.out.println("======================================================================");
		check_x_video_time();
		print_notAttendent_video();
		
		System.out.println("======================================================================");
		System.out.println("E. ���� �ð��� ���Ͽ� ���� ������� ���� ���");
		System.out.println("======================================================================");
		check_x_assignment_time();
		print_notPassed_assignment();
	}
}


public class SmartCampusMain {
	public static void main(String[] args) throws IOException {
//		SmartCampus.execute_debug(0);
//		SmartCampus.execute();
		
		
//		SmartCampusTokenizer.execute();
//		SmartCampusTokenizer.current_time();
		SmartCampusTokenizer.execute_tokenizer();

	}

}
