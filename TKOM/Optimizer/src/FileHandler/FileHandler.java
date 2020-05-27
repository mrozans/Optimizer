package FileHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {

    private String[] lines;
    private int lineNumber;
    private int signPos;
    private int lineNumberPeak;
    private int signPosPeak;
    private boolean end;
    private boolean valid;

    public FileHandler(Path path) throws IOException {
        if(!Files.isReadable(path)){
            valid = false;
            return;
        }
        lines = Files.lines(path).toArray(String[]::new);
        valid = lines.length != 0;
        lineNumber = 0;
        signPos = 0;
        end = false;
    }

    public char nextSign(){
        if(end) return 0;
        char sign;
        if(signPos == lines[lineNumber].length()){
            if(++lineNumber == lines.length){
                end = true;
                return 0;
            }
            lineNumberPeak = lineNumber;
            signPos = 0;
            signPosPeak = 0;
            return nextSign();
        }
        sign = lines[lineNumber].charAt(signPos);
        signPos++;
        signPosPeak = signPos;
        return sign;
    }

    public char peak(){
        char sign;
        if(signPosPeak == lines[lineNumberPeak].length()) return '\n';
        sign = lines[lineNumberPeak].charAt(signPosPeak);
        signPosPeak++;
        return sign;
    }

    public void skip(){
        signPos = signPosPeak;
        lineNumber = lineNumberPeak;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getSignPos() {
        return signPos;
    }

    public boolean isValid() {
        return valid;
    }
}

