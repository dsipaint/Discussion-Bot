import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main
{
	static JDA jda;
	static final String PREFIX = ">";
	static final String token = ""; //put your bot's token in the quotation marks
	static final String community_leader = ""; //put the community leader or whatever role id in the quotation marks
	static final String category = ""; //put the id of the category a channel will be made in here
	
	public static void main(String[] args)
	{
		try
		{
			jda = new JDABuilder(AccountType.BOT).setToken(token).build();
		}
		catch (LoginException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			jda.awaitReady();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jda.addEventListener(new CommandListener());
	}
}
