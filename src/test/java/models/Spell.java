package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("__v")
public class Spell {

    private String id;
    private String spell;
    private String effect;
    private String type;

    @JsonGetter(value = "_id")
    public String getId() {
        return id;
    }

    public String getSpell() {
        return spell;
    }

    public String getEffect() {
        return effect;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Kuzlo {zavolas ho takto='" + spell + "', sposobi ='" + effect + '}';
    }
}
