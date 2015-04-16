package eva.logik;

import com.eva.backend.gameApi.model.Game;

import java.io.Serializable;
import java.util.ArrayList;

public class ProgramData extends com.google.api.client.json.GenericJson {

    @com.google.api.client.util.Key
    public ArrayList<Game> games = new ArrayList<Game>();
    @com.google.api.client.util.Key
    public ArrayList<Game> onlineGames = new ArrayList<Game>();
}