import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class Utils
{	
	public static boolean hasCommunityLeader(Member m)
	{
		if(m.isOwner())
			return true;
		
		for(Role r : m.getRoles())
		{
			if(r.getId().equals(Main.community_leader))
				return true;
		}
		
		return false;
	}
}
