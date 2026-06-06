import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

//enumerazione

enum TipoCliente{ Singolo, Gruppo, Scolastico, Universitario};
enum RuoloLavoratore{Addetto, Istruttore};

record Stabilimento(String id, String nome, String indirizzo){};
record Piscina (String id, String nome, Stabilimento stabilimento){};
record Cliente (String id, String anagrafica, TipoCliente tipo){};
record Lavoratore(String id, String anagrafica, RuoloLavoratore ruolo){};

record Corso(String id, String nome, Piscina piscina, Lavoratore istruttore, double prezzo){};

record Manutenzione(String id, Piscina piscina, LocalDateTime inizio, LocalDateTime fine, String descrizione){
        public long getDurataOre(){
         return Duration.between(inizio, fine).toHours();
        }
};

record Prenotazione(String id, Cliente cliente, Corso corso, LocalDateTime dataInizio, LocalDateTime dataFine){
     public boolean sovrappone(LocalDateTime start, LocalDateTime end){
        return start.isBefore(dataFine) && end.isAfter(dataInizio);
     }
};

class PrenotazioneException extends Exception {
    public PrenotazioneException(String message) {
        super(message);
    }
}
