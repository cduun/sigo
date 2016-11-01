package mx.org.bamx.sigo;

/**
 * This class is the javabean of the socio-economic questionnaire.
 */
public class SocioEconomicStudy {

    private Integer id;
    private String community;
    private String name;
    private int hungryInBed;
    private String skippedMeals;

    public SocioEconomicStudy () {
    }

    public SocioEconomicStudy(String community, String name, int hungryInBed, String skippedMeals) {
        this.community = community;
        this.name = name;
        this.hungryInBed = hungryInBed;
        this.skippedMeals = skippedMeals;
    }

    public SocioEconomicStudy(int id, String community, String name, int hungryInBed, String skippedMeals) {
        this.id = id;
        this.community = community;
        this.name = name;
        this.hungryInBed = hungryInBed;
        this.skippedMeals = skippedMeals;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public int isHungryInBed() {
        return hungryInBed;
    }

    public void setHungryInBed(int hungryInBed) {
        this.hungryInBed = hungryInBed;
    }

    public String getSkippedMeals() {
        return skippedMeals;
    }

    public void setSkippedMeals(String skippedMeals) {
        this.skippedMeals = skippedMeals;
    }

}
