package eva.logik;

import com.eva.backend.gameApi.model.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramData implements Serializable {
    // Vigtigt: Sæt versionsnummer så objekt kan læses selvom klassen er ændret!
    private static final long serialVersionUID = 12345; // bare et eller andet nr.

    public ArrayList<Game> games = new ArrayList<Game>();
    public ArrayList<Game> onlineGames = new ArrayList<Game>();
}