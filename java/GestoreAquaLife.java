import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

class GestoreAquaLife {
    private List<Stabilimento> stabilimenti = new ArrayList<>();
    private List<Piscina> piscine = new ArrayList<>();
    private List<Lavoratore> lavoratori = new ArrayList<>();
    private List<Cliente> clienti = new ArrayList<>();
    private List<Corso> corsi = new ArrayList<>();
    private List<Manutenzione> manutenzioni = new ArrayList<>();
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    public void aggiungiStabilimento(Stabilimento s) {
        stabilimenti.add(s);
    }
    public void aggiungiPiscina(Piscina p) {
        piscine.add(p);
    }
    public void aggiungiLavoratore(Lavoratore l) {
        lavoratori.add(l);
    }
    public void aggiungiCliente(Cliente c) {
        clienti.add(c);
    }
    public void aggiungiCorso(Corso c) {
        corsi.add(c);
    }
    public void aggiungiManutenzione(Manutenzione m) {
        manutenzioni.add(m);
    }

    public void aggiungiPrenotazione(Prenotazione pr) throws PrenotazioneException {
       boolean inManutenzione = manutenzioni.stream()
            .filter(m -> m.piscina().equals(pr.corso().piscina()))
            .anyMatch(m -> pr.dataInizio().isBefore(m.fine()) && pr.dataFine().isAfter(m.inizio()));
        if (inManutenzione) {
            throw new PrenotazioneException("La piscina è in manutenzione durante il periodo richiesto.");
        }

        boolean istruttoreOccupato = prenotazioni.stream()
            .filter(pren -> pren.corso().istruttore().equals(pr.corso().istruttore()))
            .filter(pren -> !pren.equals(pr)) // Escludi la prenotazione corrente
            .anyMatch(pren -> !pren.sovrappone(pr.dataInizio(), pr.dataFine()));
        if (istruttoreOccupato) {
            throw new PrenotazioneException("L'istruttore è occupato durante il periodo richiesto.");
        }

        prenotazioni.add(pr);
    }


    public Map <TipoCliente, Long> contaClientiPerTipo() {
         return clienti.stream()
            .collect(Collectors.groupingBy(Cliente::tipo, Collectors.counting()));
    }

    public List<Lavoratore> istruttoriLiberi(LocalDateTime inizio, LocalDateTime fine){

        Set<String> occupatiIds = prenotazioni.stream()
            .filter(pr -> pr.sovrappone(inizio, fine))
            .map(pr -> pr.corso().istruttore().id())
            .collect(Collectors.toSet());


        return lavoratori.stream()
            .filter(l -> l.ruolo() == RuoloLavoratore.Istruttore)
            .filter(l -> !occupatiIds.contains(l.id()))
            .collect(Collectors.toList());
    }

    public Map<Lavoratore, Double> calcolaIntroitiPerIstruttore(YearMonth mese){
        return prenotazioni.stream()
            .filter(pr -> YearMonth.from(pr.dataInizio()).equals(mese))
            .collect(Collectors.groupingBy(pr -> pr.corso().istruttore(),
                    Collectors.summingDouble(pr -> pr.corso().prezzo())));
    }

    public Optional <Piscina> piscinaProblematica (Stabilimento s){
        return manutenzioni.stream()
            .filter (m -> m.piscina().stabilimento().equals(s))
            .collect(Collectors.groupingBy(Manutenzione::piscina, Collectors.summingLong(Manutenzione::getDurataOre)))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey);
    }
}

