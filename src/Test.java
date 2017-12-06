import java.util.Date;
import java.text.*;
public class Test {
	public static void main(String args[])
	{
		DateFormat df = new SimpleDateFormat("HH:mm");
		System.out.print(df.format(new Date()));
	}
}
