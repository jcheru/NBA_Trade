public class Player
{
    public String first;
    public String last;

    public String team_ab;

    public String pos;
    public double age;

    public double sal_2015;

    public Player(String first, String last, String team_ab, String pos, double age, double sal_2015)
    {
        this.first = first;
        this.last = last;
        this.team_ab = team_ab;
        this.pos = pos;
        this.age = age;
        this.sal_2015 = sal_2015;
    }

    public String get_full_name()
    {
        return this.first + ' ' + this.last;
    }
}
