import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.List;

public class Trade
{
    private static final int CAP = 70000000;
    private static final int LUXURY = 76829000;

    public static List<Team> load_teams(HSSFSheet teams_, HSSFSheet players_, HSSFSheet contracts)
    {
        List<Team> teams = new ArrayList<>();

        for (int i = 1; i < teams_.getLastRowNum(); i++)
        {
            Row row = teams_.getRow(i);

            String city = row.getCell(1).getStringCellValue();
            String name = row.getCell(2).getStringCellValue();
            String abrv = row.getCell(3).getStringCellValue();

            double fg = row.getCell(6).getNumericCellValue();
            double fga = row.getCell(7).getNumericCellValue();
            double fg_p = row.getCell(8).getNumericCellValue();

            double three_fg = row.getCell(9).getNumericCellValue();
            double three_fga = row.getCell(10).getNumericCellValue();
            double three_fg_p = row.getCell(11).getNumericCellValue();

            List players = make_players(players_, contracts, abrv);

            Team t = new Team(city, name, abrv,
                    fg, fga, fg_p,
                    three_fg, three_fga, three_fg_p, players);
            teams.add(t);
        }
        return teams;
    }

    public static List make_players(HSSFSheet players_, HSSFSheet contracts, String arbv)
    {
        List playerz = new ArrayList<Player>();

        for (int i = 1; i < 412; i++)
        {
            Row row = contracts.getRow(i);

            if (arbv.equals(row.getCell(4)
                    .getStringCellValue()))
            {
                String name = row.getCell(1).getStringCellValue();
                String first = row.getCell(2).getStringCellValue();
                String last = row.getCell(3).getStringCellValue();

                String team_ab = row.getCell(4).getStringCellValue();

                double sal = row.getCell(5).getNumericCellValue();

                String pos = get_pos(name, players_);
                double age = get_age(name, players_);

                Player p = new Player(first, last, team_ab, pos, age, sal);
                playerz.add(p);
            }
        }
        return playerz;
    }

    public static String get_pos(String name, HSSFSheet players_)
    {
        for (int i = 1;  i < 415 ; i++)
        {
            Row row = players_.getRow(i);

            if(name.equals(row.getCell(1)
                    .getStringCellValue()))
            {
                return row.getCell(4).getStringCellValue();
            }
        }
        return null;
    }

    public static double get_age(String name, HSSFSheet players_)
    {
        for (int i = 1; i < 415 ; i++)
        {
            Row row = players_.getRow(i);

            if(name.equals(row.getCell(1)
                    .getStringCellValue()))
            {
                return row.getCell(5).getNumericCellValue();
            }
        }
        return 0;
    }

    public static ResultPair trade(List<Player> player_list, List<Team> trade_teams)
    {
        Team team1 = trade_teams.get(0);
        Team team2 = trade_teams.get(1);

        double s1 = 0;
        double s2 = 0;

        for(Player p: player_list)
        {
            if (p.team_ab.equals(team1.abrv))
            {
                s1 += p.sal_2015;
            }
            else
            {
                s2 += p.sal_2015;
            }
        }

        int  team1_over_cap = find_over_cap(team1, s2, s1);
        int  team2_over_cap = find_over_cap(team2, s1, s2);

        Pair team1_ok = team_ok(team1, team1_over_cap, s2, s1);
        Pair team2_ok = team_ok(team2, team2_over_cap, s1, s2);


        if (team1_ok.bool && team2_ok.bool)
        {
            return new ResultPair(null, new Pair(true, 0));
        }
        else if(team1_ok.bool == false)
        {
            return new ResultPair(team1, team1_ok);
        }
        else
        {
            return new ResultPair(team2, team2_ok);
        }
    }

    public static int find_over_cap(Team t, double in, double out)
    {
        double payroll = t.payroll + in - out;
        if (payroll - CAP <= 100000)
        {
            return -1;           /* UNDER CAP*/
        }
        else if (payroll < LUXURY)
        {
            return 0;           /* UNDER TAX */
        }
        else
        {
            return 1;           /* OVER TAX */
        }
    }

    public static Pair team_ok(Team t, int over_cap, double in, double out)
    {
        if (over_cap == -1)               /* UNDER CAP */
        {
            return new Pair(true, 0);
        }

        else if (over_cap == 0)           /* UNDER TAX */
        {
            double max1 = 1.5*out + 100000;
            double max2 = out + 5000000;
            if (max1 < max2)
            {
                if (in < max1)
                {
                    return new Pair(true, 0);
                }
                else
                {
                    return new Pair(false, in - max1);
                }
            }
            else
            {
                if(in < max2)
                {
                    return new Pair(true, 0);
                }
                else
                {
                    return new Pair(false, in - max2);
                }
            }
        }

        else                                 /* OVER TAX */
        {
            double max = 1.25*out + 100000;
            if (in < max)
            {
                return new Pair(true, 0);
            }
            else
            {
                return new Pair(false, in - max);
            }
        }
    }
}
