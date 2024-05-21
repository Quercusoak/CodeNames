package DTO;

import engine.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamsList {
    public TeamsList(List<Team> teams){
        teamList =new ArrayList<>();
        teams.forEach(t-> teamList.add(new DTOTeam(t)));
    }

    private List<DTOTeam> teamList;
    public List<DTOTeam> getTeamList(){
        return teamList;
    }
}
