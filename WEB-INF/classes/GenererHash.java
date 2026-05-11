import org.mindrot.jbcrypt.BCrypt;

public class GenererHash {
    public static void main(String[] args) {
        String motDePasse = "admin123";
        String hash = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        System.out.println("Hash : " + hash);
        
        // Vérification
        System.out.println("Vérification : " + BCrypt.checkpw(motDePasse, hash));
    }
}