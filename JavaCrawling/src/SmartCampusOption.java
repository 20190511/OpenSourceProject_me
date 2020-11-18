import java.io.IOException;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class SmartCampusOption extends SmartCampusTokenizer {

	private static String[] assign_cal_date;
	private static String[][] video_cal_date; //video_cal_date[count][0] = �����ð� , video_cal_date[count][1] = �����ð�.

	// 24�ð� ���� �˷��� (���� �ð��� 0�÷� �����ص�) -> ���� �ð��� ������ 23:59�� �־� ������ �߻��� �� ����.
	// �̷��� �ð� ���� �Լ��� ���� ����.
	public static String calulate_date_function(String date_smartCampus) throws IOException {
		String cal_format = date_smartCampus;
//		System.out.println(cal_format);
		int year = year(cal_format);
		int month = month(cal_format);
		int date = date(cal_format) - 1;

		// 30�� 31�� ����.

		// 1�� ������ 0���� �Ǹ�
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

			// ���� + 2���� 28���Դϴ�.
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

	public static void assgin_calulation_date() throws IOException 
	{
		assign_cal_date = new String [count_notPassed_assign];
		for(int i = 0 ; i < count_notPassed_assign-1 ; i++)
		{
			String cal_data = if_notPassed_AssignedDate[i];
			assign_cal_date[i] = calulate_date_function(cal_data);
		}
	}
	
	public static void video_calulation_date() throws IOException 
	{
		video_cal_date = new String [count_notWatching_video][2];
		for(int i = 0 ; i < count_notWatching_video-1 ; i++)
		{
			String cal_data = if_notattendent_week_videoDate[i];
			video_cal_date[i][0] = calulate_date_function(cal_data);
			if(if_notattendent_week_videoLate[i] == null)
			{
				//���� ��¥�� ������ ���� ������ ������ �־��
				video_cal_date[i][1] = cal_data;
			}
			else
			{
				String cal_data_late = if_notattendent_week_videoLate[i];
				video_cal_date[i][1] = calulate_date_function(cal_data_late);
			}
		}
	}


	
	//
	public static void print_noPassing_dateMinus1()
	{
//		System.out.println("���� �ð��� ���Ǿ� ���� ������ �� �ִ� ������ ��� ");
//		System.out.println("���� �ð� =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(assign_cal_date[count_print] == null)
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
				System.out.println("���� ����  - 1= "+assign_cal_date[count_print]);
				System.out.println();
			}
			count_print++;
		}
	}
	
	public static void print_notWataching_dateMinus1()
	{
		System.out.println("==========================================================================");
//		System.out.println("���� �ð��� ���Ǿ� ���� �⼮�� �� �ִ� ������ ��� ");
//		System.out.println("���� �ð� =: "+current_time);
		int count_print = 0;
		while(true)
		{
			if(video_cal_date[count_print][0] == null)
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
				System.out.println("���� �Ⱓ  = "+video_cal_date[count_print][0]);
				System.out.println("���� ����  = "+if_notattendent_week_videoLength[count_print]);
				if(if_notattendent_week_videoLate[count_print] != null)
					System.out.println("���� �Ⱓ  = "+video_cal_date[count_print][1]);
				System.out.println();
			}
			count_print++;
		}
		System.out.println("==========================================================================");
	}
	
	
	public static void execite_options() throws IOException {
		execute_tokenizer();
		assgin_calulation_date();
		video_calulation_date();
		System.out.println("======================================================================");
		System.out.println("F. ���� ���� ���� �� �Ϸ� ������ �����.");
		System.out.println("======================================================================");
		print_notWataching_dateMinus1();
		
		System.out.println("======================================================================");
		System.out.println("F-2. �������� ���� ���� �� �Ϸ� ������ �����.");
		System.out.println("======================================================================");
		print_noPassing_dateMinus1();
	}
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		execite_options();

	}

}
