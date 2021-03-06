import java.io.IOException;
/*not used 떠 있지만 안쓰면 오류뜸 밑의 4개 꼭 써서 짜야됨*/
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SmartCampusOption extends SmartCampusTokenizer {

	private static String[] assign_cal_date;
	private static String[][] video_cal_date; //video_cal_date[count][0] = 원래시간 , video_cal_date[count][1] = 지각시간.
	
	/*2020.11.19 추가된 필드*/
	//남은 동영상 최종 길이
	private static String video_length_total;
	
	
	/*2020.12.02.추가된 필드*/
	private static int thisWeek;
	private static String thisweek_videoName[][]; //[과목명][강의 인덱스]
	private static String thisweek_videoPeriod[][]; //[과목명][강의 인덱스]
	private static String thisweek_videoLength[][]; //[과목명][강의 인덱스]
	private static String thisweek_videoLate[][]; //[과목명][강의 인덱스]
	private static String thisweek_checkVideo[][]; //강의를 들었는지 체크하는 함수
	
	
	
	// 24시간 전에 알려줌 (뒤의 시간은 0시로 고정해둠) -> 원래 시간을 따르면 23:59가 있어 문제가 발생할 수 있음.
	// 미래에 시간 조절 함수도 넣을 생각.
	public static String calulate_date_function(String date_smartCampus) throws IOException {
		String cal_format = date_smartCampus;
//		System.out.println(cal_format);
		int year = year(cal_format);
		int month = month(cal_format);
		int date = date(cal_format) - 1;

		// 30일 31일 검증.

		// 1을 뺐더니 0일이 되면
		if (date == 0) {
			month -= 1;
			if (month < 7)
				if (month % 2 == 1)
					date = 31;
				else
					date = 30;
			if (month >= 8)
				if (month % 2 == 0)
					date = 31;
				else
					date = 30;

			// 윤년 + 2월은 28일입니다.
			if (month == 2) {
				int test1 = year % 4;
				int test2 = year % 100;
				int test3 = year % 400;
				if (test1 == 0 && test2 != 0 || test3 == 0)
					date = 28;
				else
					date = 29;
			}
		}
//		String[] split_assigndata = test.split(" ");
		String calculated_date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(date)
				+ " 00:00";
//		System.out.println(calculated_date);
		return calculated_date;
	}

	
	//과제 남은 기한 -1을 계산하는 함수
	public static void assgin_calulation_date() throws IOException 
	{
		assign_cal_date = new String [count_notPassed_assign];
		for(int i = 0 ; i < count_notPassed_assign-1 ; i++)
		{
			String cal_data = if_notPassed_AssignedDate[i];
			assign_cal_date[i] = calulate_date_function(cal_data);
		}
	}
	
	//남은 비디오 기한 -1 일을 계산해주는 함수
	public static void video_calulation_date() throws IOException 
	{
		video_cal_date = new String [count_notWatching_video][2];
		for(int i = 0 ; i < count_notWatching_video-1 ; i++)
		{
			String cal_data = if_notattendent_week_videoDate[i];
			video_cal_date[i][0] = calulate_date_function(cal_data);
			if(if_notattendent_week_videoLate[i] == null)
			{
				//지각 날짜가 없으면 원래 동영상 기한을 넣어둠
				video_cal_date[i][1] = cal_data;
			}
			else
			{
				String cal_data_late = if_notattendent_week_videoLate[i];
				video_cal_date[i][1] = calulate_date_function(cal_data_late);
			}
		}
	}


	
	//기헌 -1 출력함수 (과졔)
	public static void print_noPassing_dateMinus1()
	{
//		System.out.println("현재 시각과 대비되어 아직 제출할 수 있는 과제의 경우 ");
//		System.out.println("현재 시각 =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(assign_cal_date[count_print] == null)
			{
				if(count_print == 0)
					System.out.println("\n현재 수행할 수 있는 과제는 모두 제출하셨습니다.\n");
				break;
			}
			else
			{
				System.out.println("count = "+(count_print+1));
				System.out.println("해당 과목 수업명  = "+if_notPassed_AssignedSubject[count_print]);
				System.out.println("과제 명 = "+if_notPassed_AssignedName[count_print]);
				System.out.println("과제 기한  - 1= "+assign_cal_date[count_print]);
				System.out.println();
			}
			count_print++;
		}
	}
	
	//기헌 -1 출력함수 (동영상)
	public static void print_notWataching_dateMinus1()
	{
		System.out.println("==========================================================================");
//		System.out.println("현재 시각과 대비되어 아직 출석할 수 있는 영상의 경우 ");
//		System.out.println("현재 시각 =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(video_cal_date[count_print][0] == null)
			{
				if(count_print == 0)
					System.out.println("\n현재 들을 수 있는 강의는 모두 청강하였습니다.\n");
				break;
			}
			else
			{
				System.out.println("count = "+(count_print+1));
				System.out.println("해당 과목 수업명  = "+if_notattendent_week_videoSubject[count_print]);
				System.out.println("과목 명 = "+if_notattendent_week_videoName[count_print]);
				System.out.println("수강 기간  = "+video_cal_date[count_print][0]);
				System.out.println("영상 길이  = "+if_notattendent_week_videoLength[count_print]);
				if(if_notattendent_week_videoLate[count_print] != null)
					System.out.println("지각 기간  = "+video_cal_date[count_print][1]);
				System.out.println();
			}
			count_print++;
		}
		System.out.println("==========================================================================");
	}

	/*남은 동영상을 들어야하는 시간*/
	//강의 시간이 10 아래인 것들은 6:4:3 이런식으로 표현되서 -> 06:04:03 이런식으로 바꿀 예정
	public static String check_format_ten_under(int time)
	{
		String time_format;
		if(time < 10)
		{
			time_format = "0"+Integer.toString(time);
		}
		else
		{
			time_format = Integer.toString(time);
		}
		return time_format;
	}
		
	//동영상의 남은 시간.
	public static String video_total_lenght()
	{
		int total_hour = 0;
		int total_minute = 0;
		int total_second = 0;
		
		for(int i = 0 ; i < count_notWatching_video-1 ;i++)
		{
			int hour = 0;
			int minute = 0;
			int second = 0;
			String[] split_video_length = if_notattendent_week_videoLength[i].split(":");
			if (split_video_length.length == 3)
			{
				hour = Integer.parseInt(split_video_length[0]);
				minute = Integer.parseInt(split_video_length[1]);
				second = Integer.parseInt(split_video_length[2]);
			}
			else if(split_video_length.length == 2)
			{
				minute = Integer.parseInt(split_video_length[0]);
				second = Integer.parseInt(split_video_length[1]);
			}
			else
			{
				second = Integer.parseInt(split_video_length[0]);
			}
			
			total_hour += hour;
			total_minute += minute;
			total_second += second;
			
//			System.out.println("+hour = "+hour+"\n+minute = "+minute+"\n+seconde = "+second);
		}
		
		if(total_second >= 60)
		{
			int upper_second = total_second/60;
			int save_second = total_second%60;
			total_minute += upper_second;
			total_second = save_second;
		}
		if(total_minute >= 60)
		{
			int upper_minute = total_minute/60;
			int save_minute = total_minute%60;
			total_hour += upper_minute;
			total_minute = save_minute;
		}
//		System.out.println("total hour = "+total_hour+"\n+total minute = "+total_minute+"\n+total seconde = "+total_second);
		String time_length = check_format_ten_under(total_hour)+":"+check_format_ten_under(total_minute)+":"+check_format_ten_under(total_second);	
		video_length_total = time_length;
		return time_length;
	}
	
	
	
	
	//실행함수.
	public static void execite_options() throws IOException {
		execute_tokenizer();
		assgin_calulation_date();
		video_calulation_date();
		System.out.println("======================================================================");
		System.out.println("F. 듣지 않은 강의 중 하루 전날을 출력함.");
		System.out.println("======================================================================");
		print_notWataching_dateMinus1();
		
		System.out.println("======================================================================");
		System.out.println("F-2. 제출하지 않은 강의 중 하루 전날을 출력함.");
		System.out.println("======================================================================");
		print_noPassing_dateMinus1();
		
		System.out.println("======================================================================");
		System.out.println("G. 남은 동영상 강의 길이 출력.");
		System.out.println("======================================================================");
		String len_video = video_total_lenght();
		System.out.println("남은 동영상 길이  = "+len_video);
	}
	
	
	
	
	/*2020 12 02 추가된 메소드*/
	//주차를 찾아주는 함수. 
	public static void find_thisWeek () throws IOException {
		Document doc_link = Jsoup.connect(subject_link[1])
				.cookies(cookies)
				.get();
		String doc_link_string = doc_link.toString();
//		System.out.println(doc_link_string);
		
		String this_week_checkPoint = "이번 주";
		String this_week_str_start = "<h3 class=\"sectionname\"><span>";
		String this_week_str_end = "주차";
		
		int this_week_idx_start = doc_link_string.indexOf(this_week_str_start);
		int this_week_idx_end = doc_link_string.indexOf(this_week_str_end , this_week_idx_start);
		String this_week_str = doc_link_string.substring(this_week_idx_start+this_week_str_start.length() , this_week_idx_end);
//		System.out.println(this_week_str);
		
		//이번 주차 정수값.
		thisWeek = Integer.parseInt(this_week_str);
	}
	
	
	//이번주차만 모아둔 객체
	public static void tokenizer_thisWeek()
	{
		thisweek_videoName = new String [count][15];
		thisweek_videoPeriod = new String [count][15];
		thisweek_videoLength = new String [count][15];
		thisweek_videoLate = new String [count][15];
		thisweek_checkVideo = new String [count][15];
		
		for(int subject = 0 ; subject < count ; subject++)
		{
			for (int data = 0 ; data < 15 ; data++)
			{
				thisweek_videoName[subject][data] = subject_videoName[subject][thisWeek][data];
				thisweek_videoPeriod[subject][data] = subject_videoPeriod[subject][thisWeek][data];
				thisweek_videoLength[subject][data] = subject_videoLength[subject][thisWeek][data];
				thisweek_videoLate[subject][data] = subject_videoLate[subject][thisWeek][data];
				thisweek_checkVideo[subject][data] = check_video[subject][thisWeek][data];
			}
			
		}
	}
	
	
	//이번 주차만 나온 객체 출력
	public static void print_thisWeekVideo()
	{
		int count_index_thisweek = 0;
		for(int subject = 0 ; subject < count ; subject++)
		{
			for (int data = 0 ; data < 15 ; data++)
			{
				if(thisweek_videoName[subject][data] != null)
				{
					System.out.println("Count = "+count_index_thisweek);
					System.out.println("과목 명 = "+thisweek_videoName[subject][data]);
					System.out.println("수강 기간  = "+thisweek_videoPeriod[subject][data]);
					System.out.println("영상 길이  = "+thisweek_videoLength[subject][data]);
					if(thisweek_videoLate[subject][data]  != null)
					{
						System.out.println("지각 기간(있는경우)  = "+thisweek_videoLate[subject][data] );
					}
					System.out.println("진도 확인(O/X or %) = "+thisweek_checkVideo[subject][data]);
					System.out.println();
					count_index_thisweek++;
				}
			}
			
		}
	}
	
	public static void execute_thisWeek () throws IOException{
		//Login 잘못 입력했을 때 방지.
		System.out.println("======================================================================");
		is_login();
		System.out.println("======================================================================");
		System.out.println("A. 로그인 중....");
		access_lecture_index();
		System.out.println("======================================================================");
		System.out.println("B. 강의 목록 확인 중(주의 ! 스마트 캠퍼스에서 숨김을 해놓은 경우 나타나지 않음)....");
		System.out.println("======================================================================");
		execution_crawling();
		System.out.println("======================================================================");
		System.out.println("C. 강좌 , 과제 업데이트 중 ....");
		System.out.println("======================================================================");
		video_assignment_divide();
		
		
		//-------------------------------------------------------------------------------------------
		//2020.12.02 이번 주차만 다루는 함수들
		find_thisWeek(); //이번주차를 알려줌 thisWeek이 이번주타를 가리킴 (멤버변수)
		tokenizer_thisWeek(); //이번 주차의 데이터만 뽑아오는 함수
		System.out.println("======================================================================");
		System.out.println("D. 이번 주차 강의 출력 예정 ....");
		System.out.println("======================================================================");
		print_thisWeekVideo();
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		execite_options();
		execute_thisWeek();
	}

}
