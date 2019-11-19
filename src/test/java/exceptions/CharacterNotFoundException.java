package exceptions;

public class CharacterNotFoundException extends Exception {

    public CharacterNotFoundException(String character) {
        super(String.format("Character '%s' not found", character));
    }
}
