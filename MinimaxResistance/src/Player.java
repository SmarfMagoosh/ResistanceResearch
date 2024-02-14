public class Player {
    //public boolean isResistance;
    public int num;



    public Player(int num) {
        //isResistance = resistance;
        this.num = num;
        //knownResistance = false;
        //knownSpy = false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }
        return (this.num == ((Player)o).num);
    }
}
