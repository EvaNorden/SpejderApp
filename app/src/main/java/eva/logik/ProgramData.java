package eva.logik;

import com.eva.backend2.gameApi.model.Game;
import com.eva.backend2.solutionApi.model.Solution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProgramData extends com.google.api.client.json.GenericJson {

    @com.google.api.client.util.Key
    public ArrayList<Solution> solutions = new ArrayList<Solution>();

    // Leder data
    @com.google.api.client.util.Key
    public ArrayList<Game> games = new ArrayList<Game>();
    @com.google.api.client.util.Key
    public ArrayList<Game> onlineGames = new ArrayList<Game>();

    // Spejder data
    @com.google.api.client.util.Key
    public List<Game> scoutGames = new ArrayList<Game>();
    @com.google.api.client.util.Key
    public Game activeGame = null;
}