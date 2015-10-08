import java.util.List;

public class Team
{
    public String city;
    public String team;
    public String abrv;

    public double payroll;

    public double fg;
    public double fga;
    public double fg_p;

    public double three_fg;
    public double three_fga;
    public double three_fg_p;

    public List<Player> players;

    public Team(String city, String team, String abrv,
                double fg, double fga, double fg_p,
                double three_fg, double three_fg_a, double three_fg_p, List players)
    {
        this.city = city;
        this.team = team;
        this.abrv = abrv;

        this.fg = fg;
        this.fga = fga;
        this.fg_p = fg_p;

        this.three_fg = three_fg;
        this.three_fga = three_fg_a;
        this.three_fg_p = three_fg_p;

        this.players = players;

        this.payroll_init();
    }

    public Player get_player(String player_name)
    {
        for (Player p: players)
        {
            if (player_name.equals(p.last))
            {
                return p;
            }
        }
        return null;
    }

    public String get_full_name()
    {
        return this.city + ' ' + this.team;
    }

    private void payroll_init()
    {
        double sal = 0;
        for (Player p: this.players)
        {
            sal += p.sal_2015;
        }
        this.payroll = sal;
    }
}
