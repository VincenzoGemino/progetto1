import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class GestorePiscineTest {
    public static void main(String[] args) {
        System.out.println("--- AVVIO TEST GESTIONALE AQUALIFE ---");
        GestoreAquaLife gestore = new GestoreAquaLife();

        // --- SETUP DATI ---
        Stabilimento s1 = new Stabilimento("S1", "AquaLife Centro", "Via Roma 1");
        gestore.aggiungiStabilimento(s1);

        Piscina p1 = new Piscina("P1", "Olimpionica", s1);
        Piscina p2 = new Piscina("P2", "Bambini", s1);
        gestore.aggiungiPiscina(p1); 
        gestore.aggiungiPiscina(p2);
        
        Lavoratore istr1 = new Lavoratore("L1", "Mario Rossi", RuoloLavoratore.Istruttore);
        Lavoratore istr2 = new Lavoratore("L2", "Luigi Verdi", RuoloLavoratore.Istruttore);
        gestore.aggiungiLavoratore(istr1); 
        gestore.aggiungiLavoratore(istr2);
        
        Cliente c1 = new Cliente("C1", "Studente Uno", TipoCliente.Universitario);
        Cliente c2 = new Cliente("C2", "Studente Due", TipoCliente.Universitario);
        Cliente c3 = new Cliente("C3", "Famiglia Bianchi", TipoCliente.Gruppo);
        gestore.aggiungiCliente(c1); 
        gestore.aggiungiCliente(c2); gestore.aggiungiCliente(c3);

        Corso cNuoto = new Corso("CR1", "Nuoto Avanzato", p1, istr1, 50.0);
        Corso cAcquaGym = new Corso("CR2", "AcquaGym", p2, istr2, 30.0);
        gestore.aggiungiCorso(cNuoto); 
        gestore.aggiungiCorso(cAcquaGym);

        LocalDateTime oraInizio = LocalDateTime.of(2023, 10, 20, 10, 0);
        LocalDateTime oraFine = LocalDateTime.of(2023, 10, 20, 11, 0);

        // --- TEST 1: ECCEZIONI E MANUTENZIONE ---
        System.out.println("\nTest 1: Verifica Eccezione Manutenzione");
        gestore.aggiungiManutenzione(new Manutenzione("M1", p1, oraInizio.minusHours(1), oraInizio.plusMinutes(30), "Pulizia filtri"));
        
        Prenotazione prenErrore = new Prenotazione("PR_ERR", c1, cNuoto, oraInizio, oraFine);
        try {
            gestore.aggiungiPrenotazione(prenErrore);
            System.err.println("FALLITO: Eccezione non lanciata!");
        } catch (PrenotazioneException e) {
            System.out.println("SUPERATO: " + e.getMessage());
        }

        // Aggiunta di prenotazioni valide (Nessuna eccezione attesa)
        try {
            LocalDateTime orarioValidoInizio = LocalDateTime.of(2023, 10, 21, 15, 0);
            LocalDateTime orarioValidoFine = LocalDateTime.of(2023, 10, 21, 16, 0);
            gestore.aggiungiPrenotazione(new Prenotazione("PR1", c1, cNuoto, orarioValidoInizio, orarioValidoFine));
            gestore.aggiungiPrenotazione(new Prenotazione("PR2", c2, cNuoto, orarioValidoInizio, orarioValidoFine));
            gestore.aggiungiPrenotazione(new Prenotazione("PR3", c3, cAcquaGym, orarioValidoInizio, orarioValidoFine));
            System.out.println("SUPERATO: Prenotazioni valide aggiunte correttamente.");
        } catch (Exception e) {
            System.err.println("FALLITO: Errore inatteso - " + e.getMessage());
        }

        // --- TEST 2: CONTEGGIO TIPOLOGIA CLIENTI ---
        System.out.println("\nTest 2: Conteggio Tipologia Clienti");
        Map<TipoCliente, Long> conteggio = gestore.contaClientiPerTipo();
        if (conteggio.getOrDefault(TipoCliente.Universitario, 0L) == 2 && 
            conteggio.getOrDefault(TipoCliente.Gruppo, 0L) == 1) {
            System.out.println("SUPERATO. Risultato: " + conteggio);
        } else {
            System.err.println("FALLITO. Risultato: " + conteggio);
        }

        // --- TEST 3: INTROITI PER ISTRUTTORE ---
        System.out.println("\nTest 3: Introiti per Istruttore");
        Map<Lavoratore, Double> introiti = gestore.calcolaIntroitiPerIstruttore(YearMonth.of(2023, 10));
        if (introiti.get(istr1) == 100.0 && introiti.get(istr2) == 30.0) {
            System.out.println("SUPERATO. (Mario: 100.0, Luigi: 30.0)");
        } else {
            System.err.println("FALLITO. Risultato: " + introiti);
        }

        // --- TEST 4: PISCINA PIÙ PROBLEMATICA ---
        System.out.println("\nTest 4: Piscina più problematica");
        gestore.aggiungiManutenzione(new Manutenzione("M2", p1, oraInizio.plusDays(1), oraInizio.plusDays(1).plusHours(5), "Guasto tubi")); 
        Optional<Piscina> peggiore = gestore.piscinaProblematica(s1);
        if (peggiore.isPresent() && peggiore.get().id().equals("P1")) {
            System.out.println("SUPERATO. Piscina problematica confermata: " + peggiore.get().nome());
        } else {
            System.err.println("FALLITO.");
        }
        System.out.println("\n--- FINE TEST ---");
    }
}