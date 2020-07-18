import java.awt.Color;
import java.util.EnumSet;
import java.util.LinkedList;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;

public class CommandListener extends ListenerAdapter
{
	private LinkedList<String> channels;
	private EnumSet<Permission> allowed_perms;
	
	public CommandListener()
	{
		channels = new LinkedList<String>();
		
		allowed_perms = EnumSet.of(Permission.MESSAGE_READ,
				Permission.MESSAGE_WRITE,
				Permission.MESSAGE_HISTORY,
				Permission.MESSAGE_ATTACH_FILES);
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e)
	{
		String msg = e.getMessage().getContentRaw();
		String[] args = msg.split(" ");
		
		//>discuss
		if(args[0].equalsIgnoreCase(Main.PREFIX + "discuss") && Utils.hasCommunityLeader(e.getMember()))
		{
			//>discuss @pinged @users
			if(e.getMessage().getMentionedUsers().size() >= 2)
			{
				e.getGuild().createTextChannel("discussion-room").queue((channel) ->
				{
					channels.add(channel.getId());
					ChannelManager cm = channel.getManager();
					cm = cm.setParent(e.getGuild().getCategoryById(Main.category))
						.setTopic("Remember to keep it civil- have a pleasant discussion :)")
						.putPermissionOverride((IPermissionHolder) e.getGuild().getPublicRole(), null, allowed_perms)
						.putPermissionOverride((IPermissionHolder) e.getMember(), allowed_perms, null)
						.putPermissionOverride((IPermissionHolder) e.getMember(), EnumSet.of(Permission.MANAGE_PERMISSIONS), null);
					
					for(Member m : e.getMessage().getMentionedMembers())
						cm = cm.putPermissionOverride((IPermissionHolder) m, allowed_perms, null);
					
					cm.queue();
				});
			}
			else
				e.getChannel().sendMessage("Too few people were pinged!").queue();
			
			return;
		}
		
		//>close
		if(msg.equalsIgnoreCase(Main.PREFIX + "close") && Utils.hasCommunityLeader(e.getMember()))
		{
			for(int i = 0; i < channels.size(); i++)
			{
				//if in a valid channel
				if(e.getChannel().getId().equals(channels.get(i)))
				{
					channels.remove(i);
					e.getChannel().delete().queue();
					break;
				}
					
			}
			
			return;
		}
		
		if(msg.equalsIgnoreCase(Main.PREFIX + "discussionhelp") && Utils.hasCommunityLeader(e.getMember()))
		{
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("**__Discussion Commands__**");
			eb.setDescription("***These commands only work if you have the " 
					+ e.getGuild().getRoleById(Main.community_leader).getName() + " role.***");
			
			eb.appendDescription("\n\n***" + Main.PREFIX + "discuss @ping @these @users***: Creates a private "
					+ "channel for the pinged users to discuss a topic privately, moderated by the person "
					+ "issuing the command.");
			
			eb.appendDescription("\n\n***" + Main.PREFIX + "close***: When used in a private discussion channel by a "
					+ e.getGuild().getRoleById(Main.community_leader).getName() 
					+ ", will close this discussion, deleting the channel.");
			
			eb.appendDescription("\n\n***" + Main.PREFIX + "discussionhelp***: Displays this message.");
			eb.appendDescription("\n\n***" + Main.PREFIX + "discussionstop***: (Used only by the server-owner) stops the program entirely.");
			
			eb.setColor(new Color(3, 119, 252));
			
			e.getChannel().sendMessage(eb.build()).queue();
			
			return;
		}
		
		//>stop
		if(msg.equalsIgnoreCase(Main.PREFIX + "discussionstop") && e.getMember().isOwner())
		{
			Main.jda.shutdown();
			System.exit(0);
		}
	}
}
